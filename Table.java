public class Table {
    private Row header;
    private String name;
    private String[] columnNames;
    private BinarySearchTree<Row> bst;

    public Table(String name, Row header) {
        this.name = name;
        this.header = header;
    }

    /**
     * It creates and sets a binary search tree for this table.
     * 
     */
    public void setBinarySearchTree() {
        bst = new BinarySearchTree<>();
        Row temp = getHeader();
        while (temp.getNext() != null) {
            bst.insert(temp.getNext());
            temp = temp.getNext();
        }
    }

    public BinarySearchTree<Row> getBST() {
        return bst;
    }

    public Row getHeader() {
        return header;
    }

    public void setHeader(Row newHeader) {
        header = newHeader;
    }

    public String getName() {
        return new String(name);
    }

    /**
     * It takes the header row.
     * And it splits the column names.
     * 
     * @param theHeaderRow : the header of the file, which keeps the names of
     *                     columns
     */
    public void setColumnNames(String theHeaderRow) {
        columnNames = theHeaderRow.split(",");
    }

    /**
     * It searches and returns where the column places.
     * 
     * @param columnName
     * @return
     * @throws IllegalStateException
     */
    public int whichColumn(String columnName) throws IllegalStateException {
        int i, j = -1;
        for (i = 0; i < columnNames.length; i++) {
            if (columnNames[i].equals(columnName)) {
                j = i;
            }
        }

        if (j == -1)
            throw new IllegalStateException("No Found Column: " + columnName);

        return j;
    }

    public String[] getAllColumns() {
        return header.getData().split(",");
    }
}