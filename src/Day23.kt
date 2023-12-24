import java.util.PriorityQueue

fun main() {
    data class Point(val x: Int, val y: Int, val c: Char)
    fun Point.neighbours(map: List<List<Point>>, cameFrom: HashMap<Point, Point>): ArrayList<Point> {
        val neighbours = ArrayList<Point>()
        if ((x - 1) in map.first().indices &&
            cameFrom[this] != Point(x - 1, y, map[y][x-1].c) &&
            (map[y][x-1].c == '.' || map[y][x-1].c == '<')) {
            neighbours.add(Point(x - 1, y, map[y][x-1].c))
        }
        if ((x + 1) in map.first().indices &&
            cameFrom[this] != Point(x + 1, y, map[y][x+1].c) &&
            (map[y][x+1].c == '.' || map[y][x+1].c == '>')) {
            neighbours.add(Point(x + 1, y, map[y][x+1].c))
        }
        if ((y - 1) in map.indices &&
            cameFrom[this] != Point(x, y - 1, map[y-1][x].c) &&
            (map[y-1][x].c == '.' || map[y-1][x].c == '^')) {
            neighbours.add(Point(x, y - 1, map[y-1][x].c))
        }
        if ((y + 1) in map.indices &&
            cameFrom[this] != Point(x, y+1, map[y+1][x].c) &&
            (map[y+1][x].c == '.' || map[y+1][x].c == 'v')) {
            neighbours.add(Point(x, y + 1, map[y+1][x].c))
        }
        return neighbours
    }

    fun part1(input: List<String>): Int {
        val grid = input.mapIndexed { y, row ->
            row.mapIndexed { x, el -> Point(x, y, el) }
        }
        val start = grid.first().single { it.c == '.' }
        val end = grid.last().single { it.c == '.' }

        val openSet = PriorityQueue<Pair<Int, Point>>(compareByDescending { it.first })
        val gScore = HashMap<Point, Int>()
        val cameFrom = HashMap<Point, Point>()

        openSet.add(Pair(0, start))
        gScore[start] = 0

        while (openSet.isNotEmpty()) {
            val current = openSet.poll()
            val neighbours = current.second.neighbours(grid, cameFrom)
            for (n in neighbours) {
                val nextCost = current.first + 1
                if (nextCost > gScore.getOrDefault(n, 0)) {
                    gScore[n] = nextCost
                    cameFrom[n] = current.second
                    openSet.add(Pair(nextCost, n))
                }
            }
        }

        return gScore.getValue(end)
    }

    data class State(val current: Point, var history: Set<Point>, val cameFrom: Direction)
    fun State.neighbourinos(map: List<List<Point>>): ArrayList<State> {
        val neighbours = ArrayList<State>()
        val x = current.x
        val y = current.y
        if ((x - 1) in map.first().indices) {
            val left = map[y][x-1]
            if (left.c != '#' &&
                cameFrom != Direction.LEFT &&
                !history.contains(left)) {
                neighbours.add(State(left, history, Direction.RIGHT))
            }
        }

        if ((x + 1) in map.first().indices) {
            val right = map[y][x+1]
            if (right.c != '#' &&
                cameFrom != Direction.RIGHT &&
                !history.contains(right)) {
                neighbours.add(State(right, history, Direction.LEFT))
            }
        }

        if ((y - 1) in map.indices) {
            val up = map[y - 1][x]
            if (up.c != '#' &&
                cameFrom != Direction.UP &&
                !history.contains(up)) {
                neighbours.add(State(up, history, Direction.DOWN))
            }
        }

        if ((y + 1) in map.indices) {
            val down = map[y+1][x]
            if (down.c != '#' &&
                cameFrom != Direction.DOWN &&
                !history.contains(down)) {
                neighbours.add(State(down, history, Direction.UP))
            }
        }

        return neighbours
    }

    fun dfs(grid: List<List<Point>>, start: State): Int {
        var current = start
        var distance = 1

        var neighbours = current.neighbourinos(grid)
        if (neighbours.isEmpty()) {
            return if (current.current.y != grid.size - 1) {
                0
            } else {
                distance
            }
        }

        while (neighbours.count() == 1) {
            current = neighbours.first()
            distance++
            neighbours = current.neighbourinos(grid)
        }

        if (neighbours.isEmpty()) {
            return if (current.current.y != grid.size - 1) {
                0
            } else {
                distance
            }
        } else {
            for (n in neighbours) {
                if (neighbours.count() > 1) {
                    // at a junction, add junction to history
                    n.history = current.history.plus(current.current)
                }
            }
        }

        var maxSplit = 0
        for (n in neighbours) {
            val split = dfs(grid, n)
            if (split > maxSplit) {
                maxSplit = split
            }
        }

        if (maxSplit == 0) {
            // no splits, found the end
            return 0
        }

        return distance + maxSplit
    }

    fun part2(input: List<String>): Int {
        val grid = input.mapIndexed { y, row ->
            row.mapIndexed { x, el -> Point(x, y, el) }
        }

        val start = grid.first().single { it.c == '.' }
        val startState = State(start, HashSet(), Direction.NONE)
        val longest = dfs(grid, startState)

        return longest - 1
    }

    val testInput = readInput("Day23_test")
    check(part1(testInput) == 94)
    check(part2(testInput) == 154)

    val input = readInput("Day23")
    part1(input).print("Part 1")
    part2(input).print("Part 2")
}