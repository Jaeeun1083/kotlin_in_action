package action.junyoung.chapter06;

import jakarta.annotation.Nullable;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;

public class NullableTest {
    public static final void printString(@Nullable String s1, @NotNull String s2) {
        String v0 = s2;
        if(s2 == null) {
            v0 = "null";
        }

        Intrinsics.checkNotNullParameter(s2, "s2");
        String v1 = s1;
        if (s1 == null) {
            v1 = "null";
        }
        String v2 = v1;
        System.out.println(v2);
        System.out.println(v0);
    }
    public static void main(String[] args) {
        printString(null, "null");
    }
}
