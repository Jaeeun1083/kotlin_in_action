package action.junyoung.chapter08;

import kotlin.Unit;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.functions.Function1;

import java.util.ArrayList;
import java.util.List;

import static action.junyoung.chapter08.part1.FunctionTestKt.processTheAnswer;

public class JavaFunctionTest {
    public static void main(String[] args) {
        processTheAnswer(number -> number + 1);

        processTheAnswer(new Function1<Integer, Integer>() {
            @Override
            public Integer invoke(Integer number) {
                System.out.println(number);
                return number + 1;
            }
        });

        List<String> strings = new ArrayList<>();
        strings.add("42");

        CollectionsKt.forEach(strings, s -> {
            System.out.println(s);
            return Unit.INSTANCE;
        });
    }
}
