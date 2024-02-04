package action.junyoung.chapter06;

import java.io.File;
import java.util.List;

public interface FileContentProcessor {
    void processContents(File path, byte[] binaryContents, List<String> textContents);
}
