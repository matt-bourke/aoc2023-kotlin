enum class Direction {
    NONE, LEFT, UP, RIGHT, DOWN
}

data class Pipe(val position: Pair<Int, Int>, var pipeType: Char, val cameFrom: Direction)

fun Pipe.findNextPipe(pipeMap: ArrayList<ArrayList<Pipe>>): Pipe {
    val x = this.position.first
    val y = this.position.second
    return when (this.pipeType) {
        '-' -> {
            if (this.cameFrom == Direction.LEFT) {
                val rightPipe = pipeMap[y][x + 1]
                Pipe(rightPipe.position, rightPipe.pipeType, Direction.LEFT)
            } else {
                val leftPipe = pipeMap[y][x - 1]
                Pipe(leftPipe.position, leftPipe.pipeType, Direction.RIGHT)
            }
        }
        '|' -> {
            if (this.cameFrom == Direction.UP) {
                val downPipe = pipeMap[y + 1][x]
                Pipe(downPipe.position, downPipe.pipeType, Direction.UP)
            } else {
                val upPipe = pipeMap[y - 1][x]
                Pipe(upPipe.position, upPipe.pipeType, Direction.DOWN)
            }
        }
        'L' -> {
            if (this.cameFrom == Direction.UP) {
                val rightPipe = pipeMap[y][x + 1]
                Pipe(rightPipe.position, rightPipe.pipeType, Direction.LEFT)
            } else {
                val upPipe = pipeMap[y - 1][x]
                Pipe(upPipe.position, upPipe.pipeType, Direction.DOWN)
            }
        }
        'J' -> {
            if (this.cameFrom == Direction.UP) {
                val leftPipe = pipeMap[y][x - 1]
                Pipe(leftPipe.position, leftPipe.pipeType, Direction.RIGHT)
            } else {
                val upPipe = pipeMap[y - 1][x]
                Pipe(upPipe.position, upPipe.pipeType, Direction.DOWN)
            }
        }
        '7' -> {
            if (this.cameFrom == Direction.DOWN) {
                val leftPipe = pipeMap[y][x - 1]
                Pipe(leftPipe.position, leftPipe.pipeType, Direction.RIGHT)
            } else {
                val downPipe = pipeMap[y + 1][x]
                return Pipe(downPipe.position, downPipe.pipeType, Direction.UP)
            }
        }
        'F' -> {
            if (this.cameFrom == Direction.DOWN) {
                val rightPipe = pipeMap[y][x + 1]
                Pipe(rightPipe.position, rightPipe.pipeType, Direction.LEFT)
            } else {
                val downPipe = pipeMap[y + 1][x]
                return Pipe(downPipe.position, downPipe.pipeType, Direction.UP)
            }
        }
        else -> Pipe(Pair(0, 0), '.', Direction.UP) // default yucky
    }
}

typealias Pipes = ArrayList<Pipe>

fun ArrayList<Pipes>.get(pipe: Pipe, direction: Direction): Pipe? {
    return when (direction) {
        Direction.UP -> {
            if (pipe.position.second > 0) {
                this[pipe.position.second - 1][pipe.position.first]
            } else {
                null
            }
        }
        Direction.LEFT -> {
            if (pipe.position.first > 0) {
                this[pipe.position.second][pipe.position.first - 1]
            } else {
                null
            }
        }
        Direction.RIGHT -> {
            if (pipe.position.first < this.first().size - 1) {
                this[pipe.position.second][pipe.position.first + 1]
            } else {
                null
            }
        }
        Direction.DOWN -> {
            if (pipe.position.second < this.size - 1) {
                this[pipe.position.second + 1][pipe.position.first]
            } else {
                null
            }
        }
        Direction.NONE -> throw Exception("None cannot exist here")
    }
}

fun getInitialPathPipes(pipes: ArrayList<Pipes>, startLocation: Pair<Int, Int>): Pair<Pipe, Pipe> {
    val startPipe = pipes.flatten().single { it.position == startLocation }
    var path1 = Pipe(Pair(0, 0), '.', Direction.UP)
    var path2 = Pipe(Pair(0, 0), '.', Direction.UP)

    pipes.get(startPipe, Direction.LEFT)?.let { leftPipe ->
        if (leftPipe.pipeType in arrayOf('-', 'L', 'F')) {
            path1 = Pipe(leftPipe.position, leftPipe.pipeType, Direction.RIGHT)
        }
    }

    pipes.get(startPipe, Direction.UP)?.let { upPipe ->
        if (upPipe.pipeType in arrayOf('|', '7', 'F')) {
            if (path1.pipeType == '.') {
                path1 = Pipe(upPipe.position, upPipe.pipeType, Direction.DOWN)
            } else {
                path2 = Pipe(upPipe.position, upPipe.pipeType, Direction.DOWN)
            }
        }
    }

    pipes.get(startPipe, Direction.RIGHT)?.let { rightPipe ->
        if (rightPipe.pipeType in arrayOf('-', 'J', '7')) {
            if (path1.pipeType == '.') {
                path1 = Pipe(rightPipe.position, rightPipe.pipeType, Direction.LEFT)
            } else {
                path2 = Pipe(rightPipe.position, rightPipe.pipeType, Direction.LEFT)
            }
        }
    }

    pipes.get(startPipe, Direction.DOWN)?.let { downPipe ->
        if (downPipe.pipeType in arrayOf('|', 'J', 'L')) {
            if (path1.pipeType == '.') {
                path1 = Pipe(downPipe.position, downPipe.pipeType, Direction.UP)
            } else {
                path2 = Pipe(downPipe.position, downPipe.pipeType, Direction.UP)
            }
        }
    }

    return Pair(path1, path2)
}

fun main() {
    fun part1(input: List<String>): Int {
        var start = Pair(0, 0)
        val pipes = ArrayList<Pipes>()
        for (y in input.indices) {
            val pipeRow = Pipes()
            for (x in input[y].indices) {
                pipeRow.add(Pipe(Pair(x, y), input[y][x], Direction.NONE))
                if (input[y][x] == 'S') {
                    start = Pair(x, y)
                }
            }
            pipes.add(pipeRow)
        }

        var (path1, path2) = getInitialPathPipes(pipes, start)
        var steps = 1
        while (path1.position != path2.position) {
            path1 = path1.findNextPipe(pipes)
            path2 = path2.findNextPipe(pipes)
            steps++
        }

        return steps
    }

    fun part2(input: List<String>): Int {
        val pipeMap = ArrayList<ArrayList<Pipe>>()
        for (y in input.indices) {
            val pipeRow = ArrayList<Pipe>()
            for (x in input[y].indices) {
                pipeRow.add(Pipe(Pair(x, y), input[y][x], Direction.NONE))
            }
            pipeMap.add(pipeRow)
        }

        for ((index, rows) in pipeMap.windowed(2).withIndex()) {
            val row1 = rows[0]
            val row2 = rows[1]
            val rowToInsert = ArrayList<Pipe>()
            for ((i, pipes) in row1.zip(row2).withIndex()) {
                if (pipes.first.pipeType in arrayOf('|', 'F', '7', 'S') &&
                    pipes.second.pipeType in arrayOf('|', 'J', 'L', 'S')) {
                    rowToInsert.add(Pipe(Pair(i, index + 1), '|', Direction.NONE))
                } else {
                    rowToInsert.add(Pipe(Pair(i, index + 1), '.', Direction.NONE))
                }
            }
            pipeMap.add(2 * index + 1, rowToInsert)
        }

        for ((row, pipeRow) in pipeMap.withIndex()) {
            for ((index, pipes) in pipeRow.windowed(2).withIndex()) {
                if (pipes[0].pipeType in arrayOf('-', 'F', 'L', 'S') &&
                    pipes[1].pipeType in arrayOf('-', 'J', '7', 'S')) {
                    pipeRow.add(2 * index + 1, Pipe(Pair(index, row), '-', Direction.NONE))
                } else {
                    pipeRow.add(2 * index + 1, Pipe(Pair(index, row), '.', Direction.NONE))
                }
            }
        }

        var start = Pair(0, 0)
        val pipesRemapped = ArrayList<ArrayList<Pipe>>()
        for ((y, pipeRow) in pipeMap.withIndex()) {
            val remappedPipeRow = ArrayList<Pipe>()
            for ((x, pipe) in pipeRow.withIndex()) {
                remappedPipeRow.add(Pipe(Pair(x, y), pipe.pipeType, Direction.NONE))
                if (pipe.pipeType == 'S') {
                    start = Pair(x, y)
                }
            }
            pipesRemapped.add(remappedPipeRow)
        }

        var (path1, path2) = getInitialPathPipes(pipesRemapped, start)
        val mainLoop = HashSet<Pair<Int, Int>>()
        mainLoop.add(start)
        mainLoop.add(path1.position)
        mainLoop.add(path2.position)

        while (path1.position != path2.position) {
            path1 = path1.findNextPipe(pipesRemapped)
            path2 = path2.findNextPipe(pipesRemapped)
            mainLoop.add(path1.position)
            mainLoop.add(path2.position)
        }

        for ((y, pipeRow) in pipeMap.withIndex()) {
            for ((x, pipe) in pipeRow.withIndex()) {
                if (!mainLoop.contains(Pair(x, y))) {
                    pipe.pipeType = '.'
                }
            }
        }

        // flood fill causes stack overflow, trying slow loops instead
        for (pipe in pipeMap.first().union(pipeMap.last())
            .union(pipeMap.map { it.first() }).union(pipeMap.map { it.last() })) {
            if (pipe.pipeType == '.') {
                pipe.pipeType = 'O'
            }
        }

        var changed = true
        while (changed) {
            changed = false
            for ((y, pipeRow) in pipeMap.withIndex()) {
                for ((x, pipe) in pipeRow.withIndex()) {
                    if ((pipe.pipeType == '.') &&
                        ((y > 0 && pipeMap[y - 1][x].pipeType == 'O') ||
                         (y < pipeMap.size - 1 && pipeMap[y + 1][x].pipeType == 'O') ||
                         (x > 0 && pipeMap[y][x - 1].pipeType == 'O') ||
                         (x < pipeMap[0].size - 1 && pipeMap[y][x + 1].pipeType == 'O'))) {
                        pipe.pipeType = 'O'
                        changed = true
                    }
                }
            }
        }

        // deflate map again
        pipeMap.reversed().withIndex().forEach { (i, pipes) -> if (i % 2 == 1) pipeMap.remove(pipes) }
        pipeMap.forEach { pipeRow ->
            pipeRow.reversed().withIndex().forEach { (i, pipe) -> if (i % 2 == 1) pipeRow.remove(pipe) }
        }

        return pipeMap.flatten().count { it.pipeType == '.' }
    }

    var testInput = readInput("Day10_test")
    check(part1(testInput) == 8)
    testInput = readInput("Day10_Part2_test")
    check(part2(testInput) == 10)

    val input = readInput("Day10")
    part1(input).print("Part 1")
    part2(input).print("Part 2")
}