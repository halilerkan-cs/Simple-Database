import javax.lang.model.util.ElementScanner14;

public class BinarySearchTree<E> {

    /*
     * Inner Class Node
     * It should be comparable since we use compare in binary search tree.
     * It has a row variable to point it.
     */
    public class Node implements Comparable {
        private Row row;
        private Node left, right;
        private Node next; // It is used for the nodes that has the same data index.

        public Node(Row data) {
            this.row = data;
            left = right = next = null;
        }

        public Node getLeft() {
            return left;
        }

        public Node getRight() {
            return right;
        }

        public Node getNext() {
            return next;
        }

        public void setNext(Node newNext) {
            next = newNext;
        }

        public Row getRow() {
            return row;
        }

        public String getFirstColumn() {
            return row.getData().substring(0, row.getData().indexOf(","));
        }

        public int compareTo(Object o) {
            Node rowToCompare = null;
            if (o instanceof BinarySearchTree.Node)
                rowToCompare = (Node) o;

            // Keep the first columns.
            // Columns are string.
            String data1 = getFirstColumn();
            String data2 = rowToCompare.getFirstColumn();

            // If the data are numbers it will be parse to numbers, otherwise will be
            // returned String class's compareTo method.
            if (data1.matches("-?\\d+(\\.\\d+)?") && data2.matches("-?\\d+(\\.\\d+)?")) {
                Double d1 = Double.parseDouble(data1);
                Double d2 = Double.parseDouble(data2);
                if (d1 > d2)
                    return 1;
                if (d2 > d1)
                    return -1;
                return 0;
            }
            return data1.compareTo(data2);
        }

        public String toString() {
            return new String(row.getData());
        }
    }/* ----------------------End Of Inner Class---------------------- */

    private Node root;

    public BinarySearchTree() {
        root = null;
    }

    public Node getRoot() {
        return root;
    }

    public void setRoot(Node newRoot) {
        root = newRoot;
    }

    /**
     * FOR NOT GETTING STACK-OVER-FLOW ERROR, DONT USE FOR BIG FILES!
     * Any non-recursive version will be better for not getting error.
     * It prints the keys inorder traversal.
     * It can be used for debug, not for actual program.
     */
    public void inorder() {
        inorderRecursive(root);
    }

    public void inorderRecursive(Node root) {
        if (root != null) {
            inorderRecursive(root.left);
            System.out.print(root.getFirstColumn() + ",");
            for (Node temp = root; temp.getNext() != null; temp = temp.getNext())
                System.out.print(temp.getFirstColumn() + ",");
            inorderRecursive(root.right);
        }
    }

    public void insert(E e) {
        Node newNode = null;
        try {
            if (e instanceof Row) {
                Row t = (Row) e;
                newNode = new Node(t);
            } else {
                throw new IllegalStateException("You cannot insert except the Row type.");
            }
        } catch (Exception exc) {
            exc.printStackTrace();
        }

        if (root == null) {
            root = newNode;
            return;
        }
        Node prev = null;
        Node temp = root;
        while (temp != null) {
            if (temp.compareTo(newNode) == 0) {
                Node temp2 = temp;
                for (; temp2.getNext() != null; temp2 = temp2.getNext())
                    ;
                temp2.setNext(newNode);
                return;
            } else if (temp.compareTo(newNode) < 0) {
                prev = temp;
                temp = temp.right;
            } else {
                prev = temp;
                temp = temp.left;
            }
        }
        if (prev.compareTo(newNode) > 0)
            prev.left = newNode;
        else
            prev.right = newNode;
    }

    /**
     * It searches the node that satisfies the condition.
     * 
     * @param condition : a string
     * @return : the node that satisfies the condition
     */
    public Node getData(String condition) {
        condition = condition + ",";
        if (root == null)
            return null;

        Node newNode = new Node(new Row(condition, null));
        Node prev = null;
        Node temp = root;
        while (temp != null) {
            if (temp.compareTo(newNode) == 0) {
                return temp;
            } else if (temp.compareTo(newNode) < 0) {
                prev = temp;
                temp = temp.right;
            } else {
                prev = temp;
                temp = temp.left;
            }
        }
        return null;
    }

    public String toString() {
        inorder();
        return "";
    }
}