data class Spot(val x: Int, val y: Int, val isAsh: Boolean)
//typealias Spot = Char
typealias GroundRow = ArrayList<Spot>

fun getReflectionIndex(ground: ArrayList<GroundRow>): Int {
    var hasHorizontalReflection: Boolean
    var horizontalReflectionIndex = 0
    for (i in 1..<ground.size) {
        hasHorizontalReflection = true
        val rowsAbove = ground.subList(0, i).reversed()
        val rowsBelow = ground.subList(i, ground.size)

        for (sides in rowsAbove.zip(rowsBelow)) {
            if (sides.first.map { s -> s.isAsh } != sides.second.map { s -> s.isAsh }) {
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

fun getReflectionIndexWithSmudge(ground: ArrayList<GroundRow>): Int {
    // find horizontal reflection
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
                if (spots.first.isAsh != spots.second.isAsh) {
                    if (!hasFixedSmudge) {
                        hasFixedSmudge = true
                    } else {
                        // can't be reflection
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
            for ((y, row) in it.split("\r\n").withIndex()) {
                val groundRow = GroundRow()
                for ((x, s) in row.withIndex()) {
                    groundRow.add(Spot(x, y, s == '#'))
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
            for ((y, row) in it.split("\r\n").withIndex()) {
                val groundRow = GroundRow()
                for ((x, s) in row.withIndex()) {
                    val spot = Spot(x, y, s == '#')
                    groundRow.add(spot)
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