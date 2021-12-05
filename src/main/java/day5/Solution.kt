package day5

import common.FileReader
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

fun main() {
  val lines = readFile()

  findInterceptingPoints(lines)
}

fun findInterceptingPoints(lines: List<Line>) {
  // Find boundaries.
  val maxX = lines
    .map { listOf(it.start, it.end) }
    .flatten()
    .maxOf { it.x }

  val maxY = lines
    .map { listOf(it.start, it.end) }
    .flatten()
    .maxOf { it.y }

  // Create the matrix.
  val matrix = Matrix(maxX = maxX, maxY = maxY)

  lines.forEach {
    matrix.drawLine(it)
  }

  // Let's find the most points.
  println("Points with at least 2 :: ${matrix.pointsWithAtLeast(2)}")
}

private fun readFile(): List<Line> {
  return FileReader.readFile("day5.txt")
    .lines()
    .map {
      val coordinates = it.split("->")
        .map { string ->
          val trimmedString = string.trim()
          val xAndY = trimmedString.split(",")

          if (xAndY.size != 2) error("xAndY are not right :: $xAndY")

          Coordinate(x = xAndY[0].toInt(), y = xAndY[1].toInt())
        }

      require(coordinates.size == 2)

      Line(start = coordinates[0], end = coordinates[1])
    }
}

data class Coordinate(val x: Int, val y: Int)
data class Line(val start: Coordinate, val end: Coordinate)

data class Matrix(
  val maxX: Int,
  val maxY: Int
) {
  private val matrix = Array(maxX + 1) { IntArray(maxY + 1) }

  fun drawLine(line: Line) {
    // Let's start.
    // Only draw vert or horizontal lines.
    val start = line.start
    val end = line.end

    // Draw diagonal.
    val movementX = abs(start.x - end.x)
    val movementY = abs(start.y - end.y)

    // Can we do it in step functions somehow? Yes this works.
    // This is sort of doing a step function. It's important to note that it relies on everything
    // Being divisible into wholes, so only diagonal, vert, or horizontal lines work.
    val movementMax = max(movementX, movementY)

    for (i in 0..(movementMax)) {
      val iY = (i * movementY / movementMax)
      val iX = (i * movementX / movementMax)

      val y = if (start.y < end.y) {
        // Moving down.
        start.y + iY
      } else {
        start.y - iY
      }
      val x = if (start.x < end.x) {
        // We're moving right.
        start.x + iX
      } else {
        start.x - iX
      }

      val current = matrix[y][x]
      matrix[y][x] = current + 1
    }
  }

  fun pointsWithAtLeast(atLeast: Int): Int {
    var pointsWithAtLeast = 0
    matrix
      .forEach { array ->
        array.forEach {
          if (it >= atLeast) {
            pointsWithAtLeast++
          }
        }
      }

    return pointsWithAtLeast
  }
}