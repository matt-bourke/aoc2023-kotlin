fun main() {
    fun part1(input: List<String>): Int {
        var sum = 0
        for (line in input) {
            val digits = line.filter { it.isDigit() }
            val firstDigit = digits[0].digitToInt()
            val secondDigit = digits[digits.length-1].digitToInt()
            sum += (firstDigit * 10) + secondDigit
        }
        return sum
    }

    fun part2(input: List<String>): Int {
        // weird replacement map to maintain strings with shared chars e.g 'oneight'
        val spelledDigits = mapOf("one" to "1one", "two" to "2two", "three" to "3three", "four" to "4four",
            "five" to "5five", "six" to "6six", "seven" to "7seven", "eight" to "8eight", "nine" to "9nine")
        var sum = 0
        for (line in input) {
            var mutableLine = line
            val firstSpelledDigit = line.findAnyOf(spelledDigits.keys)?.second
            firstSpelledDigit?.let { key -> mutableLine = mutableLine.replaceFirst(key, spelledDigits[key]!!) }

            val lastSpelledDigit = line.findLastAnyOf(spelledDigits.keys)?.second
            lastSpelledDigit?.let { key -> mutableLine = mutableLine.replace(key, spelledDigits[key]!!) }

            val digits = mutableLine.filter { it.isDigit() }
            val firstDigit = digits[0].digitToInt()
            val secondDigit = digits[digits.length-1].digitToInt()
            sum += (firstDigit * 10) + secondDigit
        }
        return sum
    }

    // test examples
    var testInput = readInput("Day01_Part1_test")
    check(part1(testInput) == 142)

    testInput = readInput("Day01_Part2_test")
    check(part2(testInput) == 281)

    val input = readInput("Day01")
    part1(input).print("Part 1")
    part2(input).print("Part 2")
}