package day4.player

import day4.Board
import day4.Game

class LoserPlayer(
  private val game: Game
) : Player {
  init {
    println("Starting the game. Looking for the worst board.")
  }

  override fun play(): Board {
    // Make sure it's a set.
    val boardsWon = mutableSetOf<Board>()
    val totalBoards = game.boards.size

    game.numbers.forEach { number ->
      game.boards.forEach { board ->
        if (board.playNumber(number)) {
          boardsWon += board

          if (boardsWon.size == totalBoards) {
            return board
          }
        }
      }
    }

    error("No boards found")
  }
}