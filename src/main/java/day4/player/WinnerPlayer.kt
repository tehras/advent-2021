package day4.player

import day4.Board
import day4.Game

class WinnerPlayer(
  private val game: Game
) : Player {
  init {
    println("Starting the game. Looking for the best board.")
  }

  override fun play(): Board {
    // Keep track of numbers called.
    game.numbers.forEach { number ->
      game.boards.forEach { board ->
        if (board.playNumber(number)) {
          // Board won.
          return board
        }
      }
    }

    error("No winning boards found")
  }
}