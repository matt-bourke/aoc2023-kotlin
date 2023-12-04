fun main() {
    fun part1(input: List<String>): Int {
        var sum = 0
        for (line in input) {
            val numbers = line.split(": ")[1]
            val (winningNumbers, myNumbers) = numbers.split(" | ")
            val winningNumbersList = winningNumbers.trim().replace("  ", " ").split(" ").map { it.toInt() }
            val myNumbersList = myNumbers.trim().replace("  ", " ").split(" ").map { it.toInt() }

            var score = 0
            for (number in myNumbersList) {
                if (number in winningNumbersList) {
                    if (score == 0) {
                        score = 1
                    } else {
                        score *= 2
                    }
                }
            }
            sum += score
        }

        return sum
    }

    fun part2(input: List<String>): Int {
        val cards = ArrayList<Pair<Int, Int>>() // number of matches, number of cards
        for (line in input) {
            val numbers = line.split(": ")[1]
            val (winningNumbers, myNumbers) = numbers.split(" | ")
            val winningNumbersList = winningNumbers.trim().replace("  ", " ").split(" ").map { it.toInt() }
            val myNumbersList = myNumbers.trim().replace("  ", " ").split(" ").map { it.toInt() }

            var matches = 0
            for (number in myNumbersList) {
                if (number in winningNumbersList) {
                    matches++
                }
            }
            cards.add(Pair(matches, 1))
        }

        for ((index, card) in cards.withIndex()) {
            val matches = card.first
            for (i in 1..matches) {
                cards[index+i] = Pair(cards[index+i].first, cards[index+i].second + card.second)
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