package day15

import common.FileReader
import java.util.LinkedList
import java.util.Queue

fun main() {
  val grid = parseFullGrid()

  val start = System.currentTimeMillis()
  solution1(grid)
  val end = System.currentTimeMillis()
  println("duration :: ${end - start}")
}

private fun solution1(grid: Grid) {
  val start = Point(x = 0, y = 0, value = grid.first().first())
  val end =
    Point(x = grid.lastIndex, y = grid[grid.lastIndex].lastIndex, value = grid.last().last())

  var lowestToComplete: Long = Long.MAX_VALUE
  val lowestAtPoint = mutableMapOf<Point, Long>(start to 0)
  val pointsToVisit: Queue<Point> = LinkedList(listOf(start))

  while (pointsToVisit.isNotEmpty()) {
    val current = pointsToVisit.remove()
    val total = lowestAtPoint[current] ?: error("something went wrong")

    val adjacent = grid.adjacentTo(current).sortedBy { it.value }

    adjacent.forEach { point ->
      val pointTotal = total + point.value
      if (pointTotal > lowestToComplete) return@forEach

      if (point == end) {
        if (pointTotal < lowestToComplete) {
          lowestToComplete = pointTotal
        }
      } else {

        val lowestAtThisPoint = lowestAtPoint[point]

        if (lowestAtThisPoint == null || pointTotal < lowestAtThisPoint) {
          // This is lower so we can add to points to visit.
          lowestAtPoint[point] = pointTotal
          pointsToVisit += point
        }
      }
    }
  }

  println("Points :: $lowestToComplete")
}

private fun Grid.adjacentTo(point: Point): List<Point> {
  val createPoint: (x: Int, y: Int) -> Point? = { x, y ->
    getOrNull(x)?.getOrNull(y)?.let { Point(x, y, it) }
  }

  return listOfNotNull(
    createPoint(point.x - 1, point.y),
    createPoint(point.x + 1, point.y),
    createPoint(point.x, point.y - 1),
    createPoint(point.x, point.y + 1),
  )
}

private fun parseGrid(): Grid {
  val lines = FileReader.readFile("day15.txt").lines()

  return lines.mapIndexed { _, s ->
    s.mapIndexed { _, c -> c.digitToInt() }.toIntArray()
  }.toTypedArray()
}

private fun parseFullGrid(): Grid {
  val originalGrid = parseGrid()
  val horizontalSize = originalGrid[0].size
  val verticalSize = originalGrid.size

  val fullMap = Array(verticalSize * 5) { IntArray(horizontalSize * 5) }

  for (yMultiplier in 0..4) {
    for (xMultiplier in 0..4) {
      originalGrid.forEachIndexed { y, ints ->
        ints.forEachIndexed { x, value ->
          val adjustedX = x + (horizontalSize * xMultiplier)
          val adjustedY = y + (verticalSize * yMultiplier)
          val valueOffset = xMultiplier + yMultiplier

          val adjustedValue = if (value + valueOffset > 9) {
            value + valueOffset - 9
          } else {
            value + valueOffset
          }

          fullMap[adjustedY][adjustedX] = adjustedValue
        }
      }
    }
  }

  return fullMap
}

data class Point(
  val x: Int, val y: Int, val value: Int
)

typealias Grid = Array<IntArray>