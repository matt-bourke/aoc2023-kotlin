typealias SpaceRow = ArrayList<Space>
data class Space(val x: Int, val y: Int, val isGalaxy: Boolean)
fun Space.rangeFrom(other: Space): Pair<IntRange, IntRange> {
    val xRange = findMinMax(this.x, other.x)
    val yRange = findMinMax(this.y, other.y)
    return Pair(IntRange(xRange.first, xRange.second), IntRange(yRange.first, yRange.second))
}

fun findGalaxyDistanceSum(input: List<String>, expansionConstant: Int): Long {
    val spaceGrid = ArrayList<SpaceRow>()
    input.forEachIndexed { rowIndex, line ->
        val spaceRow = SpaceRow()
        line.forEachIndexed { colIndex, space ->
            spaceRow.add(Space(colIndex, rowIndex, space == '#'))
        }
        spaceGrid.add(spaceRow)
    }

    val rowsToExpand = spaceGrid.indices.filter { i -> spaceGrid[i].none { s -> s.isGalaxy } }
    val colsToExpand = spaceGrid.first().indices.filter { i -> spaceGrid.map { it[i] }.none { s -> s.isGalaxy } }
    val galaxies = spaceGrid.flatten().filter { it.isGalaxy }

    return galaxies.dropLast(1).withIndex().sumOf { (i, galaxy) ->
        galaxies.drop(i).sumOf { galaxy2 ->
            val distance = manhattan(galaxy.x, galaxy.y, galaxy2.x, galaxy2.y)
            val (rangeX, rangeY) = galaxy.rangeFrom(galaxy2)
            val numberOfExpansionsCrossedX = (colsToExpand.count { it in rangeX } * (expansionConstant - 1))
            val numberOfExpansionsCrossedY = (rowsToExpand.count { it in rangeY } * (expansionConstant - 1))
            (distance + numberOfExpansionsCrossedX + numberOfExpansionsCrossedY).toLong()
        }
    }
}

fun main() {
    fun part1(input: List<String>): Long {
        return findGalaxyDistanceSum(input, 2)
    }

    fun part2(input: List<String>, expansionConstant: Int): Long {
        return findGalaxyDistanceSum(input, expansionConstant)
    }

    val testInput = readInput("Day11_test")
    check(part1(testInput) == 374L)
    check(part2(testInput, 100) == 8410L)

    val input = readInput("Day11")
    part1(input).print("Part 1")
    part2(input, 1000000).print("Part 2")
}