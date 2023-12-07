enum class HandType {
    HighCard,
    OnePair,
    TwoPair,
    ThreeOfAKind,
    FullHouse,
    FourOfAKind,
    FiveOfAKind
}

fun getCardValue(c: Char): Int {
    return when(c) {
        'T' -> 0
        'J' -> 1
        'Q' -> 2
        'K' -> 3
        'A' -> 4
        else -> 0
    }
}

data class Hand(val cards: String, val handType: HandType, val bid: Int) : Comparable<Hand> {
    override fun compareTo(other: Hand): Int {
        if (this.handType.ordinal != other.handType.ordinal) {
            return this.handType.ordinal - other.handType.ordinal
        }

        for (i in 0..4) {
            val card1 = this.cards[i]
            val card2 = other.cards[i]
            if (card1 != card2) {
                 if (card1.isDigit() && card2.isDigit()) {
                     return card1.digitToInt() - card2.digitToInt()
                 }

                if (card1.isDigit()) {
                    return -1
                }

                if (card2.isDigit()) {
                    return 1
                }

                return getCardValue(card1) - getCardValue(card2)
            }
        }

        return 0
    }
}

fun getCardValueJokerEdition(c: Char): Int {
    return when(c) {
        'J' -> 0
        'T' -> 1
        'Q' -> 2
        'K' -> 3
        'A' -> 4
        else -> 0
    }
}

data class HandJokerEdition(val cards: String, val handType: HandType, val bid: Int) : Comparable<HandJokerEdition> {
    override fun compareTo(other: HandJokerEdition): Int {
        if (this.handType.ordinal != other.handType.ordinal) {
            return this.handType.ordinal - other.handType.ordinal
        }

        for (i in 0..4) {
            val card1 = this.cards[i]
            val card2 = other.cards[i]
            if (card1 != card2) {
                if (card1.isDigit() && card2.isDigit()) {
                    return card1.digitToInt() - card2.digitToInt()
                }

                if (card1.isDigit()) {
                    if (card2 == 'J') {
                        return 1
                    }
                    return -1
                }

                if (card2.isDigit()) {
                    if (card1 == 'J') {
                        return -1
                    }
                    return 1
                }

                return getCardValueJokerEdition(card1) - getCardValueJokerEdition(card2)
            }
        }

        return 0
    }
}

fun getHandType(cardCounts: HashMap<Char, Int>): HandType {
    if (cardCounts.values.any { v -> v == 5 }) {
        return HandType.FiveOfAKind
    } else if (cardCounts.values.any { v -> v == 4}) {
        return HandType.FourOfAKind
    } else if (cardCounts.values.any { v -> v == 3 } &&
               cardCounts.values.any { v -> v == 2 }) {
        return HandType.FullHouse
    } else if (cardCounts.values.any { v -> v == 3 }) {
        return HandType.ThreeOfAKind
    } else if (cardCounts.values.count { v -> v == 2 } == 2) {
        return HandType.TwoPair
    } else if (cardCounts.values.count { v -> v == 2 } == 1) {
        return HandType.OnePair
    }

    return HandType.HighCard
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
        var hands = input.map {
            val (cards, bid) = it.split(" ")

            val cardCounts = HashMap<Char, Int>()
            for (c in cards) {
                cardCounts[c] = cardCounts.getOrPut(c) { 0 } + 1
            }
            val handType = getHandType(cardCounts)

            Hand(cards, handType, bid.toInt())
        }

        hands = hands.sorted()
        var sum = 0
        hands.forEachIndexed { i, hand ->
            sum += hand.bid * (i + 1)
        }

        return sum
    }

    fun part2(input: List<String>): Int {
        var hands = input.map {
            val (cards, bid) = it.split(" ")

            val cardCounts = HashMap<Char, Int>()
            for (c in cards) {
                cardCounts[c] = cardCounts.getOrPut(c) { 0 } + 1
            }
            val handType = getHandTypeWithJokers(cardCounts)

            HandJokerEdition(cards, handType, bid.toInt())
        }

        hands = hands.sorted()
        var sum = 0
        hands.forEachIndexed { i, hand ->
            sum += hand.bid * (i + 1)
        }

        return sum
    }

    val testInput = readInput("Day07_test")
    check(part1(testInput) == 6440)
    check(part2(testInput) == 5905)

    val input = readInput("Day07")
    part1(input).print("Part 1")
    part2(input).print("Part 2")
}