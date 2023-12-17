data class Beam(val x: Int, val y: Int, val direction: Direction)
data class Tile(val x: Int, val y: Int, val tileType: Char)
typealias TileRow = ArrayList<Tile>
typealias TileMap = ArrayList<ArrayList<Tile>>

fun TileMap.getNext(current: Beam, direction: Direction): Beam? {
    return when (direction) {
        Direction.UP -> if (current.y > 0) Beam(current.x, current.y - 1, direction) else null
        Direction.DOWN ->  if (current.y < this.size - 1) Beam(current.x, current.y + 1, direction) else null
        Direction.LEFT -> if (current.x > 0) Beam(current.x - 1, current.y, direction) else null
        Direction.RIGHT -> if (current.x < this.first().size - 1) Beam(current.x + 1, current.y, direction) else null
        else -> throw Exception("no valid direction")
    }
}

fun TileMap.processBeam(beam: Beam, direction: Direction, explored: HashSet<Beam>, unexplored: ArrayList<Beam>) {
    val next = this.getNext(beam, direction)
    if (next != null) {
        if (!explored.contains(next)) {
            unexplored.add(next)
        }
    }
}

fun calculateEnergy(tileMap: TileMap, start: Beam): Int {
    val explored = HashSet<Beam>()
    val unexplored = ArrayList<Beam>()
    unexplored.add(start)

    var currentDirection: Direction
    while (unexplored.isNotEmpty()) {
        val beam = unexplored.removeFirst()
        currentDirection = beam.direction
        when (tileMap[beam.y][beam.x].tileType) {
            '.' -> tileMap.processBeam(beam, currentDirection, explored, unexplored)
            '/' -> {
                currentDirection = when (currentDirection) {
                    Direction.UP -> Direction.RIGHT
                    Direction.DOWN -> Direction.LEFT
                    Direction.LEFT -> Direction.DOWN
                    Direction.RIGHT -> Direction.UP
                    else -> throw Exception("no direction")
                }
                tileMap.processBeam(beam, currentDirection, explored, unexplored)
            }
            '\\' -> {
                currentDirection = when (currentDirection) {
                    Direction.UP -> Direction.LEFT
                    Direction.DOWN -> Direction.RIGHT
                    Direction.LEFT -> Direction.UP
                    Direction.RIGHT -> Direction.DOWN
                    else -> throw Exception("no direction")
                }
                tileMap.processBeam(beam, currentDirection, explored, unexplored)
            }
            '-' -> {
                if (currentDirection == Direction.UP || currentDirection == Direction.DOWN) {
                    tileMap.processBeam(beam, Direction.LEFT, explored, unexplored)
                    tileMap.processBeam(beam, Direction.RIGHT, explored, unexplored)
                } else {
                    tileMap.processBeam(beam, currentDirection, explored, unexplored)
                }
            }
            '|' -> {
                if (currentDirection == Direction.LEFT || currentDirection == Direction.RIGHT) {
                    tileMap.processBeam(beam, Direction.UP, explored, unexplored)
                    tileMap.processBeam(beam, Direction.DOWN, explored, unexplored)
                } else {
                    tileMap.processBeam(beam, currentDirection, explored, unexplored)
                }
            }
        }
        explored.add(beam)
    }

    val uniqueExplored = HashSet<Pair<Int, Int>>()
    for (tile in explored) {
        uniqueExplored.add(Pair(tile.x, tile.y))
    }

    return uniqueExplored.size
}

fun main() {
    fun part1(input: List<String>): Int {
        val tileMap = TileMap()
        for ((y, line) in input.withIndex()) {
            val row = TileRow()
            for ((x, tile) in line.withIndex()) {
                row.add(Tile(x, y, tile))
            }
            tileMap.add(row)
        }

        val start = Beam(0, 0, Direction.RIGHT)
        return calculateEnergy(tileMap, start)
    }

    fun part2(input: List<String>): Int {
        val tileMap = TileMap()
        for ((y, line) in input.withIndex()) {
            val row = TileRow()
            for ((x, tile) in line.withIndex()) {
                row.add(Tile(x, y, tile))
            }
            tileMap.add(row)
        }

        val startingBeams = ArrayList<Beam>()
        for (x in tileMap.first().indices) {
            startingBeams.add(Beam(x, 0, Direction.DOWN))
            startingBeams.add(Beam(x, tileMap.size - 1, Direction.UP))
        }
        for (y in tileMap.indices) {
            startingBeams.add(Beam(0, y, Direction.RIGHT))
            startingBeams.add(Beam(tileMap.first().size - 1, y, Direction.LEFT))
        }

        return startingBeams.maxOfOrNull { start ->
            calculateEnergy(tileMap, start)
        }!!
    }

    val testInput = readInput("Day16_test")
    check(part1(testInput) == 46)
    check(part2(testInput) == 51)

    val input = readInput("Day16")
    part1(input).print("Part 1")
    part2(input).print("Part 2")
}