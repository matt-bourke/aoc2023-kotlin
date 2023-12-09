fun main() {
    fun part1(input: List<String>): Int {
        var sum = 0
        for (line in input) {
            val values = line.split(" ").map { it.toInt() }
            var currentList = values
            val lastValues = ArrayList<Int>()
            while (!currentList.all { it == 0 }) {
                val nextValues = ArrayList<Int>()
                for (i in 1..<currentList.count()) {
                    nextValues.add(currentList[i] - currentList[i-1])
                }
                lastValues.add(currentList.last())
                currentList = nextValues
            }

            // backtrack
            var prediction = 0
            for (value in lastValues.reversed()) {
                prediction += value
            }
            sum += prediction
        }

        return sum
    }

    fun part2(input: List<String>): Int {
        var sum = 0
        for (line in input) {
            val values = line.split(" ").map { it.toInt() }
            var currentList = values
            val firstValues = ArrayList<Int>()
            while (!currentList.all { it == 0 }) {
                val nextValues = ArrayList<Int>()
                for (i in 1..<currentList.count()) {
                    nextValues.add(currentList[i] - currentList[i-1])
                }
                firstValues.add(currentList.first())
                currentList = nextValues
            }

            // backtrack
            var prediction = 0
            for (value in firstValues.reversed()) {
                prediction = value - prediction
            }
            sum += prediction
        }

        return sum
    }

    val testInput = readInput("Day09_test")
    check(part1(testInput) == 114)
    check(part2(testInput) == 2)

    val input = readInput("Day09")
    part1(input).print("Part 1")
    part2(input).print("Part 2")
}