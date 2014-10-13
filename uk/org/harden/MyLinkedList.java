package uk.org.harden;

public class MyLinkedList {

    private class Node {
        Node   next;
        Object data;

        // Node constructor
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
        // this is an empty list, so the reference to the head node
        // is set to a new node with no data
        head = new Node(null);
        listCount = 0;
    }

    public void add(Object data)
    // appends the specified element to the end of this list.
    {
        Node myTemp = new Node(data);
        Node myCurrent = head;
        // starting at the head node, crawl to the end of the list
        while (myCurrent.getNext() != null) {
            myCurrent = myCurrent.getNext();
        }
        // the last node's "next" reference set to our new node
        myCurrent.setNext(myTemp);
        listCount++;// increment the number of elements variable
    }

    public void add(Object data, int index)
    // inserts the specified element at the specified position in this list
    {
        Node myTemp = new Node(data);
        Node myCurrent = head;
        // crawl to the requested index or the last element in the list,
        // whichever comes first
        for (int i = 1; i < index && myCurrent.getNext() != null; i++) {
            myCurrent = myCurrent.getNext();
        }
        // set the new node's next-node reference to this node's next-node
        // reference
        myTemp.setNext(myCurrent.getNext());
        // now set this node's next-node reference to the new node
        myCurrent.setNext(myTemp);
        listCount++;// increment the number of elements variable
    }

    public Object get(int index)
    // returns the element at the specified position in this list.
    {
        // index must be 1 or higher
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

    public boolean remove(int index)
    // removes the element at the specified position in this list.
    {
        // if the index is out of range, exit
        if (index < 1 || index > size())
            return false;

        Node myCurrent = head;
        for (int i = 1; i < index; i++) {
            if (myCurrent.getNext() == null)
                return false;

            myCurrent = myCurrent.getNext();
        }
        myCurrent.setNext(myCurrent.getNext().getNext());
        listCount--; // decrement the number of elements variable
        return true;
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