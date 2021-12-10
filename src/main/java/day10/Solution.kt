package day10

import common.FileReader

fun main() {
  val lines = getLines()

  findIllegalChars(lines)
}

fun findIllegalChars(lines: List<CharArray>) {

  val illegalChars = mutableListOf<Char>()
  val completeChars = mutableListOf<List<Char>>()

  lines.forEach Lines@{ line ->
    // let's find first illegal character.
    // We need to track start tag.
    val tagStack = Stack<Char>()

    line.forEach Line@{ c ->
      if (c in startTags) {
        // That means it's a start, so we're going to add it to the stack.
        tagStack.push(c)
      } else if (c in endTags) {
        // Let's check if the latest opener matches.
        val lastOpenTag = tagStack.peek() ?: error("Cannot")

        if (startTags.indexOf(lastOpenTag) == endTags.indexOf(c)) {
          // Matches, let's pop.
          tagStack.pop()
        } else {
          illegalChars += c
          val expectedEndTag = lastOpenTag.endTag()

          println("Found illegal char : $c where it should have been $expectedEndTag in line ${line.concatToString()}")
          return@Lines
        }
      } else {
        error("Invalid character :: $c")
      }
    }

    // Let's calculate complete chars.
    completeChars += tagStack.toCompletedChars()
  }

  println("All illegal chars added up to ${illegalChars.toPoints()} points")
  println("\n\n")
  println("All additional chars added up to ${completeChars.toPoints()} points")
}

private fun Stack<Char>.toCompletedChars(): List<Char> {
  val completedChars = mutableListOf<Char>()

  while (peek() != null) {
    val popped = pop() ?: error("Oops something went bad with the stack?")
    completedChars += popped.endTag()
  }

  println("Found some chars to complete :: ${completedChars.toCharArray().concatToString()}")

  return completedChars
}

private fun Char.endTag(): Char {
  return endTags[startTags.indexOf(this)]
}

private fun List<Char>.toPoints(): Int {
  return sumOf { points[it] ?: error("Could not find point for $it") }
}

private fun List<List<Char>>.toPoints(): Long {
  // Calculate is prev * 5 + value
  val sortedResults = map { charList ->
    charList.scan(0L) { current, c ->
      (current * 5L) + (completePoints[c] ?: error("Could not find complete points for $c"))
    }.last().toLong().also {
      println("${charList.toCharArray().concatToString()} - $it points")
    }
  }.sortedDescending()

  return sortedResults[sortedResults.size / 2]
}

private fun getLines(): List<CharArray> {
  return FileReader.readFile("day10.txt")
    .lines()
    .map { it.toCharArray() }
}

private val tags = setOf(
  '[' to ']',
  '(' to ')',
  '{' to '}',
  '<' to '>',
)

private val points = mapOf(
  ')' to 3,
  ']' to 57,
  '}' to 1197,
  '>' to 25137
)

private val completePoints = mapOf(
  ')' to 1,
  ']' to 2,
  '}' to 3,
  '>' to 4
)

val startTags = tags.map { it.first }
val endTags = tags.map { it.second }

class Stack<T> {
  private val storage = arrayListOf<T>()

  fun push(element: T) = storage.add(element)

  fun pop(): T? = storage.removeLastOrNull()

  fun peek(): T? = storage.lastOrNull()
}