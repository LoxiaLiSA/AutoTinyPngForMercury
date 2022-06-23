
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.InputStream
import java.util.*
import javax.swing.JSplitPane
import javax.swing.JSplitPane.DIVIDER

object FileUtil {

    var pngCount = 0

    fun traverseFolder(path: String, onPngFound: (File)->Unit) {
        val file = File(path)
        if (file.exists()) {
            if (file.isDirectory) {
                val files = file.listFiles()
                if (files?.isNotEmpty() == true) {
                    for (childFile in files) {
                        if (childFile.isDirectory) {
                            traverseFolder(childFile.absolutePath, onPngFound)
                        } else if (childFile.isFile) {
                            if (childFile.isPng) {
                                pngCount++
//                                println(childFile.path + ", png总数：$pngCount")
                                onPngFound(childFile)
                            }
                        }
                    }
                } else {
                    println("文件夹是空的!")
                }
            } else if (file.isFile) {
                if (file.isPng) {
                    pngCount++
                    println(file.path + "$pngCount")
                }
            }
        } else {
            println("文件/文件夹不存在!")
        }
    }
}

internal val File.isPng get() = extension.lowercase(Locale.ROOT) == "png"

fun copyFile(srcPath: String, destPath: String) {
    FileInputStream(File(srcPath)).use { fis ->
        FileOutputStream(File(destPath)).use { os ->
            val buffer = ByteArray(1024)
            var len: Int
            while (fis.read(buffer).also { len = it } != -1) {
                os.write(buffer, 0, len)
            }
        }
    }
}