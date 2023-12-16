fun hash(input: String): Int {
    return input.fold(0) { acc, i ->((acc + i.code) * 17) % 256 }
}

fun main() {
    fun part1(input: List<String>): Int {
        return input.first().split(",").sumOf { hash(it) }
    }

    fun part2(input: List<String>): Int {
        data class Lens(val label: String, var focalLength: Int)
        val hashMap = ArrayList<ArrayList<Lens>>()
        for (i in 0..<256) {
            hashMap.add(ArrayList())
        }

        for (element in input.first().split(",")) {
            if (element.endsWith("-")) {
                val label = element.substring(0, element.length - 1)
                val boxNumber = hash(label)
                hashMap[boxNumber].removeIf { it.label == label }
            } else {
                val (label, value) = element.split("=")
                val boxNumber = hash(label)
                val index = hashMap[boxNumber].indexOfFirst { it.label == label }
                if (index > -1) {
                    hashMap[boxNumber][index].focalLength = value.toInt()
                } else {
                    hashMap[boxNumber].add(Lens(label, value.toInt()))
                }
            }
        }

        return hashMap.sumOfIndexed { i, box ->
            box.sumOfIndexed { j, lens ->
                (i + 1) * (j + 1) * lens.focalLength
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