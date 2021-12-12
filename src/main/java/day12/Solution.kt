package day12

import common.FileReader

fun main() {
  val nodes = findNodes()

  findAllPaths(nodes, 1)
  findAllPaths(nodes, 2)
}

fun findAllPaths(
  nodes: List<Node>,
  canVisitSmall: Int,
) {
  var paths: Set<List<Node>>
  val start = nodes.first { it.isStart }

  paths = recurseThroughPaths(start, mutableListOf(), canVisitSmall)

  while (!paths.areAllPathsComplete()) {
    paths = paths.map { recurseThroughPaths(it.last(), it, canVisitSmall) }.flatten().toSet()
  }

  val set = paths.toSet()

  println("Found ${set.size} paths.")
  println("\n")
}

private fun Set<List<Node>>.areAllPathsComplete(): Boolean {
  return all { it.complete() }
}

private fun List<Node>.complete(): Boolean {
  return lastOrNull()?.isEnd ?: false
}

private fun recurseThroughPaths(
  startingNode: Node, visited: List<Node>, canVisitSmall: Int
): Set<List<Node>> {
  if (startingNode.isEnd) return setOf(visited)

  val paths = mutableSetOf<List<Node>>()
  val visitedSmallCaves = visited
    .filterNot { it.bigCave || it.isStart }
    .groupBy { it.name }
    .entries
    .filter { it.value.size > 1 }

  startingNode.connectedNodes.forEach { node ->
    if (node.isEnd) {
      paths += visited.addPath(startingNode, node)
    } else if (node.bigCave) {
      paths += visited.addPath(startingNode, node)
    } else if (!node.isStart) {
      val visitedAmount = visited.filter { it.name == node.name }.size

      // If never visited, we're allowed to visit.
      if (visitedAmount == 0) {
        paths += visited.addPath(startingNode, node)
      } else if (visitedSmallCaves.isEmpty() && visitedAmount < canVisitSmall) {
        paths += visited.addPath(startingNode, node)
      }
    }
  }

  return paths
}

private fun List<Node>.addPath(startingNode: Node, newNode: Node): List<Node> {
  return toMutableList().also {
    if (startingNode !in it) {
      it += startingNode
    }
    it += newNode
  }
}

private fun findNodes(): List<Node> {
  val allNodes = mutableListOf<Node>()
  FileReader.readFile("day12.txt").lines().forEach { line ->
    val nodes = line.split("-")

    require(nodes.size == 2)

    val leftNode = nodes[0].toNode(allNodes)
    val rightNode = nodes[1].toNode(allNodes)

    rightNode.connectedNodes += leftNode
    leftNode.connectedNodes += rightNode
  }

  return allNodes
}

private fun String.toNode(allNodes: MutableList<Node>): Node {
  val node = allNodes.firstOrNull { it.name == this } ?: Node(name = this).also {
    allNodes += it
  }

  return node
}

data class Node(val name: String) {
  val connectedNodes: MutableList<Node> = mutableListOf()

  val isStart = name == "start"
  val isEnd = name == "end"
  val bigCave = name.lowercase() != name
}