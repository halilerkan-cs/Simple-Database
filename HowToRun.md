# Simple-Database
A simple database made with Java
It can only save csv files, otherwise be thrown an exception.
You can save the files writing "CREATE TABLE FROM <csv-file-path>".
You can make a search writing "SELECT <column-name1,column-name2...> FROM <table-name>;".
If you prefer to list all columns, you can use asterix (*) instead of column names.
Table names are the name of the files without the csv extention. For example, file name: "userLogs.csv", table name: "userLogs".
You can make a search according to column names which place on the first line of the csv file.
If you prefer, you can add a condition using keyword "WHERE". Like, "SELECT first_name FROM userLogs WHERE id=1000;" 
It will be printed, the first_name columns where the rows has an id 1000.
