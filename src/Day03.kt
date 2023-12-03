fun main() {
    fun part1(input: List<String>): Int {
        var sum = 0
        for ((y, line) in input.withIndex()) {
            var x = 0
            loop@ while (x < line.length) {
                var char = line[x]
                if (char.isDigit()) {
                    val pos = x
                    var numberString = char.toString()
                    x++
                    if (x >= line.length - 1) {
                        break
                    }
                    char = line[x]
                    while (char.isDigit()) {
                        char = line[x]
                        numberString += char
                        x++

                        if (x > line.length - 1) {
                            break
                        }
                        char = line[x]
                    }

                    // get number bounds
                    val xMin = (pos - 1).coerceAtLeast(0)
                    val xMax = x.coerceAtMost(line.length - 1)
                    val yMin = (y - 1).coerceAtLeast(0)
                    val yMax = (y + 1).coerceAtMost(input.size - 1)

                    for (symbolSearchY in yMin..yMax) {
                        for (symbolSearchX in xMin..xMax) {
                            if (symbolSearchY == y && symbolSearchX >= pos && symbolSearchX < x) {
                                continue
                            }

                            val inspectionChar = input[symbolSearchY][symbolSearchX]
                            if (inspectionChar in charArrayOf('@', '#', '$', '%', '&', '*', '-', '+', '=', '/')) {
                                sum += numberString.toInt()
                                continue@loop
                            }
                        }
                    }
                }
                x++
            }
        }
        return sum
    }

    fun part2(input: List<String>): Int {
        var gearSum = 0

        for ((y, line) in input.withIndex()) {
            var x = 0
            while (x < line.length) {
                val char = line[x]
                if (char == '*') {
                    val xMin = (x - 1).coerceAtLeast(0)
                    val xMax = (x + 1).coerceAtMost(line.length - 1)
                    val yMin = (y - 1).coerceAtLeast(0)
                    val yMax = (y + 1).coerceAtMost(input.size - 1)

                    val attachedNumbers = ArrayList<Int>()
                    for (numberSearchY in yMin..yMax) {
                        var numberSearchX = xMin
                        while (numberSearchX <= xMax) {
                            if (numberSearchX == x && numberSearchY == y) {
                                numberSearchX++
                                continue
                            }

                            if (input[numberSearchY][numberSearchX].isDigit()) {
                                var startIndex = numberSearchX

                                while (startIndex > 0 && input[numberSearchY][startIndex-1].isDigit()) {
                                    startIndex--
                                }

                                var endIndex = startIndex
                                while (endIndex < line.length-1 && input[numberSearchY][endIndex+1].isDigit()) {
                                    endIndex++
                                }

                                attachedNumbers.add(input[numberSearchY].substring(startIndex, endIndex+1).toInt())
                                numberSearchX = endIndex + 1
                            }

                            numberSearchX++
                        }
                    }

                    if (attachedNumbers.size == 2) {
                        gearSum += (attachedNumbers[0] * attachedNumbers[1])
                    }
                }
                x++
            }
        }

        return gearSum
    }

    val testInput = readInput("Day03_test")
    check(part1(testInput) == 4361)
    check(part2(testInput) == 467835)

    val input = readInput("Day03")
    part1(input).print("Part 1")
    part2(input).print("Part 2")
}