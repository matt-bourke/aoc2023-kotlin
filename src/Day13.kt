data class Spot(val x: Int, val y: Int, val isAsh: Boolean)
typealias GroundRow = ArrayList<Spot>

fun main() {
    fun part1(input: String): Long {
        val grounds = input.split("\r\n\r\n")

        var sum = 0L

        for (g in grounds) {
            val ground = ArrayList<GroundRow>()
            for ((y, row) in g.split("\r\n").withIndex()) {
                val groundRow = GroundRow()
                for ((x, s) in row.withIndex()) {
                    val spot = Spot(x, y, s == '#')
                    groundRow.add(spot)
                }
                ground.add(groundRow)
            }

            // find vertical reflection
            var hasVerticalReflection: Boolean
            var verticalReflectionIndex = 0
            for (i in 1..<ground.first().size) {
                hasVerticalReflection = true
                rows@ for (groundRow in ground) {
                    val left = groundRow.subList(0, i).reversed()
                    val right = groundRow.subList(i, groundRow.size)
                    for (sides in left.zip(right)) {
                        if (sides.first.isAsh != sides.second.isAsh) {
                            // can't be reflection
                            hasVerticalReflection = false
                            break@rows
                        }
                    }
                }

                if (hasVerticalReflection) {
                    // all rows checks are reflections
                    verticalReflectionIndex = i
                    break
                }
            }

            sum += verticalReflectionIndex


            // find horizontal reflection
            var hasHorizontalReflection: Boolean
            var horizontalReflectionIndex = 0
            for (i in 1..<ground.size) {
                hasHorizontalReflection = true
                val rowsAbove = ground.subList(0, i).reversed()
                val rowsBelow = ground.subList(i, ground.size)

                for (sides in rowsAbove.zip(rowsBelow)) {
                    if (sides.first.map { it.isAsh } != sides.second.map { it.isAsh }) {
                        hasHorizontalReflection = false
                        break
                    }
                }

                if (hasHorizontalReflection) {
                    horizontalReflectionIndex = i
                    break
                }
            }

            sum += (horizontalReflectionIndex * 100)
        }

        return sum
    }

    fun part2(input: String): Long {
        val grounds = input.split("\r\n\r\n")

        var sum = 0L

        for (g in grounds) {
            val ground = ArrayList<GroundRow>()
            for ((y, row) in g.split("\r\n").withIndex()) {
                val groundRow = GroundRow()
                for ((x, s) in row.withIndex()) {
                    val spot = Spot(x, y, s == '#')
                    groundRow.add(spot)
                }
                ground.add(groundRow)
            }

            // find vertical reflection
            var hasFixedSmudge: Boolean
            var hasVerticalReflectionWithSmudge: Boolean
            var verticalReflectionWithSmudgeIndex = 0
            for (i in 1..<ground.first().size) {
                hasFixedSmudge = false
                hasVerticalReflectionWithSmudge = true
                rows@ for (groundRow in ground) {
                    val left = groundRow.subList(0, i).reversed()
                    val right = groundRow.subList(i, groundRow.size)
                    for (sides in left.zip(right)) {
                        if (sides.first.isAsh != sides.second.isAsh) {
                            if (!hasFixedSmudge) {
                                hasFixedSmudge = true
                            } else {
                                // can't be reflection
                                hasVerticalReflectionWithSmudge = false
                                break@rows
                            }
                        }
                    }
                }

                if (hasVerticalReflectionWithSmudge && hasFixedSmudge) {
                    // all rows checks are reflections
                    verticalReflectionWithSmudgeIndex = i
                    break
                }
            }

            sum += verticalReflectionWithSmudgeIndex


            // find horizontal reflection
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

            sum += (horizontalReflectionWithSmudgeIndex * 100)
        }

        return sum
    }

    val testInput = readInputRaw("Day13_test")
    check(part1(testInput) == 405L)
    check(part2(testInput) == 400L)

    val input = readInputRaw("Day13")
    part1(input).print("Part 1")
    part2(input).print("Part 2")
}