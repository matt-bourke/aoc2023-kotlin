import java.awt.Point
import java.util.PriorityQueue

data class Block(val pos: Point, val heatLoss: Int, var fScore: Int): Comparable<Block> {
    override fun compareTo(other: Block): Int {
        return this.fScore - other.fScore
    }

    override fun hashCode(): Int {
        return this.pos.hashCode()
    }
}
typealias BlockRow = ArrayList<Block>
typealias BlockGrid = ArrayList<BlockRow>

fun straightCount(current: Block, cameFrom: HashMap<Block, Block>): Pair<Direction, Int> {
    if (!cameFrom.containsKey(current)) return Pair(Direction.NONE, 0)

    var count = 0
    val cameFromDirection = current.from(cameFrom.getValue(current))
    var b = current
    while (cameFrom.containsKey(b) && b.from(cameFrom.getValue(b)) == cameFromDirection) {
        count++
        b = cameFrom.getValue(b)
    }
    return Pair(cameFromDirection, count)
}

fun Block.from(other: Block?): Direction {
    if (other == null) return Direction.NONE
    if (this.pos.x - other.pos.x == 1) return Direction.RIGHT
    if (this.pos.x - other.pos.x == -1) return Direction.LEFT
    if (this.pos.y - other.pos.y == 1) return Direction.UP
    if (this.pos.y - other.pos.y == -1) return Direction.DOWN
    return Direction.NONE
}

fun Block.getNeighbours(grid: BlockGrid, cameFrom: HashMap<Block, Block>): ArrayList<Block> {
    val neighbours = ArrayList<Block>()
    val (cameFromDirection, straightCount) = straightCount(this, cameFrom)
    when (cameFromDirection) {
        Direction.DOWN -> { // going up
            if (this.pos.y > 0 && straightCount < 3) {
                val neighbourToAdd = grid[this.pos.y - 1][this.pos.x]
                neighbours.add(neighbourToAdd)
            }
            if (this.pos.x > 0) {
                val neighbourToAdd = grid[this.pos.y][this.pos.x - 1]
                neighbours.add(neighbourToAdd)
            }
            if (this.pos.x < grid.first().size - 1) {
                val neighbourToAdd = grid[this.pos.y][this.pos.x + 1]
                neighbours.add(neighbourToAdd)
            }
        }

        Direction.UP -> { // going down
            if (this.pos.y < grid.size - 1 && straightCount < 3) {
                val neighbourToAdd = grid[this.pos.y + 1][this.pos.x]
                neighbours.add(neighbourToAdd)
            }
            if (this.pos.x > 0) {
                val neighbourToAdd = grid[this.pos.y][this.pos.x - 1]
                neighbours.add(neighbourToAdd)
            }
            if (this.pos.x < grid.first().size - 1) {
                val neighbourToAdd = grid[this.pos.y][this.pos.x + 1]
                neighbours.add(neighbourToAdd)
            }
        }

        Direction.LEFT -> { // going left
            if (this.pos.x > 0 && straightCount < 3) {
                val neighbourToAdd = grid[this.pos.y][this.pos.x - 1]
                neighbours.add(neighbourToAdd)
            }
            if (this.pos.y > 0) {
                val neighbourToAdd = grid[this.pos.y - 1][this.pos.x]
                neighbours.add(neighbourToAdd)
            }
            if (this.pos.y < grid.size - 1) {
                val neighbourToAdd = grid[this.pos.y + 1][this.pos.x]
                neighbours.add(neighbourToAdd)
            }
        }

        Direction.RIGHT -> { // going right
            if (this.pos.x < grid.first().size - 1 && straightCount < 3) {
                val neighbourToAdd = grid[this.pos.y][this.pos.x + 1]
                neighbours.add(neighbourToAdd)
            }
            if (this.pos.y > 0) {
                val neighbourToAdd = grid[this.pos.y - 1][this.pos.x]
                neighbours.add(neighbourToAdd)
            }
            if (this.pos.y < grid.size - 1) {
                val neighbourToAdd = grid[this.pos.y + 1][this.pos.x]
                neighbours.add(neighbourToAdd)
            }
        }

        Direction.NONE -> {
            if (this.pos.y > 0) {
                val neighbourToAdd = grid[this.pos.y + 1][this.pos.x]
                neighbours.add(neighbourToAdd)
            }
            if (this.pos.y < grid.size - 1) {
                val neighbourToAdd = grid[this.pos.y + 1][this.pos.x]
                neighbours.add(neighbourToAdd)
            }
            if (this.pos.x > 0) {
                val neighbourToAdd = grid[this.pos.y][this.pos.x - 1]
                neighbours.add(neighbourToAdd)
            }
            if (this.pos.x < grid.first().size - 1) {
                val neighbourToAdd = grid[this.pos.y][this.pos.x + 1]
                neighbours.add(neighbourToAdd)
            }
        }
    }
    return neighbours
}

fun reconstructPath(cameFrom: HashMap<Block, Block>, endpoint: Block): ArrayList<Block> {
    val path = ArrayList<Block>()
    path.addFirst(endpoint)
    var current = endpoint
    while (cameFrom.containsKey(current)) {
        current = cameFrom.getValue(current)
        path.addFirst(current)
    }
    return path
}

fun pathfind(grid: BlockGrid): ArrayList<Block> {
    val start = grid.first().first()
    val goal = grid.last().last()

    val openSet = PriorityQueue<Block>()
    openSet.add(start)

    val cameFrom = HashMap<Block, Block>()

    val gScores = HashMap<Block, Int>()
    for (block in grid.flatten()) {
        gScores[block] = Int.MAX_VALUE
    }
    gScores[start] = 0

    val fScores = HashMap<Block, Int>()
    for (block in grid.flatten()) {
        fScores[block] = Int.MAX_VALUE
    }
    val startF = manhattan(start.pos.x, start.pos.y, goal.pos.x, goal.pos.y)
    fScores[start] = startF
    start.fScore = startF

    while (openSet.isNotEmpty()) {
        val current = openSet.remove()
//        if (current.pos == goal.pos) {
//            println("found path")
//            return reconstructPath(cameFrom, current)
//        }

        val neighbours = current.getNeighbours(grid, cameFrom)
        for (neighbour in neighbours) {
            val tentativeG = gScores.getValue(current) + neighbour.heatLoss
            if (tentativeG < gScores.getValue(neighbour)) {
                cameFrom[neighbour] = current
                gScores[neighbour] = tentativeG
                val neighbourF = tentativeG + manhattan(neighbour.pos.x, neighbour.pos.y, goal.pos.x, goal.pos.y)
                fScores[neighbour] = neighbourF
                neighbour.fScore = neighbourF
                if (!openSet.contains(neighbour)) {
                    openSet.add(neighbour)
                }
            }
        }
    }

    for (row in grid) {
        for (block in row) {
            if (gScores.contains(block)) {
                print(gScores[block].toString().padStart(5, ' ').padEnd(9, ' '))
            } else {
                print("    .    ")
            }
        }
        println()
    }

    return reconstructPath(cameFrom, goal)
}

fun main() {
    fun part1(input: List<String>): Int {
        val grid = BlockGrid()
        for ((y, line) in input.withIndex()) {
            val blockRow = BlockRow()
            for ((x, block) in line.withIndex()) {
                blockRow.add(Block(Point(x, y), block.digitToInt(), Int.MAX_VALUE))
            }
            grid.add(blockRow)
        }

        val path = pathfind(grid)
        var heatSum = 0-grid[0][0].heatLoss
        for (row in grid) {
            for (block in row) {
                if (path.contains(block)) {
                    heatSum += block.heatLoss
                    val index = path.indexOf(block)
                    print(("$index($heatSum)").padStart(5, ' ').padEnd(9, ' '))
                } else {
                    print("    .    ")
                }
            }
            println()
        }

        val totalHeat = path.sumOf { it.heatLoss } - grid[0][0].heatLoss
        println(totalHeat)
        return totalHeat
    }

    fun part2(input: List<String>): Int {
        return input.size
    }

    val testInput = readInput("Day17_test")
    check(part1(testInput) == 102)
//    check(part2(testInput) == 0)

//    val input = readInput("Day17")
//    part1(input).print("Part 1")
//    part2(input).print("Part 2")
}