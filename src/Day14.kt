typealias Platform = ArrayList<String>

fun Platform.tiltNorth(): Platform = this.transpose().slideValues().transpose()
fun Platform.tiltSouth(): Platform = this.transpose().flip().slideValues().flip().transpose()
fun Platform.tiltEast(): Platform = this.flip().slideValues().flip()
fun Platform.tiltWest(): Platform = this.slideValues()

fun Platform.slideValues(): Platform {
    for (i in this.indices) {
        var row = this[i]
        var vacant = 0
        for ((index, element) in row.withIndex()) {
            when (element) {
                '#' -> vacant = index + 1
                'O' -> {
                    row = row.replaceRange(index, index + 1, ".")
                    row = row.replaceRange(vacant, vacant + 1, "O")
                    vacant++
                }
            }
        }
        this[i] = row
    }
    return this
}

fun main() {
    fun part1(input: List<String>): Int {
        val tilted = ArrayList(input).tiltNorth()
        return tilted.mapIndexed { i, row -> row.count { it == 'O' } * (tilted.size - i) }.sum()
    }

    fun part2(input: List<String>): Int {
        var platform = ArrayList(input)
        val memo = HashMap<ArrayList<String>, Pair<ArrayList<String>, Int>>()

        var n = 0
        val requiredCycles = 1000000000
        while (n < requiredCycles) {
            if (memo.contains(platform)) {
                val (p, prevIndex) = memo.getValue(platform)
                platform = p
                val cycleSize = n - prevIndex
                val cyclesToSkip = (requiredCycles - n) / cycleSize // required for integer division
                n += (cyclesToSkip * cycleSize) + 1
                continue
            }

            val originalPlatform = platform
            platform = platform.tiltNorth().tiltWest().tiltSouth().tiltEast()
            memo[originalPlatform] = Pair(platform, n)
            n++
        }

        return platform.mapIndexed { i, row -> row.count { it == 'O' } * (platform.size - i) }.sum()
    }

    val testInput = readInput("Day14_test")
    check(part1(testInput) == 136)
    check(part2(testInput) == 64)

    val input = readInput("Day14")
    part1(input).print("Part 1")
    part2(input).print("Part 2")
}