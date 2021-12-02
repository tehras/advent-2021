package day2

import common.FileReader

fun main() {
  val movements = parseThroughFile()

  parseMovement(movements).print()
  parseMovementWithAim(movements).print()
}

private fun parseMovementWithAim(movements: List<Movement>): Result {
  var horizontalMovement = 0
  var verticalMovement = 0
  var aim = 0

  movements.forEach { movement ->
    when (movement.direction) {
      Direction.Up -> aim -= movement.distance
      Direction.Down -> aim += movement.distance
      Direction.Forward -> {
        horizontalMovement += movement.distance
        verticalMovement += aim * movement.distance
      }
    }
  }

  return Result(horizontal = horizontalMovement, vertical = verticalMovement)
}

private fun parseMovement(movements: List<Movement>): Result {
  var horizontalMovement = 0
  var verticalMovement = 0

  movements.forEach { movement ->
    when (movement.direction) {
      Direction.Up -> verticalMovement -= movement.distance
      Direction.Down -> verticalMovement += movement.distance
      Direction.Forward -> horizontalMovement += movement.distance
    }
  }

  return Result(horizontal = horizontalMovement, vertical = verticalMovement)
}

private fun parseThroughFile(): List<Movement> {
  return FileReader.readFile("day2.txt")
    .lines()
    .mapLines()
}

private fun List<String>.mapLines(): List<Movement> {
  return map { line ->
    val split = line.split(" ")

    require(split.size == 2) { "$line is not valid format" }

    val direction = split[0].toDirection()
    val distance = split[1].toInt()

    Movement(
      direction = direction,
      distance = distance
    )
  }
}

private fun String.toDirection(): Direction {
  return Direction.valueOf(this.replaceFirstChar { it.uppercase() })
}

data class Result(
  val horizontal: Int,
  val vertical: Int
) {
  private val total = horizontal * vertical

  fun print() {
    println("Submarine traveled : $horizontal horizontally and $vertical vertically")
    println("This yields a total of $total")
  }
}

data class Movement(
  val direction: Direction,
  val distance: Int
)

enum class Direction {
  Up,
  Down,
  Forward
}
