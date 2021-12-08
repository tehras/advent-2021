package day8

import common.FileReader
import day8.Number.*

fun main() {
  val inputs = parseFile()

  val count = inputs.obtainValuesMatchingLengths(One, Four, Seven, Eight)
  println("There are $count amount of times 1, 4, 7, 8 are seen.")

  decode(inputs)
}

// This method calculates how many inputs match numbers lengths.
private fun List<Input>.obtainValuesMatchingLengths(vararg numbers: Number): Int {
  val lengthsToLookFor = numbers.map { it.chars.size }

  return map { it.values.toList() }
    .flatten()
    .filter { it.length in lengthsToLookFor }
    .count()
}

private fun decode(inputs: List<Input>) {
  val total = inputs.sumOf(::decode)

  println("Total :: $total")
}

private fun decode(input: Input): Int {
  val entries = input.entries
    .map {
      it.toCharArray().sortedArray()
    }

  val newOne = entries.findByNumber(One).sortedArray()
  val newFour = entries.findByNumber(Four).sortedArray()
  val newSeven = entries.findByNumber(Seven).sortedArray()
  val newEight = entries.findByNumber(Eight).sortedArray()

  val mapping = mutableMapOf<CharArray, Number>()
  mapping[newOne] = One
  mapping[newFour] = Four
  mapping[newSeven] = Seven
  mapping[newEight] = Eight

  val newNine = entries
    .filter { it.size == 6 }
    .first { entry ->
      entry
        .filterNot { it in newFour }
        .count() == 2
    }

  mapping[newNine] = Nine

  val newSix = entries
    .filter { it.size == 6 }
    .first { entry ->
      newEight
        .filterNot { it in entry }
        .count() == 1 &&
        entry.filter { it in newSeven }
          .count() == 2
    }

  mapping[newSix] = Six

  val newFive = entries
    .filter { it.size == 5 }
    .first { entry ->
      newSix
        .filterNot { it in entry }
        .count() == 1
    }

  mapping[newFive] = Five

  val newZero = entries
    .filterNot { mapping.containsCharArray(it) }
    .filter { it.size == 6 }
    .first { entry ->
      newEight.filterNot { it in entry }
        .count() == 1
    }

  mapping[newZero] = Zero

  val newThree = entries
    .filterNot { mapping.containsCharArray(it) }
    .filter { it.size == 5 }
    .first { entry ->
      entry.filterNot { it in newSeven }
        .count() == 2
    }
  mapping[newThree] = Three

  val newTwo = entries
    .filterNot { mapping.containsCharArray(it) }
    .first()

  mapping[newTwo] = Two

  // Ok now let's decode the values, based on the mappings.
  // First remap.
  return input.values
    .map { it.toCharArray().sortedArray() }
    .map { array ->
      val value = mapping.findCharArray(array)?.value?.digitToChar()

      value
        ?: error("Could not find value for [${array.concatToString()}] for ${mapping.keys.map { it.concatToString() }}")
    }
    .toCharArray()
    .concatToString()
    .toInt()
    .also { println("Returning :: $it") }
}

private fun Map<CharArray, Number>.containsCharArray(charArray: CharArray): Boolean {
  return findCharArray(charArray) != null
}

private fun Map<CharArray, Number>.findCharArray(charArray: CharArray): Number? {
  return firstNotNullOfOrNull {
    if (it.key.contentEquals(charArray)) {
      it.value
    } else {
      null
    }
  }
}

private fun List<CharArray>.findByNumber(number: Number): CharArray {
  return first { it.size == number.chars.size }
}

private fun parseFile(): List<Input> {
  return FileReader.readFile("day8.txt")
    .lines()
    .map {
      val split = it.split("|")
      require(split.size == 2) { "$it could not be parsed" }
      split[0] to split[1]
    }
    .map { (entries, values) ->
      Input(
        entries = entries.split(" ").filterNot { it.isEmpty() }.toTypedArray(),
        values = values.split(" ").filterNot { it.isEmpty() }.toTypedArray()
      ).also {
        require(it.entries.size == 10) { "${it.entries} is not 10 of them." }
        require(it.values.size == 4) { "${it.values} is not 4 of them." }
      }
    }
}

// a = d, c -> a, f -> b, d = f
enum class Number(val chars: CharArray, val value: Int) {
  Zero(charArrayOf('a', 'b', 'c', 'e', 'f', 'g'), 0),
  One(charArrayOf('c', 'f'), 1),
  Two(charArrayOf('a', 'c', 'd', 'e', 'g'), 2),
  Three(charArrayOf('a', 'c', 'd', 'f', 'g'), 3),
  Four(charArrayOf('b', 'c', 'd', 'f'), 4), // eafb -> ef -> b = e, d = f
  Five(charArrayOf('a', 'b', 'd', 'f', 'g'), 5),
  Six(charArrayOf('a', 'b', 'd', 'e', 'f', 'g'), 6),
  Seven(charArrayOf('a', 'c', 'f'), 7), // a = d --- dab
  Eight(charArrayOf('a', 'b', 'c', 'd', 'e', 'f', 'g'), 8), // deaf_
  Nine(charArrayOf('a', 'b', 'c', 'd', 'f', 'g'), 9),
}

class Input(
  val entries: Array<String>,
  val values: Array<String>,
)