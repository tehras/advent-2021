package day6

import common.FileReader

fun main() {
  val fish = parseFile()

  println("There are originally ${fish.count()} fish in the ocean.")
  advanceByDays(days = 256, originalFish = fish)
  advanceByDays(days = 80, originalFish = fish)
  advanceByDaysLegacy(days = 256, originalFish = fish)
}

private fun advanceByDays(days: Int, originalFish: Array<Fish>) {
  var fishMap = sortedMapOf<Int, Long>()
  originalFish.forEach {
    val currentCount = fishMap.getOrDefault(it.initialAge, 0)
    fishMap[it.initialAge] = currentCount + 1
  }

  for (day in 1..days) {
    val newFishMap = sortedMapOf<Int, Long>()
    // New fish.
    fishMap.keys.reversed().forEach { key ->
      if (key == 0) {
        newFishMap[8] = fishMap[key]!!
        newFishMap[6] = newFishMap.getOrDefault(6, 0) + fishMap[key]!!
      } else {
        newFishMap[key - 1] = fishMap[key]!!
      }
    }
    fishMap = newFishMap

    println("After day $day there are ${fishMap.values.sum()} fish in the ocean.")
  }

  println("Fish count after $days :: ${fishMap.values.sum()}")
}

/**
 * This solution won't work, I think we end up running out of memory.
 */
private fun advanceByDaysLegacy(days: Int, originalFish: Array<Fish>) {
  val fish = originalFish.toMutableList()

  for (day in 1..days) {
    val newFish = mutableListOf<Fish>()
    fish.forEach { f ->
      f.advanceByADay()?.let { newFish += it }
    }

    fish += newFish

    println("After day $day there are ${fish.size} fish.")
  }

  println("There are ${fish.size} fish.")
}

private fun parseFile(): Array<Fish> {
  return FileReader.readFile("day6.txt")
    .split(",")
    .map { it.toInt() }
    .map { Fish(initialAge = it) }
    .toTypedArray()
}

data class Fish(
  val initialAge: Int
) {
  // Initially it should be the initial age.
  private var daysTillReproduction: Long = initialAge.toLong()

  fun advanceByADay(): Fish? {
    return if (daysTillReproduction == 0L) {
      daysTillReproduction = 6
      Fish(initialAge = 8)
    } else {
      daysTillReproduction--
      null
    }
  }
}