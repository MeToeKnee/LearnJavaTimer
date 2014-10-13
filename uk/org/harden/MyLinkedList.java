package uk.org.harden;

public class MyLinkedList {

    private class Node {
        Node   next;
        Object data;

        public Node(Object dataValue) {
            next = null;
            data = dataValue;
        }

        public Object getData() {
            return data;
        }

        public void setData(Object dataValue) {
            data = dataValue;
        }

        public Node getNext() {
            return next;
        }

        public void setNext(Node nextValue) {
            next = nextValue;
        }
    }

    // reference to the head node.
    private Node head;
    private int  listCount;

    // LinkedList constructor
    public MyLinkedList() {
        // empty list ... head node is set to a new node with no data and never deleted.
        head = new Node(null);
        listCount = 0;
    }

    public int add(Object data)
    // appends the specified element to the end of this list.
    {
        return add(data, listCount + 1);
    }

    public int add(Object data, int index)
    // inserts the specified element at the specified position in this list
    {
        Node myTemp = new Node(data);
        Node myCurrent = head;
        int i;

        for (i = 1; i < index && myCurrent.getNext() != null; i++) {
            myCurrent = myCurrent.getNext();
        }

        myTemp.setNext(myCurrent.getNext());
        myCurrent.setNext(myTemp);
        listCount++;

        return i;
    }

    public Object get(int index)
    // returns the element at the specified position in this list.
    {
        if (index <= 0)
            return null;

        Node myCurrent = head.getNext();
        for (int i = 1; i < index; i++) {
            if (myCurrent.getNext() == null)
                return null;

            myCurrent = myCurrent.getNext();
        }
        return myCurrent.getData();
    }

    public Object remove(int index)
    // removes the element at the specified position in this list.
    {
        if (index < 1 || index > size())
            return false;

        Node myCurrent = head;
        for (int i = 1; i < index; i++) {
            if (myCurrent.getNext() == null)
                return false;

            myCurrent = myCurrent.getNext();
        }
        Node nodeDeleted = myCurrent.getNext();
        myCurrent.setNext(nodeDeleted.getNext());
        listCount--;
        return nodeDeleted.getData();
    }

    public int size()
    // returns the number of elements in this list.
    {
        return listCount;
    }

    public String toString() {
        String output = "";
        for (Node myCurrent = head.getNext(); myCurrent != null; myCurrent = myCurrent.getNext()) {
            output += "[" + myCurrent.getData().toString() + "]";
        }
        return output;
    }
}