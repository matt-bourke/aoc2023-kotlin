fun main() {
    fun isGamePossible(gameData: String): Boolean {
        val rMax = 12
        val gMAx = 13
        val bMax = 14

        for (round in gameData.split("; ")) {
            for (cubeColor in round.split(", ")) {
                val (amount, colour) = cubeColor.split(" ")
                when (colour) {
                    "red" -> if (amount.toInt() > rMax) return false
                    "green" -> if (amount.toInt() > gMAx) return false
                    "blue" -> if (amount.toInt() > bMax) return false
                }
            }
        }

        return true
    }

    fun part1(input: List<String>): Int {
        return input.withIndex().sumOf { (gameID, gameData) ->
            if (isGamePossible((gameData.split(": ").last()))) (gameID + 1) else 0
        }
    }

    fun calculatePowerSum(gameData: String): Int {
        var requiredReds = 0
        var requiredGreens = 0
        var requiredBlues = 0

        for (round in gameData.split("; ")) {
            for (cubeColour in round.split(", ")) {
                val (amount, colour) = cubeColour.split(" ")
                val num = amount.toInt()
                when (colour) {
                    "red" -> if (num > requiredReds) requiredReds = num
                    "green" -> if (num > requiredGreens) requiredGreens = num
                    "blue" -> if (num > requiredBlues) requiredBlues = num
                }
            }
        }

        return requiredReds * requiredGreens * requiredBlues
    }

    fun part2(input: List<String>): Int {
        return input.sumOf { game ->
            calculatePowerSum(game.split(": ").last())
        }
    }

    var testInput = readInput("Day02_test")
    check(part1(testInput) == 8)

    testInput = readInput("Day02_test")
    check(part2(testInput) == 2286)

    val input = readInput("Day02")
    part1(input).print("Part 1")
    part2(input).print("Part 2")
}