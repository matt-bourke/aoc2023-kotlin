data class Pipe(val position: Pair<Int, Int>, var pipeType: Char)

fun Pipe.isHorizontalTop(): Boolean {
    return this.pipeType in arrayOf('-', 'L', 'J')
}

fun Pipe.isHorizontalBottom(): Boolean {
    return this.pipeType in arrayOf('-', 'F', '7')
}

fun Pipe.isVerticalLeft(): Boolean {
    return this.pipeType in arrayOf('|', 'J', '7')
}

fun Pipe.isVerticalRight(): Boolean {
    return this.pipeType in arrayOf('|', 'F', 'L')
}

enum class Direction {
    LEFT,
    UP,
    RIGHT,
    DOWN
}
data class PathPipe(val position: Pair<Int, Int>, val pipeType: Char, val cameFrom: Direction)

fun findNextPipe(currentPipe: PathPipe, pipeMap: ArrayList<ArrayList<Pipe>>): PathPipe {
    val x = currentPipe.position.first
    val y = currentPipe.position.second
    return when (currentPipe.pipeType) {
        '-' -> {
            if (currentPipe.cameFrom == Direction.LEFT) {
                val rightPipe = pipeMap[y][x + 1]
                PathPipe(rightPipe.position, rightPipe.pipeType, Direction.LEFT)
            } else {
                val leftPipe = pipeMap[y][x - 1]
                PathPipe(leftPipe.position, leftPipe.pipeType, Direction.RIGHT)
            }
        }
        '|' -> {
            if (currentPipe.cameFrom == Direction.UP) {
                val downPipe = pipeMap[y + 1][x]
                PathPipe(downPipe.position, downPipe.pipeType, Direction.UP)
            } else {
                val upPipe = pipeMap[y - 1][x]
                PathPipe(upPipe.position, upPipe.pipeType, Direction.DOWN)
            }
        }
        'L' -> {
            if (currentPipe.cameFrom == Direction.UP) {
                val rightPipe = pipeMap[y][x + 1]
                PathPipe(rightPipe.position, rightPipe.pipeType, Direction.LEFT)
            } else {
                val upPipe = pipeMap[y - 1][x]
                PathPipe(upPipe.position, upPipe.pipeType, Direction.DOWN)
            }
        }
        'J' -> {
            if (currentPipe.cameFrom == Direction.UP) {
                val leftPipe = pipeMap[y][x - 1]
                PathPipe(leftPipe.position, leftPipe.pipeType, Direction.RIGHT)
            } else {
                val upPipe = pipeMap[y - 1][x]
                PathPipe(upPipe.position, upPipe.pipeType, Direction.DOWN)
            }
        }
        '7' -> {
            if (currentPipe.cameFrom == Direction.DOWN) {
                val leftPipe = pipeMap[y][x - 1]
                PathPipe(leftPipe.position, leftPipe.pipeType, Direction.RIGHT)
            } else {
                val downPipe = pipeMap[y + 1][x]
                return PathPipe(downPipe.position, downPipe.pipeType, Direction.UP)
            }
        }
        'F' -> {
            if (currentPipe.cameFrom == Direction.DOWN) {
                val rightPipe = pipeMap[y][x + 1]
                PathPipe(rightPipe.position, rightPipe.pipeType, Direction.LEFT)
            } else {
                val downPipe = pipeMap[y + 1][x]
                return PathPipe(downPipe.position, downPipe.pipeType, Direction.UP)
            }
        }

        else -> PathPipe(Pair(0, 0), '.', Direction.UP) // default yucky
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
    }
}

fun main() {
    fun part1(input: List<String>): Int {
        var start = Pair(0, 0)
        val pipes = ArrayList<Pipes>()
        for (y in input.indices) {
            val pipeRow = Pipes()
            for (x in input[y].indices) {
                pipeRow.add(Pipe(Pair(x, y), input[y][x]))
                if (input[y][x] == 'S') {
                    start = Pair(x, y)
                }
            }
            pipes.add(pipeRow)
        }

        val startPipe = pipes.flatten().single { it.position == start }
        var path1 = PathPipe(Pair(0, 0), '.', Direction.UP)
        var path2 = PathPipe(Pair(0, 0), '.', Direction.UP)

        // check cardinal directions at S to get both paths
        val leftPipe = pipes.get(startPipe, Direction.LEFT)
        if (leftPipe != null && leftPipe.pipeType in arrayOf('-', 'L', 'F')) {
            path1 = PathPipe(leftPipe.position, leftPipe.pipeType, Direction.RIGHT)
        }

        val upPipe = pipes.get(startPipe, Direction.UP)
        if (upPipe != null && upPipe.pipeType in arrayOf('|', '7', 'F')) {
            if (path1.pipeType == '.') {
                path1 = PathPipe(upPipe.position, upPipe.pipeType, Direction.DOWN)
            } else {
                path2 = PathPipe(upPipe.position, upPipe.pipeType, Direction.DOWN)
            }
        }

        val rightPipe = pipes.get(startPipe, Direction.RIGHT)
        if (rightPipe != null && rightPipe.pipeType in arrayOf('-', 'J', '7')) {
            if (path1.pipeType == '.') {
                path1 = PathPipe(rightPipe.position, rightPipe.pipeType, Direction.LEFT)
            } else {
                path2 = PathPipe(rightPipe.position, rightPipe.pipeType, Direction.LEFT)
            }
        }

        val downPipe = pipes.get(startPipe, Direction.DOWN)
        if (downPipe != null && downPipe.pipeType in arrayOf('|', 'J', 'L')) {
            if (path1.pipeType == '.') {
                path1 = PathPipe(downPipe.position, downPipe.pipeType, Direction.UP)
            } else {
                path2 = PathPipe(downPipe.position, downPipe.pipeType, Direction.UP)
            }
        }

        var steps = 1
        while (path1.position != path2.position) {
            path1 = findNextPipe(path1, pipes)
            path2 = findNextPipe(path2, pipes)
            steps++
        }

        return steps
    }



    fun part2(input: List<String>): Int {
        val pipeMap = ArrayList<ArrayList<Pipe>>()
        for (y in input.indices) {
            val pipeRow = ArrayList<Pipe>()
            for (x in input[y].indices) {
                pipeRow.add(Pipe(Pair(x, y), input[y][x]))
            }
            pipeMap.add(pipeRow)
        }

        val rowsToInflate = ArrayList<Int>()
        val colsToInflate = ArrayList<Int>()

        for ((index, rows) in pipeMap.windowed(2).withIndex()) {
            val row1 = rows[0]
            val row2 = rows[1]
            for (pipes in row1.zip(row2)) {
                if (pipes.first.isHorizontalTop() && pipes.second.isHorizontalBottom()) {
                    // inflate row i + 1
                    rowsToInflate.add(index + 1)
                    break
                }
            }
        }

        for ((rowOffset, row) in rowsToInflate.withIndex()) {
            val rowToInsert = ArrayList<Pipe>()
            for (i in input[0].indices) {
                if (pipeMap[row - 1 + rowOffset][i].pipeType in arrayOf('|', 'F', '7', 'S') &&
                    pipeMap[row + rowOffset][i].pipeType in arrayOf('|', 'J', 'L', 'S')) {
                    rowToInsert.add(Pipe(Pair(i, row), '|'))
                } else {
                    rowToInsert.add(Pipe(Pair(i, row), '.'))
                }
            }
            pipeMap.add(row + rowOffset, rowToInsert)
        }

        for ((index, cols) in pipeMap[0].indices.windowed(2).withIndex()) {
            val firstCol = pipeMap.map { it[cols[0]] }
            val secondCol = pipeMap.map { it[cols[1]] }

            for (pipes in firstCol.zip(secondCol)) {
                if (pipes.first.isVerticalLeft() && pipes.second.isVerticalRight()) {
                    colsToInflate.add(index + 1)
                    break
                }
            }
        }

        for ((offset, col) in colsToInflate.withIndex()) {
            val colToInsert = ArrayList<Pipe>()
            for (i in pipeMap.indices) {
                if (pipeMap[i][col - 1 + offset].pipeType in arrayOf('-', 'F', 'L', 'S') &&
                    pipeMap[i][col + offset].pipeType in arrayOf('-', 'J', '7', 'S')) {
                    colToInsert.add(Pipe(Pair(i, col), '-'))
                } else {
                    colToInsert.add(Pipe(Pair(i, col), '.'))
                }
            }

            for ((idx, pipes) in pipeMap.withIndex()) {
                val pipeToInsert = colToInsert[idx]
                pipes.add(col + offset, pipeToInsert)
            }
        }

        var start = Pair(0, 0)
        val pipesRemapped = ArrayList<ArrayList<Pipe>>()
        for ((y, pipeRow) in pipeMap.withIndex()) {
            val remappedPipeRow = ArrayList<Pipe>()
            for ((x, pipe) in pipeRow.withIndex()) {
                remappedPipeRow.add(Pipe(Pair(x, y), pipe.pipeType))
                if (pipe.pipeType == 'S') {
                    start = Pair(x, y)
                }
            }
            pipesRemapped.add(remappedPipeRow)
        }

        var path1 = PathPipe(Pair(0, 0), '.', Direction.UP)
        var path2 = PathPipe(Pair(0, 0), '.', Direction.UP)

        // check cardinal directions at S to get both paths
        if (start.first > 0) {
            val leftPipe = pipesRemapped[start.second][start.first - 1]
            if (leftPipe.pipeType in arrayOf('-', 'L', 'F')) {
                path1 = PathPipe(leftPipe.position, leftPipe.pipeType, Direction.RIGHT)
            }
        }

        if (start.second > 0) {
            val upPipe = pipesRemapped[start.second - 1][start.first]
            if (upPipe.pipeType in arrayOf('|', '7', 'F')) {
                if (path1.pipeType == '.') {
                    path1 = PathPipe(upPipe.position, upPipe.pipeType, Direction.DOWN)
                } else {
                    path2 = PathPipe(upPipe.position, upPipe.pipeType, Direction.DOWN)
                }
            }
        }

        if (start.first < pipesRemapped[0].size - 1) {
            val rightPipe = pipesRemapped[start.second][start.first + 1]
            if (rightPipe.pipeType in arrayOf('-', 'J', '7')) {
                if (path1.pipeType == '.') {
                    path1 = PathPipe(rightPipe.position, rightPipe.pipeType, Direction.LEFT)
                } else {
                    path2 = PathPipe(rightPipe.position, rightPipe.pipeType, Direction.LEFT)
                }
            }
        }

        if (start.second < pipesRemapped.size - 1) {
            val downPipe = pipesRemapped[start.second + 1][start.first]
            if (downPipe.pipeType in arrayOf('|', 'J', 'L')) {
                if (path1.pipeType == '.') {
                    path1 = PathPipe(downPipe.position, downPipe.pipeType, Direction.UP)
                } else {
                    path2 = PathPipe(downPipe.position, downPipe.pipeType, Direction.UP)
                }
            }
        }

        val mainLoop = HashSet<Pair<Int, Int>>()
        mainLoop.add(start)
        mainLoop.add(path1.position)
        mainLoop.add(path2.position)

        while (path1.position != path2.position) {
            // path 1 find next pipe
            val nextPath1 = findNextPipe(path1, pipesRemapped)

            // path 2 find next pipe
            val nextPath2 = findNextPipe(path2, pipesRemapped)

            if (nextPath1 == path2 || nextPath2 == path1) {
                break
            }

            path1 = nextPath1
            path2 = nextPath2

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

        // flood fill causes stack overflow
        // trying slow loops instead
        for (pipe in pipeMap.first()) {
            if (pipe.pipeType == '.') {
                pipe.pipeType = 'O'
            }
        }

        for (pipe in pipeMap.last()) {
            if (pipe.pipeType == '.') {
                pipe.pipeType = 'O'
            }
        }

        for (pipe in pipeMap.map { it[0] }) {
            if (pipe.pipeType == '.') {
                pipe.pipeType = 'O'
            }
        }

        for (pipe in pipeMap.map { it[pipeMap[0].size - 1] }) {
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
        colsToInflate.forEach {
            for (pipeRow in pipeMap) {
                pipeRow.removeAt(it)
            }
        }
        rowsToInflate.forEach { pipeMap.removeAt(it) }

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
