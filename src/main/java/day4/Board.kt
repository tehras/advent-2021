package day4

// The bingo board is a 5x5 matrix.
class Board {
  // 5x5 matrix.
  private val matrix = Array(5) { IntArray(5) { -1 } }
  private val matchingArray = Array(5) { IntArray(5) { -1 } }
  private var lastNumberPlayed = -1

  /** Plays the number and returns whether it has won or not. **/
  fun playNumber(number: Int): Boolean {
    lastNumberPlayed = number

    // Find location of the number.
    matrix.forEachIndexed Outer@{ rowIndex, ints ->
      ints.forEachIndexed { columnIndex, i ->
        if (i == number) {
          matchingArray[rowIndex][columnIndex] = number
          return@Outer
        }
      }
    }

    return hasWon()
  }

  /**
   * The score of the winning board can now be calculated. Start by finding the sum of all unmarked numbers on that board; in this case, the sum is 188. Then, multiply that sum by the number that was just called when the board won
   */
  fun calculateScore(): Int {
    // Get marked numbers.
    val markedNumbers = markedNumbers()

    // Now that we got marked numbers, let's sum all the unmarked.
    val sumOfUnmarked = matrix.sumOf { row ->
      row.filterNot { it in markedNumbers }
        .sum()
    }

    return lastNumberPlayed * sumOfUnmarked
  }

  fun addRow(row: IntArray) {
    if (row.size != 5) {
      error("Invalid row trying to be added :: $row")
    }
    if (isValid()) {
      error("Trying to add to a full board.")
    }

    val index = matrix.indexOfFirst { r -> r.contains(-1) }
    matrix[index] = row
  }

  fun isEmpty(): Boolean = matrix.none { row -> row.none { it == -1 } }
  fun isValid(): Boolean {
    val filteredMatrix = matrix
      .filter { row ->
        row.none { it == -1 }
      }

    return filteredMatrix.size == 5
  }

  private fun hasWon(): Boolean {
    // This takes care of the horizontal. Now let's do vertical.
    matchingArray.forEach { row ->
      if (row.filter { it != -1 }.size == 5) {
        return true
      }
    }

    for (i in matchingArray.indices) {
      val rowSize = matchingArray.map { it[i] }
        .filter { it != -1 }
        .size

      if (rowSize == 5) {
        return true
      }
    }

    return false
  }

  private fun markedNumbers(): List<Int> {
    return matchingArray
      .map { row ->
        row.filter { it != -1 }
      }
      .flatten()
  }

  override fun toString(): String {
    val sb = StringBuilder()
    val markedNumbers = markedNumbers()

    sb.append("Board :: \n")
    matrix
      .forEach {
        val row = it.joinToString(" ") { i ->
          val result = if (i in markedNumbers) {
            "+$i"
          } else {
            " $i"
          }
          if (result.length < 3) {
            " $result"
          } else {
            result
          }
        }
        sb.append(row)
        sb.append("\n")
      }

    return sb.toString()
  }
}