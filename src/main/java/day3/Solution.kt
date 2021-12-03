package day3

import common.FileReader

fun main() {
  val bitsFromInput = parseFile()

  computePower(bitsFromInput)
  computeLifeSupport(bitsFromInput)
}

private fun parseFile(): List<String> {
  return FileReader.readFile("day3.txt").lines()
    .filterNot { it.isEmpty() }
}

private fun computeLifeSupport(inputs: List<String>) {
  println("====== Computing Life Support ======")
  val oxygen = recurse(validInputs = inputs, atIndex = 0, findMostCommon = true)
  val co2 = recurse(validInputs = inputs, atIndex = 0, findMostCommon = false)

  val oxygenNum = Integer.parseInt(oxygen, 2)
  val co2Num = Integer.parseInt(co2, 2)

  val result = oxygenNum * co2Num

  println("For oxygen :: $oxygen and co2 :: $co2 we yield result of :: $result")
}

private fun recurse(validInputs: List<String>, atIndex: Int, findMostCommon: Boolean): String {
  require(validInputs.isNotEmpty()) { "We've gone too far!" }

  // If the input size is 1, get out!
  if (validInputs.size == 1) return validInputs[0]

  val chars = validInputs.mapIndexed { index, string ->
    string[atIndex].digitToInt() to index
  }

  val groupedChars = chars
    // Group by digit.
    .groupBy { it.first }

  val zerosGroup = groupedChars[0] ?: emptyList()
  val onesGroup = groupedChars[1] ?: emptyList()

  val eligibleGroup = if (findMostCommon) {
    // If more 0s, then return 0s group.
    if (zerosGroup.size > onesGroup.size) {
      zerosGroup
    } else {
      onesGroup
    }
  } else {
    // Now if it's least common and it's a tie, we want to return zeros.
    if (onesGroup.size < zerosGroup.size) {
      onesGroup
    } else {
      zerosGroup
    }
  }

  // This is the group of indexes we want to keep.
  val newValidInputs = eligibleGroup
    .map { it.second }
    .map { validInputs[it] }

  return recurse(
    validInputs = newValidInputs,
    atIndex = atIndex + 1,
    findMostCommon = findMostCommon
  )
}

private fun computePower(inputs: List<String>) {
  println("====== Computing Power ======")
  // The 1st Int is the index, and the 2nd is the count of 0s.
  val countOfZeros = mutableMapOf<Int, Int>()

  inputs.map { it.toCharArray() }
    .forEach { chars ->
      chars.forEachIndexed { index, c ->
        if (c.isDigit()) {
          val value = c.digitToInt()

          if (value != 0 && value != 1) {
            error("Illegal value input ::$value")
          }

          if (value == 0) {
            // Increment by 1.
            countOfZeros[index] = countOfZeros.getOrDefault(index, 0) + 1
          }
        }
      }
    }

  val totalSize = inputs.size
  val gammaRate = IntArray(countOfZeros.keys.size)
  val epsilonRate = IntArray(countOfZeros.keys.size)

  countOfZeros.forEach { (index, value) ->
    if (value * 2 > totalSize) {
      // This means 0 is more popular.
      gammaRate[index] = 0
      epsilonRate[index] = 1
    } else {
      gammaRate[index] = 1
      epsilonRate[index] = 0
    }
  }

  val gammaRateNum = Integer.parseInt(gammaRate.toBinary(), 2)
  val epsilonRateNum = Integer.parseInt(epsilonRate.toBinary(), 2)

  println("Gamma Number :: $gammaRateNum x Epsilon Number :: $epsilonRateNum = ${gammaRateNum * epsilonRateNum}")
}

private fun IntArray.toBinary(): String {
  var binaryString = charArrayOf()

  forEach { binaryString += it.digitToChar() }

  return binaryString.concatToString()
}