package action.junyoung.chapter10;

import action.junyoung.chapter10.part1.FooFunctions;
import action.junyoung.chapter10.part1.Foos;
import action.junyoung.chapter10.part1.JsonNaming;

public class KotlinAnnotationCallTest {
    public static void main(String[] args) {
        FooFunctions.foo();

        @JsonNaming(value = "hello", name = "20")
        int foo = 150;

        Foos.fooo();
    }
}
