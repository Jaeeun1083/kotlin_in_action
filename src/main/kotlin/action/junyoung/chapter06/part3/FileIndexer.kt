package action.junyoung.chapter06.part3

import action.junyoung.chapter06.FileContentProcessor
import java.io.File

class FileIndexer : FileContentProcessor {
    override fun processContents(
        path: File,
        binaryContents: ByteArray?,
        textContents: List<String>?) {
        // ..
    }
}