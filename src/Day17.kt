import java.util.PriorityQueue

fun main() {
    data class Block(val x: Int, val y: Int, val heatLoss: Int)
    data class State(val x: Int, val y: Int, val facing: Direction, val stepsTaken: Int)

    fun State.neighbours(width: Int, height: Int, minStep: Int, maxStep: Int): ArrayList<State> {
        val next = ArrayList<State>()
        when (facing) {
            Direction.UP -> {
                if (y > 0 && stepsTaken < maxStep) next.add(State(x, y - 1, Direction.UP, stepsTaken + 1))
                if (x > 0 && stepsTaken >= minStep) next.add(State(x - 1, y, Direction.LEFT, 1))
                if (x < width - 1 && stepsTaken >= minStep) next.add(State(x + 1, y, Direction.RIGHT, 1))
            }

            Direction.DOWN -> {
                if (y < height - 1 && stepsTaken < maxStep) next.add(State(x, y + 1, Direction.DOWN, stepsTaken + 1))
                if (x > 0 && stepsTaken >= minStep) next.add(State(x - 1, y, Direction.LEFT, 1))
                if (x < width - 1 && stepsTaken >= minStep) next.add(State(x + 1, y, Direction.RIGHT, 1))
            }

            Direction.LEFT -> {
                if (x > 0 && stepsTaken < maxStep) next.add(State(x - 1, y, Direction.LEFT, stepsTaken + 1))
                if (y > 0 && stepsTaken >= minStep) next.add(State(x, y - 1, Direction.UP, 1))
                if (y < height - 1 && stepsTaken >= minStep) next.add(State(x, y + 1, Direction.DOWN, 1))
            }

            Direction.RIGHT -> {
                if (x < width - 1 && stepsTaken < maxStep) next.add(State(x + 1, y, Direction.RIGHT, stepsTaken + 1))
                if (y > 0 && stepsTaken >= minStep) next.add(State(x, y - 1, Direction.UP, 1))
                if (y < height - 1 && stepsTaken >= minStep) next.add(State(x, y + 1, Direction.DOWN, 1))
            }

            Direction.NONE -> {
                // only applicable at start
                next.add(State(1, 0, Direction.RIGHT, 1))
                next.add(State(0, 1, Direction.DOWN, 1))
            }
        }
        return next
    }

    fun pathfindMinHeatLoss(grid: List<List<Block>>, minStep: Int, maxStep: Int): Int {
        val openSet = PriorityQueue<Pair<Int, State>>(compareBy { (cost, state) -> cost - state.y - state.x })
        val gScore = HashMap<State, Int>()

        val gridWidth = grid.first().size
        val gridHeight = grid.size
        val goal = grid.last().last()
        val start = State(0, 0, Direction.NONE, 0)

        openSet.add(Pair(0, start))

        while (openSet.isNotEmpty()) {
            val (cost, current) = openSet.poll()
            if (current.x == goal.x && current.y == goal.y) {
                return gScore[current] ?: Int.MAX_VALUE
            }

            for (n in current.neighbours(gridWidth, gridHeight, minStep, maxStep)) {
                val nextCost = cost + grid[n.y][n.x].heatLoss
                if (nextCost < gScore.getOrDefault(n, Int.MAX_VALUE)) {
                    gScore[n] = nextCost
                    openSet.add(Pair(nextCost, n))
                }
            }
        }

        return Int.MAX_VALUE
    }

    fun part1(input: List<String>): Int {
        val grid = input.mapIndexed { y, row ->
            row.mapIndexed { x, c -> Block(x, y, c.digitToInt()) }
        }
        return pathfindMinHeatLoss(grid, 0, 3)
    }

    fun part2(input: List<String>): Int {
        val grid = input.mapIndexed { y, row ->
            row.mapIndexed { x, c -> Block(x, y, c.digitToInt()) }
        }
        return pathfindMinHeatLoss(grid, 4, 10)
    }

    val testInput = readInput("Day17_test")
    check(part1(testInput) == 102)
    check(part2(testInput) == 94)

    val input = readInput("Day17")
    part1(input).print("Part 1")
    part2(input).print("Part 2")
}