enum class HandType {
    HighCard,
    OnePair,
    TwoPair,
    ThreeOfAKind,
    FullHouse,
    FourOfAKind,
    FiveOfAKind
}

data class Hand(val cards: String, val handType: HandType, val bid: Int, val jokerEdition: Boolean) : Comparable<Hand> {
    override fun compareTo(other: Hand): Int {
        if (this.handType.ordinal != other.handType.ordinal) {
            return this.handType.ordinal - other.handType.ordinal
        }
        for ((card, otherCard) in this.cards.zip(other.cards)) {
            if (card != otherCard) {
                val jValue = if (this.jokerEdition) 0 else 11
                return getCardValue(card, jValue) - getCardValue(otherCard, jValue)
            }
        }
        return 0
    }
}

fun getCardValue(c: Char, jValue: Int): Int {
    return when (c) {
        'T' -> 10
        'J' -> jValue
        'Q' -> 12
        'K' -> 13
        'A' -> 14
        in '2'..'9' -> c.digitToInt()
        else -> 0
    }
}

fun getHandType(cardCounts: HashMap<Char, Int>): HandType {
    return when {
        cardCounts.values.any { v -> v == 5 } -> HandType.FiveOfAKind
        cardCounts.values.any { v -> v == 4 } -> HandType.FourOfAKind
        cardCounts.values.any { v -> v == 3 } &&
        cardCounts.values.any { v -> v == 2 } -> HandType.FullHouse
        cardCounts.values.any { v -> v == 3 } -> HandType.ThreeOfAKind
        cardCounts.values.count { v -> v == 2 } == 2 -> HandType.TwoPair
        cardCounts.values.count { v -> v == 2 } == 1 -> HandType.OnePair
        else -> HandType.HighCard
    }
}

fun getHandTypeWithJokers(cardCounts: HashMap<Char, Int>): HandType {
    val numJokers = cardCounts['J'] ?: 0
    cardCounts['J'] = 0
    val (maxKey, maxValue) = cardCounts.maxBy { it.value }
    cardCounts[maxKey] = maxValue + numJokers
    return getHandType(cardCounts)
}

fun main() {
    fun part1(input: List<String>): Int {
        val hands = input.map {
            val (cards, bid) = it.split(" ")
            val cardCounts = HashMap<Char, Int>()
            for (c in cards) {
                cardCounts[c] = cardCounts.getOrPut(c) { 0 } + 1
            }
            val handType = getHandType(cardCounts)
            Hand(cards, handType, bid.toInt(), false)
        }

        return hands.sorted().sumOfIndexed { i, hand ->
            hand.bid * (i + 1)
        }
    }

    fun part2(input: List<String>): Int {
        val hands = input.map {
            val (cards, bid) = it.split(" ")
            val cardCounts = HashMap<Char, Int>()
            for (c in cards) {
                cardCounts[c] = cardCounts.getOrPut(c) { 0 } + 1
            }
            val handType = getHandTypeWithJokers(cardCounts)
            Hand(cards, handType, bid.toInt(), true)
        }

        return hands.sorted().sumOfIndexed { i, hand ->
            hand.bid * (i + 1)
        }
    }

    val testInput = readInput("Day07_test")
    check(part1(testInput) == 6440)
    check(part2(testInput) == 5905)

    val input = readInput("Day07")
    part1(input).print("Part 1")
    part2(input).print("Part 2")
}