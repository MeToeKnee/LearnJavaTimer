package uk.org.harden;

public class MyLinkedListTest {

    public static void main(String[] args) {
        MyLinkedList lList = new MyLinkedList();

        // add elements to LinkedList
        lList.add("1");
        lList.add("2");
        lList.add("3");
        lList.add("4");
        lList.add("5");

        lList.add("7", 10);

        /*
         * Please note that primitive values can not be added into LinkedList
         * directly. They must be converted to their corresponding wrapper
         * class.
         */

        System.out.println("lList - print linkedlist: " + lList);
        System.out.println("lList.size() - print linkedlist size: " + lList.size());
        System.out.println("lList.get(3) - get 3rd element: " + lList.get(3));
        System.out.println("lList.remove(2) - remove 2nd element: " + lList.remove(2));
        System.out.println("lList.get(3) - get 3rd element: " + lList.get(3));
        System.out.println("lList.size() - print linkedlist size: " + lList.size());
        System.out.println("lList - print linkedlist: " + lList);

        System.out.println("lList.remove(1) - remove 1st element: " + lList.remove(1));
        System.out.println("lList - print linkedlist: " + lList);

    }
}
