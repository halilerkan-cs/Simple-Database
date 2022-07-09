public class Row {
    private Row next;// It is for binary search tree. The rows which have same key are in a
                     // linkedlist structure.
    private String data;
    private String[] dataArr;

    public Row(String data, Row nextRow) {
        this.data = data;
        next = nextRow;
        dataArr = data.split(",");
    }

    public String getData() {
        return new String(data);
    }

    public void setNext(Row newNext) {
        next = newNext;
    }

    public Row getNext() {
        return next;
    }

    public String getData(int index) {
        return dataArr[index];
    }

    public String toString() {
        return data;
    }
}
