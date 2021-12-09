package day9

import common.FileReader
import kotlin.text.StringBuilder

fun main() {
  val grid = Grid(parseFile())

  val lowestPoints = grid.lowPoints()
    .sortedByDescending { it.value }

  println("Lowest points :: $lowestPoints")
  println("Total :: ${lowestPoints.sumOf { it.value + 1 }}")

  val basins = grid.findBasins()
    .sortedByDescending { it.size }

  basins.forEach { println(it) }

  val topThree = basins
    .subList(0, 3)
    .map {
      println("Highest basin size :: ${it.size}")
      it.size
    }
    .reduce { acc, i -> acc * i }

  println("top three :: $topThree")
}

private fun parseFile(): List<String> = FileReader.readFile("day9.txt")
  .lines()

class Grid(
  private val input: List<String>
) {
  private val _grid = Array(input.size) { IntArray(input[0].length) }

  init {
    // Populate the grid.
    input.forEachIndexed { index, line ->
      line.toCharArray().map { it.digitToInt() }
        .forEachIndexed { digitIndex, digit ->
          _grid[index][digitIndex] = digit
        }
    }
  }

  fun lowPoints(): List<Coordinate> {
    val lowestPoints = mutableListOf<Coordinate>()

    // Start scrolling through to find low points.
    _grid.forEachIndexed { y, ints ->
      ints.forEachIndexed { x, digit ->
        val coordinate = Coordinate(x, y, digit)

        if (isLowerThanAdjacent(coordinate)) {
          lowestPoints += Coordinate(x, y, digit)
        }
      }
    }

    return lowestPoints
  }

  fun findBasins(): List<Basin> {
    val lowestPoints = lowPoints()

    // Now we go from here and see which ones are basins.
    return lowestPoints.map { coordinate ->
      Basin(coordinate = findBasins(coordinate))
    }
  }

  private fun findBasins(coordinate: Coordinate): Set<Coordinate> {
    val allCoordinates = mutableSetOf(coordinate)

    fun findBasinsNear(coordinate: Coordinate, allCoordinates: Set<Coordinate>): Set<Coordinate> {
      val adjacent = adjacentCoordinates(coordinate)
        .filterNot { it in allCoordinates }

      return adjacent
        .filter { coord -> coord.value != 9 }
        .toSet()
    }

    var newBasins = findBasinsNear(coordinate, allCoordinates)

    while (newBasins.isNotEmpty()) {
      allCoordinates += newBasins
      val oldBasins = newBasins

      newBasins = oldBasins
        .map { newCoord ->
          findBasinsNear(newCoord, allCoordinates).also {
            allCoordinates += it
          }
        }
        .flatten()
        .toSet()
    }

    return allCoordinates
  }

  private fun isLowerThanAdjacent(
    coordinate: Coordinate,
    excludes: Set<Coordinate> = emptySet()
  ): Boolean {
    if (coordinate.value == 9) return false

    val adjacent = adjacentCoordinates(coordinate)
    adjacent.filterNot { it in excludes }
      .forEach {
        if (coordinate >= it)
          return false
      }

    return true
  }

  private fun adjacentCoordinates(coordinate: Coordinate): List<Coordinate> {
    val (x, y) = coordinate.x to coordinate.y
    val adjacent = mutableListOf<Coordinate>()

    // Left.
    findCoordinate(x - 1, y)?.let(adjacent::plusAssign)
    // Right
    findCoordinate(x + 1, y)?.let(adjacent::plusAssign)
    // Below
    findCoordinate(x, y - 1)?.let(adjacent::plusAssign)
    // Above
    findCoordinate(x, y + 1)?.let(adjacent::plusAssign)

    return adjacent
  }

  private fun findCoordinate(x: Int, y: Int): Coordinate? {
    val result = _grid.getSafely(x, y)
    if (result == 10) return null

    return Coordinate(x, y, result)
  }

  private fun Array<IntArray>.getSafely(x: Int, y: Int): Int {
    // No neighbor to the top or bottom.
    if (y < 0 || y > size - 1) return 10
    if (x < 0 || x > this[y].size - 1) return 10

    return this[y][x]
  }
}

data class Coordinate(val x: Int, val y: Int, val value: Int)

data class Basin(val coordinate: Set<Coordinate>) {
  val size = coordinate.size

  override fun toString(): String {
    val sb = StringBuilder()

    // sb.append("Basin consists of :\n")
    // coordinate.forEach {
    //   sb.append(it)
    //   sb.appendLine()
    // }

    sb.append("For total of ${coordinate.size} points")
    return sb.toString()
  }
}

operator fun Coordinate.compareTo(coordinate: Coordinate): Int {
  return value - coordinate.value
}