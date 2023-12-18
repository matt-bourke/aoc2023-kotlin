import java.awt.Point
import java.util.PriorityQueue

fun main() {

    data class Vertex(val pos: Point, val heatLoss: Int)
    data class Cell(val posX: Int, val posY: Int, val facing: Direction, val stepsTaken: Int, var score: Int): Comparable<Cell> {
        override fun hashCode(): Int {
            return super.hashCode()
        }

        override fun compareTo(other: Cell): Int {
            return this.score - other.score
        }

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as Cell

            if (posX != other.posX) return false
            if (posY != other.posY) return false
            if (facing != other.facing) return false
            if (stepsTaken != other.stepsTaken) return false

            return true
        }
    }

    fun Cell.neighbours(grid: ArrayList<ArrayList<Vertex>>): ArrayList<Cell> {
        val neighbours = ArrayList<Cell>()
        when (this.facing) {
            Direction.DOWN -> {
                if (this.posY < grid.size - 1 && this.stepsTaken < 3) {
                    val neighbourToAdd = grid[this.posY + 1][this.posX]
                    val cellToAdd = Cell(neighbourToAdd.pos.x, neighbourToAdd.pos.y, Direction.DOWN, this.stepsTaken + 1, 0)
                    neighbours.add(cellToAdd)
                }
                if (this.posX > 0) {
                    val neighbourToAdd = grid[this.posY][this.posX - 1]
                    val cellToAdd = Cell(neighbourToAdd.pos.x, neighbourToAdd.pos.y, Direction.LEFT, 1, 0)
                    neighbours.add(cellToAdd)
                }
                if (this.posX < grid.first().size - 1) {
                    val neighbourToAdd = grid[this.posY][this.posX + 1]
                    val cellToAdd = Cell(neighbourToAdd.pos.x, neighbourToAdd.pos.y, Direction.RIGHT, 1, 0)
                    neighbours.add(cellToAdd)
                }
            }
            Direction.UP -> {
                if (this.posY > 0 && this.stepsTaken < 3) {
                    val neighbourToAdd = grid[this.posY - 1][this.posX]
                    val cellToAdd = Cell(neighbourToAdd.pos.x, neighbourToAdd.pos.y, Direction.UP, this.stepsTaken + 1, 0)
                    neighbours.add(cellToAdd)
                }
                if (this.posX > 0) {
                    val neighbourToAdd = grid[this.posY][this.posX - 1]
                    val cellToAdd = Cell(neighbourToAdd.pos.x, neighbourToAdd.pos.y, Direction.LEFT, 1, 0)
                    neighbours.add(cellToAdd)
                }
                if (this.posX < grid.first().size - 1) {
                    val neighbourToAdd = grid[this.posY][this.posX + 1]
                    val cellToAdd = Cell(neighbourToAdd.pos.x, neighbourToAdd.pos.y, Direction.RIGHT, 1, 0)
                    neighbours.add(cellToAdd)
                }
            }
            Direction.LEFT -> {
                if (this.posX > 0 && this.stepsTaken < 3) {
                    val neighbourToAdd = grid[this.posY][this.posX - 1]
                    val cellToAdd = Cell(neighbourToAdd.pos.x, neighbourToAdd.pos.y, Direction.LEFT, this.stepsTaken + 1, 0)
                    neighbours.add(cellToAdd)
                }
                if (this.posY > 0) {
                    val neighbourToAdd = grid[this.posY - 1][this.posX]
                    val cellToAdd = Cell(neighbourToAdd.pos.x, neighbourToAdd.pos.y, Direction.UP, 1, 0)
                    neighbours.add(cellToAdd)
                }
                if (this.posY < grid.size - 1) {
                    val neighbourToAdd = grid[this.posY + 1][this.posX]
                    val cellToAdd = Cell(neighbourToAdd.pos.x, neighbourToAdd.pos.y, Direction.DOWN, 1, 0)
                    neighbours.add(cellToAdd)
                }
            }
            Direction.RIGHT -> {
                if (this.posX < grid.first().size - 1 && this.stepsTaken < 3) {
                    val neighbourToAdd = grid[this.posY][this.posX + 1]
                    val cellToAdd = Cell(neighbourToAdd.pos.x, neighbourToAdd.pos.y, Direction.RIGHT, this.stepsTaken + 1, 0)
                    neighbours.add(cellToAdd)
                }
                if (this.posY > 0) {
                    val neighbourToAdd = grid[this.posY - 1][this.posX]
                    val cellToAdd = Cell(neighbourToAdd.pos.x, neighbourToAdd.pos.y, Direction.UP, 1, 0)
                    neighbours.add(cellToAdd)
                }
                if (this.posY < grid.size - 1) {
                    val neighbourToAdd = grid[this.posY + 1][this.posX]
                    val cellToAdd = Cell(neighbourToAdd.pos.x, neighbourToAdd.pos.y, Direction.DOWN, 1, 0)
                    neighbours.add(cellToAdd)
                }
            }
            Direction.NONE -> {
                if (this.posY > 0) {
                    val neighbourToAdd = grid[this.posY - 1][this.posX]
                    val cellToAdd = Cell(neighbourToAdd.pos.x, neighbourToAdd.pos.y, Direction.UP, 1, 0)
                    neighbours.add(cellToAdd)
                }
                if (this.posY < grid.size - 1) {
                    val neighbourToAdd = grid[this.posY + 1][this.posX]
                    val cellToAdd = Cell(neighbourToAdd.pos.x, neighbourToAdd.pos.y, Direction.DOWN, 1, 0)
                    neighbours.add(cellToAdd)
                }
                if (this.posX > 0) {
                    val neighbourToAdd = grid[this.posY][this.posX - 1]
                    val cellToAdd = Cell(neighbourToAdd.pos.x, neighbourToAdd.pos.y, Direction.LEFT, 1, 0)
                    neighbours.add(cellToAdd)
                }
                if (this.posX < grid.first().size - 1) {
                    val neighbourToAdd = grid[this.posY][this.posX + 1]
                    val cellToAdd = Cell(neighbourToAdd.pos.x, neighbourToAdd.pos.y, Direction.RIGHT, 1, 0)
                    neighbours.add(cellToAdd)
                }
            }
        }
        return neighbours
    }

    fun pathfind(grid: ArrayList<ArrayList<Vertex>>): ArrayList<Vertex> {
        val gScore = HashMap<Cell, Int>()
        val cameFrom = HashMap<Cell, Cell>()
        val openSet = PriorityQueue<Cell>()
        val visited = HashSet<Cell>()

        val start = Cell(0, 0, Direction.NONE, 0, 0)
        openSet.add(start)
        gScore[start] = 0

        while (openSet.isNotEmpty()) {
            val current = openSet.first()
            openSet.remove(current)

            val neighbours = current.neighbours(grid)
            for (n in neighbours) {
                val gridPoint = grid.flatten().single { it.pos.x == n.posX && it.pos.y == n.posY }
                val alt = gScore[current]!! + gridPoint.heatLoss
                cameFrom[n] = current
                gScore[n] = alt
                n.score = alt
                if (openSet.none { n == it } && visited.none { n == it }) {
                    openSet.add(n)
                }
            }
            visited.add(current)
        }
        val goalPos = grid.last().last().pos
        val end = gScore.filter { it.key.posX == goalPos.x && it.key.posY == goalPos.y }
            .minBy { it.value }

        var current: Cell? = end.key
        val path = ArrayList<Vertex>()
        while (current != null) {
            val gridPoint = grid.flatten().single { it.pos.x == current!!.posX && it.pos.y == current!!.posY}
            path.addFirst(gridPoint)
            current = cameFrom[current]
        }

        return path
    }

    fun part1(input: List<String>): Int {
        val grid = ArrayList<ArrayList<Vertex>>()
        for ((y, line) in input.withIndex()) {
            val row = ArrayList<Vertex>()
            for ((x, vertex) in line.withIndex()) {
                row.add(Vertex(Point(x, y), vertex.digitToInt()))
            }
            grid.add(row)
        }

        val path = pathfind(grid)

        for (row in grid) {
            for (node in row) {
                if (path.contains(node)) {
                    print("#")
                } else {
                    print(".")
                }
            }
            println()
        }

        val heatTotal = path.sumOf { it.heatLoss } - grid[0][0].heatLoss
        println(heatTotal)

        return heatTotal
    }

    fun part2(input: List<String>): Int {
        return input.size
    }

    val testInput = readInput("Day17_test")
    check(part1(testInput) == 102)
//    check(part2(testInput) == 0)

    val input = readInput("Day17")
    part1(input).print("Part 1")
//    part2(input).print("Part 2")
}