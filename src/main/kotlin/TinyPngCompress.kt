import com.tinify.Source
import com.tinify.Tinify
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import java.io.File
import java.text.DecimalFormat
import kotlin.math.log10
import kotlin.math.pow


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

class SomethingNeedToImplementSample : SomethingNeedToImplement {

    override fun mercuryProjectPath(): String {
        return "/Users/ceuilisa/Desktop/code/Mercury"
    }

    override fun pngCacheSavePath(): String {
        return "/Users/ceuilisa/Desktop/fileTest"
    }
}

fun readableFileSize(length: Long): String {
    if (length <= 0) return "0"
    val units = arrayOf("B", "kB", "MB", "GB", "TB")
    val digitGroups = (log10(length.toDouble()) / log10(1024.0)).toInt()
    return DecimalFormat("#,##0.#").format(length / 1024.0.pow(digitGroups.toDouble())) + " " + units[digitGroups]
}

fun main() {

    val somethingNeedToImplement = SomethingNeedToImplementSample()
    val mercuryPath = somethingNeedToImplement.mercuryProjectPath()
    val cachePath = somethingNeedToImplement.pngCacheSavePath()
    val imageResRootPath = "$mercuryPath/app/src/main/res"

    Tinify.setKey(somethingNeedToImplement.tinyPngApiKey())


    // 查找项目中的png文件并按文件大小排序
    val pngList = mutableListOf<File>()
    FileUtil.traverseFolder(imageResRootPath) { pngFile ->
        pngList.add(pngFile)
    }
    var total = 0L
    pngList.sortedBy { it.length() }.forEachIndexed { index, file ->
        total += file.length()
        println("${index}, ${file.path}, ${readableFileSize(file.length())}")
    }
    println("png资源总大小：${readableFileSize(total)}")


    // 选出你想要压缩的文件名, 支持多个
    val imageFileNameList = listOf<String>(
//        "img_shoe.png",
//        "img_alien.png",
//        "meet_text_intro_image.png",
//        "meet_voice_intro_image.png",
//        "meet_light_bg3.png",
//        "icon_pick_bottle_main.png",
//        "empty_friend_image.png",
//        "empty_chat_image.png",
//        "empty_circle_image.png",
//        "meet_light_bg1.png",
//        "meet_light_bg4.png",
    )
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
