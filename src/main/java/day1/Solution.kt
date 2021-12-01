package day1

import common.FileReader

fun main() {
  val listOfNumbers = readAndSplitIntoLines()

  calculateIncreased(listOfNumbers)
  calculateSlidingScale(listOfNumbers)
  calculateSlidingScaleCheating(listOfNumbers)
}

private fun calculateSlidingScaleCheating(listOfNumbers: List<Int>) {
  val result = listOfNumbers.windowed(3).map { it.sum() }.windowed(2).count { it[1] > it[0] }

  println("increased steps (cheating)- $result")
}

private fun calculateSlidingScale(listOfNumbers: List<Int>) {
  // We want to add the previous 3.
  var prev = -1
  var prev2 = -1
  var totalLast3 = -1

  var increased = 0;

  val assignLast2: (Int) -> Unit = { new ->
    prev2 = prev
    prev = new
  }

  listOfNumbers.forEach { thisNumber ->
    // That means it hasn't been assigned yet.
    when {
      // Do nothing.
      prev == -1 || prev2 == -1 -> Unit
      else -> {
        // Now we start comparing.
        // if last 3 are -1 that means this is the first.
        totalLast3 = if (totalLast3 == -1) {
          prev + prev2 + thisNumber
        } else {
          // New past 3 sum.
          val newLast3 = prev + prev2 + thisNumber

          if (newLast3 > totalLast3) {
            increased++
          }

          newLast3
        }
      }
    }

    assignLast2(thisNumber)
  }

  println("increased steps - $increased")
}

private fun calculateIncreased(listOfNumbers: List<Int>) {
  var increasedAmount = 0
  var previous = -1

  listOfNumbers.forEach { thisNumber ->
    if (previous != -1) {
      if (thisNumber > previous) {
        increasedAmount++
      }
    }

    previous = thisNumber
  }

  println("increased numbers - $increasedAmount")
}

private fun readAndSplitIntoLines(): List<Int> {
  return FileReader.readFile("day1.txt")
    .lines()
    .filterNot { it.isEmpty() }
    .map { it.toInt() }
}

