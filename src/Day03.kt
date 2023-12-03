fun main() {
    fun part1(input: List<String>): Int {
        val numberRanges = ArrayList<Triple<Int, Int, IntRange>>()
        val symbolLocations = ArrayList<Pair<Int, Int>>()

        for ((y, line) in input.withIndex()) {
            var currentNumber = ""
            var startIndex = 0

            for ((x, char) in line.withIndex()) {
                if (char.isDigit()) {
                    if (currentNumber.isEmpty()) {
                        startIndex = x
                    }
                    currentNumber += char

                    if (x == line.length - 1) {
                        numberRanges.add(Triple(currentNumber.toInt(), y, IntRange(startIndex, x)))
                    }
                } else {
                    if (currentNumber.isNotEmpty()) {
                        numberRanges.add(Triple(currentNumber.toInt(), y, IntRange(startIndex, x - 1)))
                        currentNumber = ""
                        startIndex = 0
                    }

                    if (char != '.') {
                        symbolLocations.add(Pair(y, x))
                    }
                }
            }
        }

        return symbolLocations.sumOf { symbol ->
            numberRanges.filter { numberRange ->
                numberRange.second in (symbol.first - 1..symbol.first + 1) &&
                (symbol.second - 1..symbol.second + 1).overlaps(numberRange.third)
            }.sumOf { it.first }
        }
    }

    fun part2(input: List<String>): Int {
        val numberRanges = ArrayList<Triple<Int, Int, IntRange>>()
        val asteriskLocations = ArrayList<Pair<Int, Int>>()

        for ((y, line) in input.withIndex()) {
            var currentNumber = ""
            var startIndex = 0

            for ((x, char) in line.withIndex()) {
                if (char.isDigit()) {
                    if (currentNumber.isEmpty()) {
                        startIndex = x
                    }
                    currentNumber += char

                    if (x == line.length - 1) {
                        numberRanges.add(Triple(currentNumber.toInt(), y, IntRange(startIndex, x)))
                    }
                } else {
                    if (currentNumber.isNotEmpty()) {
                        numberRanges.add(Triple(currentNumber.toInt(), y, IntRange(startIndex, x - 1)))
                        currentNumber = ""
                        startIndex = 0
                    }

                    if (char == '*') {
                        asteriskLocations.add(Pair(y, x))
                    }
                }
            }
        }

        return asteriskLocations.sumOf { symbol ->
            val touching = numberRanges.filter { numberRange ->
                numberRange.second in (symbol.first - 1..symbol.first + 1) &&
                (symbol.second - 1..symbol.second + 1).overlaps(numberRange.third)
            }

            if (touching.size == 2) touching[0].first * touching[1].first else 0
        }
    }

    val testInput = readInput("Day03_test")
    check(part1(testInput) == 4361)
    check(part2(testInput) == 467835)

    val input = readInput("Day03")
    part1(input).print("Part 1")
    part2(input).print("Part 2")
}