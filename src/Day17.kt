import java.awt.Point
import java.util.PriorityQueue

fun main() {
    fun part1(input: List<String>): Int {
        data class Vertex(val pos: Point, val heatLoss: Int)
        data class Cell(val posX: Int, val posY: Int, val facing: Direction, val stepsTaken: Int, var score: Int) :
            Comparable<Cell> {
            override fun hashCode(): Int {
                var result = posX
                result += posY * 142
                result = 5 * result + facing.ordinal
                result = 4 * result + stepsTaken
                return result
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
                        neighbours.add(Cell(this.posX, this.posY + 1, Direction.DOWN, this.stepsTaken + 1, 0))
                    }
                    if (this.posX > 0) {
                        neighbours.add(Cell(this.posX - 1, this.posY, Direction.LEFT, 1, 0))
                    }
                    if (this.posX < grid.first().size - 1) {
                        neighbours.add(Cell(this.posX + 1, this.posY, Direction.RIGHT, 1, 0))
                    }
                }

                Direction.UP -> {
                    if (this.posY > 0 && this.stepsTaken < 3) {
                        neighbours.add(Cell(this.posX, this.posY - 1, Direction.UP, this.stepsTaken + 1, 0))
                    }
                    if (this.posX > 0) {
                        neighbours.add(Cell(this.posX - 1, this.posY, Direction.LEFT, 1, 0))
                    }
                    if (this.posX < grid.first().size - 1) {
                        neighbours.add(Cell(this.posX + 1, this.posY, Direction.RIGHT, 1, 0))
                    }
                }

                Direction.LEFT -> {
                    if (this.posX > 0 && this.stepsTaken < 3) {
                        neighbours.add(Cell(this.posX - 1, this.posY, Direction.LEFT, this.stepsTaken + 1, 0))
                    }
                    if (this.posY > 0) {
                        neighbours.add(Cell(this.posX, this.posY - 1, Direction.UP, 1, 0))
                    }
                    if (this.posY < grid.size - 1) {
                        neighbours.add(Cell(this.posX, this.posY + 1, Direction.DOWN, 1, 0))
                    }
                }

                Direction.RIGHT -> {
                    if (this.posX < grid.first().size - 1 && this.stepsTaken < 3) {
                        neighbours.add(Cell(this.posX + 1, this.posY, Direction.RIGHT, this.stepsTaken + 1, 0))
                    }
                    if (this.posY > 0) {
                        neighbours.add(Cell(this.posX, this.posY - 1, Direction.UP, 1, 0))
                    }
                    if (this.posY < grid.size - 1) {
                        neighbours.add(Cell(this.posX, this.posY + 1, Direction.DOWN, 1, 0))
                    }
                }

                Direction.NONE -> {
                    if (this.posY > 0) {
                        neighbours.add(Cell(this.posX, this.posY - 1, Direction.UP, 1, 0))
                    }
                    if (this.posY < grid.size - 1) {
                        neighbours.add(Cell(this.posX, this.posY + 1, Direction.DOWN, 1, 0))
                    }
                    if (this.posX > 0) {
                        neighbours.add(Cell(this.posX - 1, this.posY, Direction.LEFT, 1, 0))
                    }
                    if (this.posX < grid.first().size - 1) {
                        neighbours.add(Cell(this.posX + 1, this.posY, Direction.RIGHT, 1, 0))
                    }
                }
            }
            return neighbours
        }

        fun pathfind(grid: ArrayList<ArrayList<Vertex>>, cells: ArrayList<Cell>): ArrayList<Vertex> {
            val gScore = HashMap<Cell, Int>()
            val cameFrom = HashMap<Cell, Cell>()
            val openSet = PriorityQueue<Cell>(cells.size + 1)

            for (cell in cells) {
                openSet.add(cell)
                gScore[cell] = Int.MAX_VALUE
            }

            val start = Cell(0, 0, Direction.NONE, 0, 0)
            gScore[start] = 0
            openSet.add(start)

            while (openSet.isNotEmpty()) {
                val current = openSet.remove()
                val neighbours = current.neighbours(grid)
                for (n in neighbours) {
                    if (openSet.contains(n)) {
                        try {
                            val alt = gScore[current]!! + grid[n.posY][n.posX].heatLoss
                            if (alt < (gScore[n] ?: Int.MAX_VALUE)) {
                                cameFrom[n] = current
                                gScore[n] = alt
                                openSet.remove(n)
                                n.score = alt
                                openSet.add(n)
                            }
                        } catch (e: Exception) {
                            println("broke")
                        }
                    }
                }
            }

            val goalPos = grid.last().last().pos
            val end = gScore.filter { it.key.posX == goalPos.x && it.key.posY == goalPos.y }
                .minBy { it.value }

            var current: Cell? = end.key
            val path = ArrayList<Vertex>()
            while (current != null) {
                val gridPoint = grid.flatten().single { it.pos.x == current!!.posX && it.pos.y == current!!.posY }
                path.addFirst(gridPoint)
                current = cameFrom[current]
            }

            return path
        }

        val grid = ArrayList<ArrayList<Vertex>>()
        val cells = ArrayList<Cell>()
        for ((y, line) in input.withIndex()) {
            val row = ArrayList<Vertex>()
            for ((x, vertex) in line.withIndex()) {
                row.add(Vertex(Point(x, y), vertex.digitToInt()))
                for (d in Direction.entries) {
                    if (d != Direction.NONE) {
                        for (s in 1..3) {
                            cells.add(Cell(x, y, d, s, Int.MAX_VALUE))
                        }
                    }
                }
            }
            grid.add(row)
        }

        val cellHash = cells.groupBy { it.hashCode() }
        println(cellHash.filter { it.value.size > 1 })

        val path = pathfind(grid, cells)

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
        data class Vertex(val pos: Point, val heatLoss: Int)
        data class Cell(val posX: Int, val posY: Int, val facing: Direction, val stepsTaken: Int, var score: Int) :
            Comparable<Cell> {
            override fun hashCode(): Int {
                var result = posX
                result += posY * 142
                result = 5 * result + facing.ordinal
                result = 10 * result + stepsTaken
                return result
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
                    if (this.posY < grid.size - 1 && this.stepsTaken < 10) {
                        neighbours.add(Cell(this.posX, this.posY + 1, Direction.DOWN, this.stepsTaken + 1, 0))
                    }
                    if (this.posX >= 4 && this.stepsTaken >= 4) {
                        neighbours.add(Cell(this.posX - 1, this.posY, Direction.LEFT, 1, 0))
                    }
                    if (this.posX <= grid.first().size - 4 && this.stepsTaken >= 4) {
                        neighbours.add(Cell(this.posX + 1, this.posY, Direction.RIGHT, 1, 0))
                    }
                }

                Direction.UP -> {
                    if (this.posY > 0 && this.stepsTaken < 10) {
                        neighbours.add(Cell(this.posX, this.posY - 1, Direction.UP, this.stepsTaken + 1, 0))
                    }
                    if (this.posX >= 4 && this.stepsTaken >= 4) {
                        neighbours.add(Cell(this.posX - 1, this.posY, Direction.LEFT, 1, 0))
                    }
                    if (this.posX <= grid.first().size - 4 && this.stepsTaken >= 4) {
                        neighbours.add(Cell(this.posX + 1, this.posY, Direction.RIGHT, 1, 0))
                    }
                }

                Direction.LEFT -> {
                    if (this.posX > 0 && this.stepsTaken < 10) {
                        neighbours.add(Cell(this.posX - 1, this.posY, Direction.LEFT, this.stepsTaken + 1, 0))
                    }
                    if (this.posY >= 4 && this.stepsTaken >= 4) {
                        neighbours.add(Cell(this.posX, this.posY - 1, Direction.UP, 1, 0))
                    }
                    if (this.posY <= grid.size - 4 && this.stepsTaken >= 4) {
                        neighbours.add(Cell(this.posX, this.posY + 1, Direction.DOWN, 1, 0))
                    }
                }

                Direction.RIGHT -> {
                    if (this.posX < grid.first().size - 1 && this.stepsTaken < 10) {
                        neighbours.add(Cell(this.posX + 1, this.posY, Direction.RIGHT, this.stepsTaken + 1, 0))
                    }
                    if (this.posY >= 4 && this.stepsTaken >= 4) {
                        neighbours.add(Cell(this.posX, this.posY - 1, Direction.UP, 1, 0))
                    }
                    if (this.posY <= grid.size - 4 && this.stepsTaken >= 4) {
                        neighbours.add(Cell(this.posX, this.posY + 1, Direction.DOWN, 1, 0))
                    }
                }

                Direction.NONE -> {
                    if (this.posY > 0) {
                        neighbours.add(Cell(this.posX, this.posY - 1, Direction.UP, 1, 0))
                    }
                    if (this.posY < grid.size - 1) {
                        neighbours.add(Cell(this.posX, this.posY + 1, Direction.DOWN, 1, 0))
                    }
                    if (this.posX > 0) {
                        neighbours.add(Cell(this.posX - 1, this.posY, Direction.LEFT, 1, 0))
                    }
                    if (this.posX < grid.first().size - 1) {
                        neighbours.add(Cell(this.posX + 1, this.posY, Direction.RIGHT, 1, 0))
                    }
                }
            }
            return neighbours
        }

        fun pathfind(grid: ArrayList<ArrayList<Vertex>>, cells: ArrayList<Cell>): ArrayList<Vertex> {
            val gScore = HashMap<Cell, Int>()
            val cameFrom = HashMap<Cell, Cell>()
            val openSet = PriorityQueue<Cell>(cells.size + 1)

            for (cell in cells) {
                openSet.add(cell)
                gScore[cell] = Int.MAX_VALUE
            }

            val start = Cell(0, 0, Direction.NONE, 0, 0)
            gScore[start] = 0
            openSet.add(start)

            val startingSetSize = openSet.size

            while (openSet.isNotEmpty()) {

                val currentSetSize = openSet.size
                if (currentSetSize % (startingSetSize/100) == 0) {
                    println("${100 * (1-(currentSetSize/startingSetSize.toFloat()))}%")
                }

                val current = openSet.remove()
                val neighbours = current.neighbours(grid)
                for (n in neighbours) {
                    if (openSet.contains(n)) {
                        try {
                            val alt = gScore[current]!! + grid[n.posY][n.posX].heatLoss
                            if (alt < (gScore[n] ?: Int.MAX_VALUE)) {
                                cameFrom[n] = current
                                gScore[n] = alt
                                openSet.remove(n)
                                n.score = alt
                                openSet.add(n)
                            }
                        } catch (e: Exception) {
                            println("broke")
                        }
                    }
                }
            }

            val goalPos = grid.last().last().pos
            val end = gScore.filter { it.key.posX == goalPos.x && it.key.posY == goalPos.y }
                .minBy { it.value }

            var current: Cell? = end.key
            val path = ArrayList<Vertex>()
            while (current != null) {
                val gridPoint = grid.flatten().single { it.pos.x == current!!.posX && it.pos.y == current!!.posY }
                path.addFirst(gridPoint)
                current = cameFrom[current]
            }

            return path
        }

        val grid = ArrayList<ArrayList<Vertex>>()
        val cells = ArrayList<Cell>()
        val width = input.first().length - 1
        val height = input.size - 1
        for ((y, line) in input.withIndex()) {
            val row = ArrayList<Vertex>()
            for ((x, vertex) in line.withIndex()) {
                row.add(Vertex(Point(x, y), vertex.digitToInt()))

                val minSteps = 4
                val maxSteps = 10

                var lowerBound = (minSteps - y).coerceAtLeast(1)
                for (s in lowerBound..maxSteps) {
                    cells.add(Cell(x, y, Direction.UP, s, 0))
                }

                lowerBound = (minSteps - (height - y)).coerceAtLeast(1)
                for (s in lowerBound..maxSteps) {
                    cells.add(Cell(x, y, Direction.DOWN, s, 0))
                }

                lowerBound = (minSteps - x).coerceAtLeast(1)
                for (s in lowerBound..maxSteps) {
                    cells.add(Cell(x, y, Direction.LEFT, s, 0))
                }

                lowerBound = (minSteps - (width - x)).coerceAtLeast(1)
                for (s in lowerBound..maxSteps) {
                    cells.add(Cell(x, y, Direction.RIGHT, s, 0))
                }
            }
            grid.add(row)
        }

        val cellHash = cells.groupBy { it.hashCode() }
        println(cellHash.filter { it.value.size > 1 })

        val path = pathfind(grid, cells)

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

    val testInput = readInput("Day17_test")
    check(part1(testInput) == 102)
    check(part2(testInput) == 94)

    val input = readInput("Day17")
    part1(input).print("Part 1")
    part2(input).print("Part 2")
}