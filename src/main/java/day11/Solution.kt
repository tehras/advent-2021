package day11

import common.FileReader

fun main() {
  val grid = findGrid()

  solveProblem1(grid.copy())
  solveProblem2(grid.copy())
}

private fun solveProblem1(grid: Grid) {
  // Problem 1.
  for (i in 0..99) {
    grid.nextStep()
    println("Step ${i + 1}")
    grid.printCurrentStats()
  }
}

private fun solveProblem2(grid: Grid) {
  var step = 1;
  var areAllFlashing = grid.areAllFlashing()
  while (!areAllFlashing) {
    grid.nextStep()
    step++
    areAllFlashing = grid.areAllFlashing()
  }
  println("All flashing @ step ${step - 1}")
}

private fun findGrid(): Grid {
  return FileReader.readFile("day11.txt").lines().let(::Grid)
}

data class Grid(
  private val input: List<String>
) {
  private val grid: Array<IntArray>
  var flashes: Long = 0L
    private set

  init {
    val ySize = input.size
    val xSize = input.first().length

    grid = Array(ySize) { IntArray(xSize) }

    input.forEachIndexed { yIndex, s ->
      s.toCharArray().map { it.digitToInt() }.forEachIndexed { xIndex, digit ->
        grid[yIndex][xIndex] = digit
      }
    }
  }

  fun nextStep() {
    // Increase every element by 1.
    grid.forEachIndexed { y, ints ->
      ints.forEachIndexed { x, i ->
        grid[y][x] = i + 1
      }
    }

    findNextFlash()
  }

  fun printCurrentStats() {
    val sb = StringBuilder()
    grid.forEach { array ->
      sb.append(array.joinToString(" ") { it.toString() })
      sb.append("\n")
    }
    println(sb.toString())

    println("Current flash count :: $flashes")
  }

  fun areAllFlashing(): Boolean {
    return grid.all { it.all { digit -> digit == 0 } }
  }

  private fun findNextFlash() {
    var increase = mutableListOf<Pair<Int, Int>>()

    // Now loop until flashes stop.
    grid.forEachIndexed Outer@{ y, ints ->
      // If greater > 9, then we want to flash.
      ints.forEachIndexed Inner@{ x, digit ->
        if (digit <= 9) return@Inner

        // Reset to 0.
        grid[y][x] = 0
        flashes++

        // Flash adjacent as well.
        // Adjacents classify as [x-1][y-1], [x][y-1], [x+1][y-1], [x-1][y], [x+1][y], [x-1][y+1] ... etc.
        increase += adjacent(x, y)
      }
    }

    while (increase.isNotEmpty()) {
      val newIncrease = mutableListOf<Pair<Int, Int>>()
      increase.forEach { (x, y) ->
        val currentVal = grid[y][x]
        if (currentVal != 0) {
          if (currentVal >= 9) {
            // That means it's popping.
            grid[y][x] = 0
            flashes++

            newIncrease += adjacent(x, y)
          } else {
            grid[y][x] = currentVal + 1
          }
        }
      }

      increase = newIncrease
    }
  }

  private fun adjacent(x: Int, y: Int): List<Pair<Int, Int>> {
    val list = mutableListOf<Pair<Int, Int>>()
    for (yOffset in -1..1) {
      for (xOffset in -1..1) {
        val newX = x + xOffset
        val newY = y + yOffset

        if (newX.withinXBounds() && newY.withinYBounds() && !(newX == x && newY == y)) {
          list += newX to newY
        }
      }
    }

    return list
  }

  private fun increaseAdjacent(x: Int, y: Int) {
    for (yOffset in -1..1) {
      for (xOffset in -1..1) {
        val newX = x + xOffset
        val newY = y + yOffset

        // Is within bounds.
        if (newX.withinXBounds() && newY.withinYBounds() && (newX != x && newY != y)) {
          val currentValue = grid[newY][newX]

          if (currentValue != 0) {
            if (currentValue >= 9) {
              grid[newY][newX] = 0
              flashes++
              increaseAdjacent(newX, newY)
            } else {
              // Let's bump by 1.
              grid[newY][newX] = currentValue + 1
            }
          }
        }
      }
    }
  }

  private fun Int.withinXBounds() = this >= 0 && this < grid.first().size
  private fun Int.withinYBounds() = this >= 0 && this < grid.size
}