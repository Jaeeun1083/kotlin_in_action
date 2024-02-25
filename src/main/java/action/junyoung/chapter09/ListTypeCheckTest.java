package action.junyoung.chapter09;

import java.util.List;

public class ListTypeCheckTest {
    public static <T> void checkType(T value) {
        if (value instanceof List) {
            System.out.println("True");
        }
    }
    public static void main(String[] args) {
        List<String> strList = List.of("Hello", "World");
        checkType(strList);
    }
}
