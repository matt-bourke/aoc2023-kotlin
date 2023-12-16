data class Tile(val x: Int, val y: Int, val tileType: Char, val beams: HashSet<Direction>)
typealias TileRow = ArrayList<Tile>
typealias TileMap = ArrayList<ArrayList<Tile>>

fun TileMap.reset() = this.flatten().forEach { tile -> tile.beams.clear() }

fun TileMap.get(current: Tile, direction: Direction): Tile? {
    return when (direction) {
        Direction.UP -> if (current.y > 0) this[current.y - 1][current.x] else null
        Direction.DOWN ->  if (current.y < this.size - 1) this[current.y + 1][current.x] else null
        Direction.LEFT -> if (current.x > 0) this[current.y][current.x - 1] else null
        Direction.RIGHT -> if (current.x < this.first().size - 1) this[current.y][current.x + 1] else null
        else -> throw Exception("no valid direction")
    }
}

fun TileMap.processTile(tile: Tile, direction: Direction, explored: HashSet<Tile>, unexplored: ArrayList<Tile>) {
    val next = this.get(tile, direction)
    if (next != null) {
        next.beams.add(direction)
        if (!explored.contains(next)) {
            unexplored.add(next)
        }
    }
}

fun calculateEnergy(tileMap: TileMap, start: Tile): Int {
    val explored = HashSet<Tile>()
    val unexplored = ArrayList<Tile>()
    unexplored.add(start)

    var currentDirection: Direction
    while (unexplored.isNotEmpty()) {
        val tile = unexplored.removeAt(0)
        for (beam in tile.beams) {
            currentDirection = beam
            when (tile.tileType) {
                '.' -> tileMap.processTile(tile, currentDirection, explored, unexplored)
                '/' -> {
                    currentDirection = when (currentDirection) {
                        Direction.UP -> Direction.RIGHT
                        Direction.DOWN -> Direction.LEFT
                        Direction.LEFT -> Direction.DOWN
                        Direction.RIGHT -> Direction.UP
                        else -> throw Exception("no direction")
                    }
                    tileMap.processTile(tile, currentDirection, explored, unexplored)
                }
                '\\' -> {
                    currentDirection = when (currentDirection) {
                        Direction.UP -> Direction.LEFT
                        Direction.DOWN -> Direction.RIGHT
                        Direction.LEFT -> Direction.UP
                        Direction.RIGHT -> Direction.DOWN
                        else -> throw Exception("no direction")
                    }
                    tileMap.processTile(tile, currentDirection, explored, unexplored)
                }
                '-' -> {
                    if (currentDirection == Direction.UP || currentDirection == Direction.DOWN) {
                        tileMap.processTile(tile, Direction.LEFT, explored, unexplored)
                        tileMap.processTile(tile, Direction.RIGHT, explored, unexplored)
                    } else {
                        tileMap.processTile(tile, currentDirection, explored, unexplored)
                    }
                }
                '|' -> {
                    if (currentDirection == Direction.LEFT || currentDirection == Direction.RIGHT) {
                        tileMap.processTile(tile, Direction.UP, explored, unexplored)
                        tileMap.processTile(tile, Direction.DOWN, explored, unexplored)
                    } else {
                        tileMap.processTile(tile, currentDirection, explored, unexplored)
                    }
                }
            }
            explored.add(tile)
        }
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
                row.add(Tile(x, y, tile, HashSet()))
            }
            tileMap.add(row)
        }

        val start = tileMap[0][0]
        start.beams.add(Direction.RIGHT)

        return calculateEnergy(tileMap, start)
    }

    fun part2(input: List<String>): Int {
        val tileMap = TileMap()
        for ((y, line) in input.withIndex()) {
            val row = TileRow()
            for ((x, tile) in line.withIndex()) {
                row.add(Tile(x, y, tile, HashSet()))
            }
            tileMap.add(row)
        }

        var maxEnergised = 0
        for (tile in tileMap.first()) {
            tile.beams.add(Direction.DOWN)
            val energy = calculateEnergy(tileMap, tile)
            if (energy > maxEnergised) {
                maxEnergised = energy
            }
            tileMap.reset()
        }
        for (tile in tileMap.last()) {
            tile.beams.add(Direction.UP)
            val energy = calculateEnergy(tileMap, tile)
            if (energy > maxEnergised) {
                maxEnergised = energy
            }
            tileMap.reset()
        }
        for (tile in tileMap.map { it[0] }) {
            tile.beams.add(Direction.RIGHT)
            val energy = calculateEnergy(tileMap, tile)
            if (energy > maxEnergised) {
                maxEnergised = energy
            }
            tileMap.reset()
        }
        for (tile in tileMap.map { it[tileMap.first().size - 1] }) {
            tile.beams.add(Direction.LEFT)
            val energy = calculateEnergy(tileMap, tile)
            if (energy > maxEnergised) {
                maxEnergised = energy
            }
            tileMap.reset()
        }

        return maxEnergised
    }

    val testInput = readInput("Day16_test")
    check(part1(testInput) == 46)
    check(part2(testInput) == 51)

    val input = readInput("Day16")
    part1(input).print("Part 1")
    part2(input).print("Part 2")
}