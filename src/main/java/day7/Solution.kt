package day7

import common.FileReader
import kotlin.math.abs

fun main() {
  val horizontalPositions = readFile()

  // Just 1 to 1.
  findOptimalHorizontalStart(horizontalPositions)

  // Adding this cache thing greatly reduces the calculation time.
  val calcCache = mutableMapOf<Int, Int>()
  // Every move goes up by 1.
  findOptimalHorizontalStart(horizontalPositions) { movement ->
    if (movement == 0) return@findOptimalHorizontalStart 0
    calcCache[movement]?.let { return@findOptimalHorizontalStart it }

    var total = 0
    for (i in 1..movement) {
      total += i
    }

    calcCache[movement] = total
    total
  }
}

fun findOptimalHorizontalStart(
  horizontalPositions: List<Int>,
  multiplier: (Int) -> Int = { movement -> movement }
) {
  val start = System.currentTimeMillis()
  // We're going to try to do middle out.
  val sorted = horizontalPositions.sorted()

  val min = sorted[0]
  val max = sorted[sorted.size - 1]
  val middle = ((max - min) + 1) / 2

  // Let's start from middle out.
  var lowestTotal = Int.MAX_VALUE
  var lowestInput = middle

  for (i in middle..max) {
    val offset = i - middle

    var totalLeft = 0
    var totalRight = 0

    val left = middle - offset
    val right = middle + offset

    sorted.reversed().forEach { number ->
      totalLeft += multiplier(abs(number - left))
      totalRight += multiplier(abs(number - right))

      if (lowestTotal != Int.MAX_VALUE) {
        if (totalLeft > lowestTotal && totalRight > lowestTotal) {
          // Stop looping.
          return@forEach
        }
      }
    }

    if (totalLeft < lowestTotal) {
      lowestTotal = totalLeft
      lowestInput = left
    } else if (totalRight < lowestTotal) {
      lowestTotal = totalRight
      lowestInput = right
    }
  }

  println("Lowest input is $lowestInput with total of $lowestTotal")
  println("Finished in ${System.currentTimeMillis() - start} ms")
}

private fun readFile(): List<Int> {
  return FileReader.readFile("day7.txt")
    .split(",")
    .map { it.toInt() }
}