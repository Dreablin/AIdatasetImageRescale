import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import ui.MainScreen
import java.awt.RenderingHints
import java.awt.image.BufferedImage
import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.File
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import javax.imageio.ImageIO


fun main() = application {
    val filesList = getFiles("C:\\_____1\\4")
    Window(onCloseRequest = ::exitApplication) {
        MainScreen(filesList)
    }
}

val listOfImageExtensions = listOf(
    "jpg"
)

val listOfMapFileExtensions = listOf(
    "xml"
)

fun getFiles(folderPath: String): List<String> {

    val folder = File(folderPath)

    if (folder.exists() && folder.isDirectory) {
        val files = folder.listFiles() ?: emptyArray()
        val images = files.filter { file ->
            file.isFile && file.extension.toLowerCase() in listOfImageExtensions
        }
        val mapFiles = files.filter { file ->
            file.isFile && file.extension.toLowerCase() in listOfMapFileExtensions
        }
//        handleRescaleInXML(mapFiles.first())

        images.forEach {
            rescaleImage(it)
        }
        mapFiles.forEach {
            handleRescaleInXML(it)
        }


        return mutableListOf<String>().apply {
            images.forEach {
                val resolution = getJPEGResolution(it.absolutePath)
                this@apply.add("${it.name}: ${resolution?.first} x ${resolution?.second} pixels")
            }
        }
    } else {
        println("The specified folder does not exist or is not a directory.")
    }
    return listOf()
}

fun getJPEGResolution(filePath: String): Pair<Int, Int>? {
    try {
        val image = ImageIO.read(File(filePath))

        if (image != null) {
            val width = image.width
            val height = image.height
            return Pair(width, height)
        } else {
            println("Failed to read the image.")
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }

    return null
}

fun rescaleImage(file: File) {
//    val outputFilePath = "${inputFilePath}_new"
    val realFilePath = "${file.absolutePath.removeSuffix(file.name)}\\converted"
    val outputDirectoryPath: Path = Paths.get(realFilePath)
    Files.createDirectories(outputDirectoryPath)
    val outputFilePath = "$realFilePath\\${file.name.toLowerCase()}"
    val resolution = getJPEGResolution(file.absolutePath)
    val targetWidth = resolution!!.first/2
    val targetHeight = resolution.second/2
    try {
//        val inputImage = ImageIO.read(File(inputFilePath))
        val inputImage = ImageIO.read(file.absoluteFile)
        val outputImage = BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_RGB)

        val graphics = outputImage.createGraphics()
        graphics.drawImage(inputImage, 0, 0, targetWidth, targetHeight, null)
        graphics.dispose()

        // Optional: You can specify rendering hints to improve the quality of the scaled image
        graphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR)
        graphics.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY)
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)

        ImageIO.write(outputImage, "jpg", File(outputFilePath))

    } catch (e: Exception) {
        e.printStackTrace()
    }
}

fun handleRescaleInXML(file: File) {
    val path = Paths.get(file.absolutePath)
    val realFilePath = "${file.absolutePath.removeSuffix(file.name)}\\converted"
    val newPath = Paths.get("${file.absolutePath.removeSuffix(file.name)}\\converted\\${file.name}")
    try {
        val reader: BufferedReader = Files.newBufferedReader(path)
        val writer: BufferedWriter = Files.newBufferedWriter(path.resolveSibling("temp.xml"))

        var line: String?
        while (reader.readLine().also { line = it } != null) {
            when {
                line == "<width>4000</width>" -> writer.write("<width>2000</width>")
                line == "<height>3000</height>" -> writer.write("<height>1500</height>")
                line!!.contains("<xmin>") -> {
                    val regex = """<xmin>(\d+)</xmin>""".toRegex()
                    val value = extractNumberFromString(line as String, regex)
                    writer.write("<xmin>${value!! / 2}</xmin>")
                }

                line!!.contains("<ymin>") -> {
                    val regex = """<ymin>(\d+)</ymin>""".toRegex()
                    val value = extractNumberFromString(line as String, regex)
                    writer.write("<ymin>${value!! / 2}</ymin>")
                }

                line!!.contains("<xmax>") -> {
                    val regex = """<xmax>(\d+)</xmax>""".toRegex()
                    val value = extractNumberFromString(line as String, regex)
                    writer.write("<xmax>${value!! / 2}</xmax>")
                }

                line!!.contains("<ymax>") -> {
                    val regex = """<ymax>(\d+)</ymax>""".toRegex()
                    val value = extractNumberFromString(line as String, regex)
                    writer.write("<ymax>${value!! / 2}</ymax>")
                }

                else -> {
                    writer.write(line)
                }
            }
        }
        reader.close()
        writer.close()

        Files.move(path.resolveSibling("temp.xml"), newPath, java.nio.file.StandardCopyOption.REPLACE_EXISTING)
    } catch (e:Exception) {
        println(e.message)
    }
//    val fileName = "${file.absolutePath.removeSuffix(file.extension)}.xml"
//    val xmlContent = file.readLines()
//    xmlContent.forEach {
//        println(it)
//    }
}

fun processXmlLine(line: String) {
    // Implement your logic for processing each line here
    // For example, you can check if the line contains specific elements or values
    println(line)
}

fun extractNumberFromString(input: String, regex: Regex): Int? {
    val matchResult = regex.find(input)

    return matchResult?.groupValues?.get(1)?.toIntOrNull()
}