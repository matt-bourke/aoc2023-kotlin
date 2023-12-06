import kotlin.math.*

fun main() {
    fun part1(input: List<String>): Int {
        val times = input[0].split(": ")[1].trim().split(" +".toRegex()).map { it.toInt() }
        val records = input[1].split(": ")[1].trim().split(" +".toRegex()).map { it.toInt() }

        var errorMargin = 1
        for ((i, time) in times.withIndex()) {
            val correspondingRecord = records[i]

            // solve quadratic
            val b = -time.toFloat()
            val c = correspondingRecord.toFloat()
            val d = sqrt(b * b - 4 * c)
            val xLower = (-b - d) / 2
            val xUpper = (-b + d) / 2
            val waysToWin = ceil(xUpper) - floor(xLower) - 1
            errorMargin *= waysToWin.toInt()
        }

        return errorMargin
    }

    fun part2(input: List<String>): Int {
        val time = input[0].split(": ")[1].trim().split(" +".toRegex()).joinToString(separator = "").toLong()
        val record = input[1].split(": ")[1].trim().split(" +".toRegex()).joinToString(separator = "").toLong()

        // solve quadratic
        val b = -time.toDouble()
        val c = record.toDouble()
        val d = sqrt(b * b - 4 * c)
        val xLower = (-b - d) / 2
        val xUpper = (-b + d) / 2

        return (ceil(xUpper) - floor(xLower) - 1).toInt()
    }

    val testInput = readInput("Day06_test")
    check(part1(testInput) == 288)
    check(part2(testInput) == 71503)

    val input = readInput("Day06")
    part1(input).print("Part 1")
    part2(input).print("Part 2")
}