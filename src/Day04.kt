import kotlin.math.pow

fun main() {
    fun String.extractNumbers(): List<Int> {
        return this.trim().replace("  ", " ").split(" ").map { it.toInt() }
    }

    fun matches(card: String): Int {
        val (winningNumbers, myNumbers) = (card.split(": ")[1]).split(" | ").map { it.extractNumbers() }
        return myNumbers.count { it in winningNumbers }
    }

    fun part1(input: List<String>): Int {
        return input.sumOf { card ->
            (2.0).pow(matches(card) - 1).toInt()
        }
    }

    fun part2(input: List<String>): Int {
        val cards = ArrayList<Pair<Int, Int>>() // number of matches, number of cards
        for (card in input) {
            cards.add(Pair(matches(card), 1))
        }

        for ((index, card) in cards.withIndex()) {
            for (i in 1..card.first) {
                cards[index + i] = Pair(cards[index + i].first, cards[index + i].second + card.second)
            }
        }

        return cards.sumOf { it.second }
    }

    val testInput = readInput("Day04_test")
    check(part1(testInput) == 13)
    check(part2(testInput) == 30)

    val input = readInput("Day04")
    part1(input).print("Part 1")
    part2(input).print("Part 2")
}