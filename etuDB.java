import java.io.*;
import java.util.Scanner;

public class etuDB {
    private Table[] tables = new Table[1];
    private String[] tableNames = new String[1];

    /**
     * It provides an command prompt.
     * By printing exit, it can be quitted.
     */
    public void prompt() {
        String query = "";
        Scanner sc = new Scanner(System.in);
        System.out.print("etuDB>>");
        while ((query = sc.nextLine()) != null) {
            if (query.indexOf("SELECT") != -1) {
                print(queryParser(query));
            } else if (query.indexOf("CREATE") != -1) {
                createTable(query);
            } else if (query.indexOf("exit") != -1) {
                break;
            } else if (isThere(query)) {
                printSchema(query);
            } else {
                System.out.println("Illegal query or wrong table name or could not find table." +
                        "\nYou can print \"exit\" to quit.");
            }
            System.out.print("etuDB>>");
        }
    }

    /**
     * It provides to create a table.
     * If it is not suitable csv file (all row has same column number), throws an
     * exception.
     * If it was already uploaded with same name, switches with the previous file.
     * 
     * @param createTableCommand : the command that includes the file path.
     */
    public void createTable(String createTableCommand) {
        try {
            String csvFilePath = createTableCommand.substring(createTableCommand.lastIndexOf("FROM") + 5);
            BufferedReader br = new BufferedReader(new FileReader(csvFilePath));
            String tableName;

            // According to the path, it parses the query and gets the file name.
            if (csvFilePath.indexOf("/") != -1) {
                tableName = csvFilePath.substring(csvFilePath.lastIndexOf("/") + 1, csvFilePath.indexOf(".csv"));
            } else if (csvFilePath.indexOf("\\") != -1) {
                tableName = csvFilePath.substring(csvFilePath.lastIndexOf("\\") + 1, csvFilePath.indexOf(".csv"));
            } else {
                tableName = csvFilePath.substring(0, csvFilePath.indexOf(".csv"));
            }

            // If it is not a csv file..
            if (!isValid(csvFilePath)) {
                throw new IllegalStateException(
                        "Not valid file type. Upload a file that has the same column number at each row.");
            }

            // If the file already exists, it deletes the table objects.
            if (isThere(tableName)) {
                int i = findTable(tableName);
                tables[i] = null;
            } else if (isFull()) {// No need to check if that table already exists.
                expandTableArray();// After removing it, some places will be empty.
            }

            Table newTable = new Table(tableName, null);
            int i;
            for (i = 0; i < tables.length && tables[i] != null; i++)// Finding the index to insert the table
                ;
            tables[i] = newTable;
            tableNames[i] = tableName;

            String line = br.readLine(); // Reading the header of the file
            newTable.setColumnNames(line);// Saving the column names(from header of the file)

            Row oldRow, header = new Row(line, null);
            newTable.setHeader(header);
            oldRow = header;
            while ((line = br.readLine()) != null) {
                Row newRow = new Row(line, null);
                oldRow.setNext(newRow);
                oldRow = oldRow.getNext();
            }
            newTable.setBinarySearchTree();
            br.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * It parses the query and call the necessary methods.
     * 
     * @param query : The query sequence that user enters.
     * @return : the data array which includes desired information.
     */
    public String[] queryParser(String query) {
        String data = "";
        String[] dataArr = null;
        // It adds semicolon if there is not.
        if (query.indexOf(";") == -1) {
            query = query + ";";
        }
        try {
            // If the query has a condition:
            if (query.indexOf("WHERE") == -1) {

                String tableName;
                tableName = query.substring(query.indexOf("FROM") + 5, query.length() - 1);

                // It controls the columns are comma seperated or all columns or a single
                // column.
                if (query.indexOf(",") != -1) {// Just some columns
                    String[] columns = (query.substring(query.indexOf(" ") + 1, query.indexOf("FROM") - 1)).split(",");
                    dataArr = getSpecificColumns(tableName, columns);
                    return dataArr;
                } else if (query.indexOf("*") != -1) {// All columns
                    dataArr = getAllColumns(tableName);
                    return dataArr;
                } else {// Single column
                    String[] columns = (query.substring(query.indexOf(" ") + 1, query.indexOf("FROM") - 1)).split(",");
                    dataArr = getSpecificColumns(tableName, columns);
                    return dataArr;
                }
            } else {// If no condition:
                String tableName;
                String conditionStatement;

                tableName = query.substring(query.indexOf("FROM") + 5, query.indexOf("WHERE") - 1);
                conditionStatement = query.substring(query.indexOf("WHERE") + 6, query.length() - 1);

                // All columns
                if (query.indexOf("*") != -1) {
                    dataArr = getAllColumnsWithCondition(tableName, conditionStatement);
                    return dataArr;
                }
                // Single column or multiple columns
                else {
                    String[] columns = (query.substring(query.indexOf(" ") + 1, query.indexOf("FROM") - 1)).split(",");
                    dataArr = getSpecificColumnsWithCondition(tableName, columns, conditionStatement);
                    return dataArr;
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }

    /**
     * It returns all the columns for given table name
     * 
     * @param tableName : the name of the table
     * @return : the array of the rows
     * @throws IllegalStateException
     */
    public String[] getAllColumns(String tableName) throws IllegalStateException {
        int i = 0;
        try {
            i = findTable(tableName);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        Table table = tables[i];
        String data = "";
        Row row = table.getHeader();
        data += row.getData() + "\n";
        while (row.getNext() != null) {
            data += row.getNext().getData() + "\n";
            row = row.getNext();
        }
        return data.split("\n");
    }

    /**
     * It returns the specific columns.
     * 
     * @param tableName : the name of the table
     * @param columns   : the columns that desired by the user
     * @return : the array of the rows, each row has only necessary columns
     */
    public String[] getSpecificColumns(String tableName, String[] columns) {
        String data = "";
        int i;
        int[] columnsIndex;
        try {
            i = findTable(tableName);
            columnsIndex = findColumns(tables[i], columns);

        } catch (Exception e) {
            e.printStackTrace();
            return data.split(" ");
        }

        for (Row temp = tables[i].getHeader(); temp.getNext() != null; temp = temp.getNext()) {
            for (int j = 0; j < columnsIndex.length; j++) {
                data += temp.getData(columnsIndex[j]) + ",";
            }
            data = data.substring(0, data.length() - 1) + "\n";
        }
        return data.split("\n");
    }

    /**
     * It searches the specific columns with considering the condition.
     * 
     * @param tableName          : the name of the table
     * @param columns            : the columns that desired by the user
     * @param conditionStatement : the condition statement that user enters
     * @return : the array of the rows, each row provides the condition
     */
    public String[] getSpecificColumnsWithCondition(String tableName, String[] columns, String conditionStatement) {
        String data = "";
        int i;
        int[] columnsIndex;
        int conditionColumn = 0;

        try {
            i = findTable(tableName);
            columnsIndex = findColumns(tables[i], columns);
            conditionColumn = tables[i].whichColumn(conditionStatement.substring(0, conditionStatement.indexOf("=")));

        } catch (Exception e) {
            e.printStackTrace();
            return data.split(" ");
        }
        if (conditionColumn == 0)
            return getSpecificColumnsFromBST(tables[i], columnsIndex, conditionStatement);
        String condition = conditionStatement.substring(conditionStatement.indexOf("=") + 1);

        Row temp = tables[i].getHeader();
        for (int j = 0; j < columnsIndex.length; j++) {
            data += temp.getData(columnsIndex[j]) + ",";
        }
        if (data.length() != 0 && data.lastIndexOf(",") == data.length() - 1) {
            data = data.substring(0, data.length() - 1) + "\n";
        }

        for (; temp.getNext() != null; temp = temp.getNext()) {
            for (int j = 0; j < columnsIndex.length; j++) {
                if (temp.getData(conditionColumn).equals(condition))
                    data += temp.getData(columnsIndex[j]) + ",";
            }
            if (data.length() != 0 && data.lastIndexOf(",") == data.length() - 1) {
                data = data.substring(0, data.length() - 1) + "\n";
            }
        }
        return data.split("\n");
    }

    public String[] getAllColumnsWithCondition(String tableName, String conditionStatement) {
        int i;
        String[] columns;

        try {
            i = findTable(tableName);
            columns = tables[i].getHeader().getData().split(",");

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return getSpecificColumnsWithCondition(tableName, columns, conditionStatement);
    }

    /**
     * It searches the specific columns with a condition
     * Only if the condition statement quests the first column.
     * 
     * @param table              : the table object
     * @param columnsIndex       : the columns that should be returned
     * @param conditionStatement : the condition statement that user enters
     * @return : the array of the rows that provide the condition
     */
    public String[] getSpecificColumnsFromBST(Table table, int[] columnsIndex, String conditionStatement) {
        String condition = conditionStatement.substring(conditionStatement.indexOf("=") + 1);
        BinarySearchTree<Row>.Node node = (table.getBST().getData(condition));
        String data = "";

        Row temp = table.getHeader();
        for (int j = 0; j < columnsIndex.length; j++) {
            data += temp.getData(columnsIndex[j]) + ",";
        }
        if (data.length() != 0 && data.lastIndexOf(",") == data.length() - 1) {
            data = data.substring(0, data.length() - 1) + "\n";
        }

        for (; node != null; node = node.getNext()) {
            for (int j = 0; j < columnsIndex.length; j++) {
                data += node.getRow().getData(columnsIndex[j]) + ",";
            }
            data = data.substring(0, data.length() - 1) + "\n";
        }

        return data.split("\n");
    }

    /**
     * It checks if the file on the filePath is valid for our project or not.
     * 
     * @param filePath : the path where the file places
     * @return : true for valid, otherwise false
     */
    public boolean isValid(String filePath) {
        try {
            BufferedReader br = new BufferedReader(new FileReader(filePath));
            int columnCounter = 0, check = 0;
            String line = br.readLine();
            check = columnCounter = (line.split(",")).length;
            while (line != null) {
                check = (line.split(",")).length;
                if (check != columnCounter)
                    return false;
                line = br.readLine();
            }
            br.close();
        } catch (Exception e) {
            System.out.println(e);
        }

        return true;
    }

    /**
     * It prints the column names of the table.
     * 
     * @param tableName :
     */
    public void printSchema(String tableName) {
        int i = findTable(tableName);
        System.out.println(tables[i].getHeader());
    }

    public Table getTable(int index) {
        return tables[index - 1];
    }

    public void print(String[] data) {

        try {
            if (data == null)
                throw new IllegalStateException("");
        } catch (Exception e) {
            // TODO: handle exception
            return;
        }
        for (int i = 0; i < data.length; i++) {
            for (; data[i].indexOf(",") != -1;) {
                data[i] = data[i].substring(0, data[i].indexOf(",")) + "\t"
                        + data[i].substring(data[i].indexOf(",") + 1);
            }
            System.out.println(data[i]);
        }
    }

    /**
     * It gets and returns indexes for each given column names
     * 
     * @param table   : table object where the operations are done
     * @param columns : the columns' names
     * @return : Returns an index for each given column
     */
    public int[] findColumns(Table table, String[] columns) {
        int[] columnIndex = new int[columns.length];

        for (int j = 0; j < columns.length; j++)
            columnIndex[j] = table.whichColumn(columns[j]);

        return columnIndex;
    }

    public boolean isThere(String tableName) {
        int i = 0, j = -1;
        for (; i < tableNames.length && tableNames[i] != null; i++) {
            if (tableNames[i].equals(tableName)) {
                j = i;
                break;
            }
        }

        if (j == -1)
            return false;

        return true;
    }

    public int findTable(String tableName) throws IllegalStateException {

        int i = 0, j = -1;
        for (; i < tableNames.length && tableNames[i] != null; i++) {
            if (tableNames[i].equals(tableName)) {
                j = i;
                break;
            }
        }

        if (j == -1)
            throw new IllegalStateException("No Found Table!");

        return i;
    }

    /**
     * It checks the array of the tables is full or not.
     * 
     * @return : a boolean value
     */
    public boolean isFull() {
        for (int i = 0; i < tables.length; i++)
            if (tables[i] == null)
                return false;
        return true;
    }

    /**
     * It expands the array by twice its size.
     */
    public void expandTableArray() {
        Table[] newTableArr = new Table[(tables.length * 2)];
        for (int i = 0; i < tables.length; i++)
            newTableArr[i] = tables[i];

        tables = newTableArr;

        String[] newTableNames = new String[(tables.length * 2)];
        for (int i = 0; i < tableNames.length; i++)
            newTableNames[i] = tableNames[i];

        tableNames = newTableNames;
    }
}
