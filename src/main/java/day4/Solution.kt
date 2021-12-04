package day4

import common.FileReader
import day4.player.LoserPlayer
import day4.player.Player
import day4.player.WinnerPlayer

fun main() {
  val game = parseGame()

  playGameWithPlayer(WinnerPlayer(game.copy()))
  playGameWithPlayer(LoserPlayer(game.copy()))
}

private fun playGameWithPlayer(player: Player) {
  printBoardAndScore(player.play())
}

private fun printBoardAndScore(board: Board) {
  println("$board")
  println("With a score of :: ${board.calculateScore()}")
}

private fun parseGame(): Game {
  val numbers = mutableListOf<Int>()
  val boards = mutableListOf<Board>()

  var board = Board()
  FileReader.readFile("day4.txt")
    .lines()
    // Now let's parse the rest.
    .forEachIndexed { index, string ->
      if (index == 0) {
        numbers += string.split(",").map { it.toInt() }
      } else {
        // Parse the board.
        if (string.isEmpty()) {
          // Add the board and create new one.
          // Unless it's the first one, in which case it would be empty.
          if (!board.isEmpty()) {
            require(board.isValid()) { "Invalid board." }

            boards += board
            board = Board()
          }
        } else {
          // Parse into a board.
          val row = string.split(" ")
            .filterNot { it.isEmpty() }
            .map { it.toInt() }
            .toIntArray()
          board.addRow(row)
        }
      }
    }
  // Let's not forget to add the last one.
  if (board.isValid() && board !in boards) {
    boards.add(board)
  }

  return Game(
    numbers = numbers,
    boards = boards
  )
}