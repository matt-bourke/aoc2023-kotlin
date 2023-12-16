fun hash(input: String): Int {
    return input.fold(0) { acc, i ->
        var x = acc + i.code
        x *= 17
        x %= 256
        x
    }
}

fun main() {
    fun part1(input: List<String>): Int {
        return input.first().split(",").sumOf { hash(it) }
    }

    fun part2(input: List<String>): Int {
        val hashMap = ArrayList<ArrayList<Pair<String, Int>>>()
        for (i in 0..<256) {
            hashMap.add(ArrayList())
        }

        for (element in input.first().split(",")) {
            if (element.endsWith("-")) {
                val label = element.substring(0, element.length - 1)
                val boxNumber = hash(label)
                var indexToRemove = -1
                for ((index, lens) in hashMap[boxNumber].withIndex()) {
                    if (lens.first == label) {
                        indexToRemove = index
                        break
                    }
                }
                if (indexToRemove > -1) {
                    hashMap[boxNumber].removeAt(indexToRemove)
                }
            } else {
                val (label, value) = element.split("=")
                val boxNumber = hash(label)
                var updated = false
                var lens: Pair<String, Int>
                for (l in hashMap[boxNumber].indices) {
                    lens = hashMap[boxNumber][l]
                    if (lens.first == label) {
                        lens = Pair(lens.first, value.toInt())
                        hashMap[boxNumber][l] = lens
                        updated = true
                    }
                }

                if (!updated) {
                    hashMap[boxNumber].add(Pair(label, value.toInt()))
                }
            }
        }

        return hashMap.sumOfIndexed { i, box ->
            box.sumOfIndexed { j, lens ->
                (i + 1) * (j + 1) * lens.second
            }
        }
    }

    val testInput = readInput("Day15_test")
    check(part1(testInput) == 1320)
    check(part2(testInput) == 145)

    val input = readInput("Day15")
    part1(input).print("Part 1")
    part2(input).print("Part 2")
}