package uk.org.harden;

class MyLinkedListTest {

    public static void main(String[] args) {
        MyLinkedList testList = new MyLinkedList();

        System.out.println("Byte    : " + Byte.MAX_VALUE);
        System.out.println("Short   : " + Short.MAX_VALUE);
        System.out.println("Integer : " + Integer.MAX_VALUE);
        System.out.println("Long    : " + Long.MAX_VALUE);
        System.out.println("Float   : " + Float.MAX_VALUE);
        System.out.println("Double  : " + Double.MAX_VALUE);

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
        System.out.println("The List           : " + testList);
        System.out.println("The List.size()    : " + testList.size());
        System.out.println("The List.get(3)    : " + testList.get(3));
        System.out.println("The List.remove(2) : " + testList.remove(2));
        System.out.println("The List.get(3)    : " + testList.get(3));
        System.out.println("The List.size()    : " + testList.size());
        System.out.println("The List           : " + testList);
        System.out.println("The List.remove(1) : " + testList.remove(1));
        System.out.println("The List           : " + testList);
        System.out.println("The List.get(0)    : " + testList.get(0));
        System.out.println("The List.get(13)   : " + testList.get(13));

    }
}
