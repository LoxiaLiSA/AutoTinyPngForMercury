import com.tinify.Source
import com.tinify.Tinify
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

interface SomethingNeedToImplement {

    // api key，可以自己申请一个(https://tinypng.com/developers)，也可以就用当前这个, 每个月500以内压缩次免费
    fun tinyPngApiKey(): String {
        return "HZt2Bmd5T8fDcNFpYwdJf4kDNf1Lglzp"
    }

    // 设置Mercury项目的路径，例："/Users/ceuilisa/Desktop/code/Mercury"
    fun mercuryProjectPath(): String

    // 设置中途生成的png文件路径，例："/Users/ceuilisa/Desktop/fileTest/to/tiny_png_cache.png"
    fun pngCacheSavePath(): String
}

class SomethingNeedToImplementSample: SomethingNeedToImplement {

    override fun mercuryProjectPath(): String {
        return "/Users/ceuilisa/Desktop/code/Mercury"
    }

    override fun pngCacheSavePath(): String {
        return "/Users/ceuilisa/Desktop/fileTest"
    }
}

fun main() {

    // 设置你想要压缩的文件名, 支持多个
    val imageFileNameList = listOf(
        "img_doll.png"
    )

    val somethingNeedToImplement = SomethingNeedToImplementSample()
    val mercuryPath = somethingNeedToImplement.mercuryProjectPath()
    val cachePath = somethingNeedToImplement.pngCacheSavePath()
    val imageResRootPath = "$mercuryPath/app/src/main/res"

    Tinify.setKey(somethingNeedToImplement.tinyPngApiKey())

    FileUtil.traverseFolder(imageResRootPath) { pngFile ->
        val fileName = pngFile.name
        if (imageFileNameList.contains(fileName)) {
            println("找到了一个目标：${pngFile.path}")
            compressImageResource(pngFile.path, cachePath)
        }
    }
}


fun compressImageResource(originImagePath: String, cachePath: String) = runBlocking {
    val source: Source = Tinify.fromFile(originImagePath)
    val fullCachePath = "$cachePath/tiny_png_cache.png"
    source.toFile(fullCachePath)

    File(originImagePath).delete()

    copyFile(fullCachePath, originImagePath)

    delay(2500L)

    File(fullCachePath).delete()

    println("compressImageResource successfully.")
}
