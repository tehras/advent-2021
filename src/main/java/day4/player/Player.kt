package day4.player

import day4.Board

interface Player {
  /** Plays until the board is found. **/
  fun play(): Board
}