fun main() {
    fun part1(input: List<String>): Int {
        return input.sumOf { line ->
            var currentList = line.split(" ").map { it.toInt() }
            val lastValues = ArrayList<Int>()
            while (!currentList.all { it == 0 }) {
                lastValues.add(currentList.last())
                currentList = currentList.windowed(2).map { it[1] - it[0] }
            }
            lastValues.reversed().reduce { acc, i -> acc + i }
        }
    }

    fun part2(input: List<String>): Int {
        return input.sumOf { line ->
            var currentList = line.split(" ").map { it.toInt() }
            val firstValues = ArrayList<Int>()
            while (!currentList.all { it == 0 }) {
                firstValues.add(currentList.first())
                currentList = currentList.windowed(2).map { it[1] - it[0] }
            }
            firstValues.reversed().reduce { acc, i -> i - acc }
        }
    }

    val testInput = readInput("Day09_test")
    check(part1(testInput) == 114)
    check(part2(testInput) == 2)

    val input = readInput("Day09")
    part1(input).print("Part 1")
    part2(input).print("Part 2")
}