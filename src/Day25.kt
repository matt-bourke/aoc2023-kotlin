
fun main() {
    fun parse(input: List<String>): Map<String, Set<String>> {
        val graph = HashMap<String, Set<String>>()
        for (line in input) {
            val (node, connectionsList) = line.split(": ")
            val connections = connectionsList.split(" ")

            if (graph.containsKey(node)) {
                val existing = graph[node]
                val updated = existing!!.plus(connections)
                graph[node] = updated
            } else {
                graph[node] = connections.toSet()
            }

            for (connection in connections) {
                if (graph.containsKey(connection)) {
                    val existing = graph[connection]
                    val updated = existing!!.plus(node)
                    graph[connection] = updated
                } else {
                    graph[connection] = setOf(node)
                }
            }
        }

        return graph
    }

    fun floodFill(node: String, graph: Map<String, Set<String>>, edgeCounter: HashMap<Set<String>, Int>?): Int {
        val seen = HashSet<String>()
        val openSet = ArrayDeque<String>()
        openSet.add(node)

        while (openSet.isNotEmpty()) {
            val current = openSet.removeFirst()
            val connections = graph[current]!!
            for (connection in connections) {
                if (seen.contains(connection)) continue
                seen.add(connection)
                openSet.add(connection)

                if (edgeCounter != null) {
                    if (!edgeCounter.containsKey(setOf(current, connection))) {
                        edgeCounter[setOf(current, connection)] = 1
                    } else {
                        val existing = edgeCounter[setOf(current, connection)]!!
                        val updated = existing + 1
                        edgeCounter[setOf(current, connection)] = updated
                    }
                }
            }
        }

        return seen.size
    }

    fun Map<String, Set<String>>.snip(vararg edgesToSnip: Set<String>): Map<String, Set<String>> {
        val snippedGraph = HashMap<String, Set<String>>()
        for (entry in this) {
            val edge = edgesToSnip.firstOrNull { entry.key in it }
            if (edge == null) {
                snippedGraph[entry.key] = entry.value
            } else {
                snippedGraph[entry.key] = entry.value.minus(edge.minus(entry.key))
            }
        }

        return snippedGraph
    }

    fun part1(input: List<String>): Int {
        val graph = parse(input)
        val edgeCounter = HashMap<Set<String>, Int>()
        for (node in graph.keys) {
            floodFill(node, graph, edgeCounter)
        }

        val sortedEdges = edgeCounter.entries.sortedByDescending { it.value }

        for (x in 0..< edgeCounter.size) {
            for (y in x+1..< edgeCounter.size) {
                for (z in y+1..< edgeCounter.size) {
                    // remove edges and test
                    val snippedGraph = graph.snip(sortedEdges[x].key, sortedEdges[y].key, sortedEdges[z].key)
                    val seen = floodFill(snippedGraph.keys.first(), snippedGraph, null)
                    if (seen < graph.size) {
                        return seen * (graph.size - seen)
                    }
                }
            }
        }

        return input.size
    }

    val testInput = readInput("Day25_test")
    check(part1(testInput) == 54)

    val input = readInput("Day25")
    part1(input).print("Part 1")
}