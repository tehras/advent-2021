package common

import java.io.File

object FileReader {
  fun readFile(fileName: String): String {
    return File("src/main/resources/$fileName").readText()
  }
}