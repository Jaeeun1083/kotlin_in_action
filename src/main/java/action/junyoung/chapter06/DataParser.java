package action.junyoung.chapter06;

import java.util.List;

public interface DataParser<T> {
    void parseData(String input, List<T> output, List<String> errors);
}
