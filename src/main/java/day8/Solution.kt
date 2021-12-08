package day8

import common.FileReader
import day8.Number.*

fun main() {
  val inputs = parseFile()

  decodeInput(inputs)
}

private fun decodeInput(inputs: List<Input>) {
  val numbers = Number.values()

  // We want to come up with an algorithm that will encode a [Number] based on other
  // entries in the array. This relies on having access to all the entries.
  val decoderMap = mutableMapOf<Long, Number>()
  val numberEntries = numbers.map { it.chars }

  // This will loop through Number.values() and creating encoding for it.
  // We later will do the same for each [Input] - and match the encoding, and pull out of the encoding map.
  encodeEntries(numberEntries) { array, encoding ->
    decoderMap[encoding] = numbers.find { it.chars.matchesCharArray(array) }
      ?: error("Could not find entry for array $array")
  }

  // Now that we got a decoder map, we have to encode every single Input, and then we can compare.
  val total = inputs.sumOf {
    sumInput(it, decoderMap)
  }

  println("Total :: $total")
}

fun sumInput(input: Input, decoderMap: MutableMap<Long, Number>): Int {
  val entries = input.entries.map { it.toCharArray() }
  val arrayToNumberMap = mutableMapOf<CharArray, Number>()

  encodeEntries(entries) { array, encoding ->
    arrayToNumberMap[array] = decoderMap[encoding] ?: error("Could not match encoding $encoding")
  }

  val values = input.values.map { it.toCharArray().sortedArray() }

  return values
    .map {
      arrayToNumberMap.findCharArray(it)?.value
        ?: error("Something went wrong, could not decode for $it")
    }
    .map { it.digitToChar() }
    .toCharArray()
    .concatToString()
    .toInt()
}

private fun encodeEntries(entries: List<CharArray>, block: (CharArray, Long) -> Unit) {
  val sorted = entries.map { it.sortedArray() }
    .sortedBy { it.size }

  for (i in sorted.first().size..sorted.last().size) {
    val matchingByLength = sorted.filter { it.size == i }

    when {
      matchingByLength.isEmpty() -> Unit // Do nothing.
      matchingByLength.size == 1 -> {
        // Only 1 match, so to simplify we're going to encode single.
        val encoded = encodeSingle(matchingByLength[0])
        block(matchingByLength[0], encoded)
      }
      else -> {
        // When multiple.
        matchingByLength.forEach { array ->
          val encoded = encodeMultiple(array, sorted)
          block(array, encoded)
        }
      }
    }
  }
}

fun encodeMultiple(chars: CharArray, all: List<CharArray>): Long {
  var total = 0L
  // The hash will be chars length + similarities with others.
  all.forEach { array ->
    // Multiply by index in order to differentiate if only 2 numbers.
    total += chars
      .sumOf {
        if (array.contains(it)) {
          1L
        } else {
          0L
        }
      }
  }

  return total
}

fun encodeSingle(chars: CharArray): Long {
  return chars.size.toLong()
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

private fun CharArray.matchesCharArray(charArray: CharArray): Boolean {
  return charArray.sortedArray().concatToString() == sortedArray().concatToString()
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