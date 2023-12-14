typealias GroundRow = ArrayList<Char>

fun getReflectionIndex(ground: List<GroundRow>): Int {
    var hasHorizontalReflection: Boolean
    var horizontalReflectionIndex = 0
    for (i in 1..<ground.size) {
        hasHorizontalReflection = true
        val rowsAbove = ground.subList(0, i).reversed()
        val rowsBelow = ground.subList(i, ground.size)

        for (sides in rowsAbove.zip(rowsBelow)) {
            if (sides.first != sides.second) {
                hasHorizontalReflection = false
                break
            }
        }

        if (hasHorizontalReflection) {
            horizontalReflectionIndex = i
            break
        }
    }

    return horizontalReflectionIndex
}

fun getReflectionIndexWithSmudge(ground: List<GroundRow>): Int {
    var hasFixedSmudge: Boolean
    var hasHorizontalReflectionWithSmudge: Boolean
    var horizontalReflectionWithSmudgeIndex = 0
    for (i in 1..<ground.size) {
        hasFixedSmudge = false
        hasHorizontalReflectionWithSmudge = true
        val rowsAbove = ground.subList(0, i).reversed()
        val rowsBelow = ground.subList(i, ground.size)

        rows@ for (sides in rowsAbove.zip(rowsBelow)) {
            for (spots in sides.first.zip(sides.second)) {
                if (spots.first != spots.second) {
                    if (!hasFixedSmudge) {
                        hasFixedSmudge = true
                    } else {
                        hasHorizontalReflectionWithSmudge = false
                        break@rows
                    }
                }
            }
        }

        if (hasHorizontalReflectionWithSmudge && hasFixedSmudge) {
            horizontalReflectionWithSmudgeIndex = i
            break
        }
    }

    return horizontalReflectionWithSmudgeIndex
}

fun main() {
    fun part1(input: String): Int {
        val grounds = input.split("\r\n\r\n")
        return grounds.sumOf {
            val ground = ArrayList<GroundRow>()
            for (row in it.split("\r\n")) {
                val groundRow = GroundRow()
                for (s in row) {
                    groundRow.add(s)
                }
                ground.add(groundRow)
            }

            val verticalReflectionIndex = getReflectionIndex(ground.transpose())
            val horizontalReflectionIndex = getReflectionIndex(ground)
            (horizontalReflectionIndex * 100) + verticalReflectionIndex
        }
    }

    fun part2(input: String): Int {
        val grounds = input.split("\r\n\r\n")
        return grounds.sumOf {
            val ground = ArrayList<GroundRow>()
            for (row in it.split("\r\n")) {
                val groundRow = GroundRow()
                for (s in row) {
                    groundRow.add(s)
                }
                ground.add(groundRow)
            }

            val verticalReflectionWithSmudgeIndex = getReflectionIndexWithSmudge(ground.transpose())
            val horizontalReflectionWithSmudgeIndex = getReflectionIndexWithSmudge(ground)
            (horizontalReflectionWithSmudgeIndex * 100) + verticalReflectionWithSmudgeIndex
        }
    }

    val testInput = readInputRaw("Day13_test")
    check(part1(testInput) == 405)
    check(part2(testInput) == 400)

    val input = readInputRaw("Day13")
    part1(input).print("Part 1")
    part2(input).print("Part 2")
}