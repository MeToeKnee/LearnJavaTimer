package uk.org.harden;

public class MyLinkedListTest {

    public static void main(String[] args) {
        MyLinkedList testList = new MyLinkedList();

        // Add elements to the end of the list
        System.out.println(testList.add("One"));
        System.out.println(testList.add("Two"));
        System.out.println(testList.add("Three"));
        System.out.println(testList.add("Four"));
        System.out.println(testList.add("Five"));

        // Add element off the end of the list
        System.out.println(testList.add("Ten", 10));

        // Add element in the middle of the list
        System.out.println(testList.add("Twenty", 3));

        // Exercise the list: to_string, size, get, remove
        System.out.println("testList           : " + testList);
        System.out.println("testList.size()    : " + testList.size());
        System.out.println("testList.get(3)    : " + testList.get(3));
        System.out.println("testList.remove(2) : " + testList.remove(2));
        System.out.println("testList.get(3)    : " + testList.get(3));
        System.out.println("testList.size()    : " + testList.size());
        System.out.println("testList           : " + testList);
        System.out.println("testList.remove(1) : " + testList.remove(1));
        System.out.println("testList           : " + testList);

    }
}
