import java.awt.Point
import kotlin.math.pow

fun main() {
    fun Point.neighbours(map: List<List<Any>>): ArrayList<Point> {
        val neighbours = ArrayList<Point>()
        if ((x - 1) in map.first().indices) {
            neighbours.add(Point(x - 1, y))
        }
        if ((x + 1) in map.first().indices) {
            neighbours.add(Point(x + 1, y))
        }
        if ((y - 1) in map.indices) {
            neighbours.add(Point(x, y - 1))
        }
        if ((y + 1) in map.indices) {
            neighbours.add(Point(x, y + 1))
        }
        return neighbours
    }

    fun List<MutableList<Char>>.countSpots() = this.flatten().count { it == 'O' }

    fun part1(input: List<String>, steps: Int): Int {
        val start = Point(0, 0)
        val grid = input.mapIndexed { y, row ->
            row.mapIndexed { x, c ->
                if (c == 'S') {
                    start.x = x
                    start.y = y
                }
                c
            }
        }

        val open = HashSet<Point>()
        val next = HashSet<Point>()
        open.add(start)

        for (i in 1..steps) {
            for (point in open) {
                val neighbours = point.neighbours(grid)
                for (n in neighbours) {
                    if (grid[n.y][n.x] != '#') {
                        next.add(n)
                    }
                }
            }

            open.clear()
            for (entry in next) {
                open.add(Point(entry.x, entry.y))
            }
            next.clear()
        }

        return open.size
    }

    fun part2(input: List<String>, steps: Int): Long {
        /*
            Alright here's the plan, buckos.
            26501365 steps fits exactly into 404601 x 404601 grid of maps 131 x 131 in size:

            26501365 / 131 = 202300 remainder 65
            Since we can go any direction, max size = 2 * 202300 + 1 (starting point)

            This forms a diamond shape since all the plots in start point row and column don't have rocks.
            We can walk exactly 26501365 in any cardinal direction, and since we can't move diagonally,
            we form a diamond.

            Diagramming this out, we can see most grid tiles are fully filled, but alternate the spots available.
            We also see that the diagonals are filled between the midpoints of 2 adjacent sides.
            This means there are 13 unique files that can be formed.
            Extending the grid to 5x5 is computationally sane, and give us a sample of each unique tile.

            We can also calculate the number of each unique tile.
            The number of completely filled in tiles = n - 2 + 2 * ((n - 3) / 2) ^ 2
            Out of this, the number of 'base' filled tiles = ((n - 3) / 2) * ((n - 1)) / 2) - ((n - 3) / 2)
            Then the number of 'alt' filled tiles = (number of completely filled in tiles - number of 'base' filled tiles)
            The number of 'chunky' diagonals = (n - 3)/2 for each diagonal
            The 'smol' diagonals = (n - 1)/2 for each diagonal
            4 corner pieces

            Using the 5x5 version to find how many spots are available for each type of tile.
            Finally, we just have to multiply the number of spots in each tile type by the frequency of that tile.
            Summing all that up gives us the answer.
         */

        val start = Point(0, 0)
        val grid = input.mapIndexed { y, row ->
            row.mapIndexed { x, c ->
                if (c == 'S') {
                    start.x = x
                    start.y = y
                }
                c
            }
        }

        val grid5x5 = ArrayList<ArrayList<Char>>()
        for (y in 1..5) {
            for (row in grid) {
                val row5x5 = ArrayList<Char>()
                for (x in 1..5) {
                    for (c in row) {
                        if (c == 'S' && (x != 3 || y != 3)) {
                            row5x5.add('.')
                        } else {
                            row5x5.add(c)
                        }
                    }
                }
                grid5x5.add(row5x5)
            }
        }

        val squaresDistance = (steps / input.size)
        val n = 2 * squaresDistance + 1
        val fullSquares: Long = n - 2 + 2 * ((n - 3).toDouble() / 2.0).pow(2.0).toLong()
        val numberOfBaseFullSquares = ((n - 3).toLong() / 2) * ((n - 1).toLong() / 2) - ((n - 3).toLong() / 2)
        val numberOfAltFullSquares = fullSquares - numberOfBaseFullSquares

        val map5x5Width = grid5x5.first().size
        val open = HashSet<Point>()
        val next = HashSet<Point>()
        val midpoint = (map5x5Width - 1) / 2
        val start5x5 = Point(midpoint, midpoint)
        open.add(start5x5)

        for (i in 1..(map5x5Width - 1) / 2) {
            for (point in open) {
                val neighbours = point.neighbours(grid5x5)
                for (ne in neighbours) {
                    if (grid5x5[ne.y][ne.x] != '#') {
                        next.add(ne)
                    }
                }
            }

            open.clear()
            for (entry in next) {
                open.add(Point(entry.x, entry.y))
            }
            next.clear()
        }

        val replacedGrid5x5 = ArrayList<ArrayList<Char>>()
        for (row in grid5x5.withIndex()) {
            val replacedRow = ArrayList<Char>()
            for (c in row.value.withIndex()) {
                if (open.contains(Point(c.index, row.index))) {
                    replacedRow.add('O')
                } else {
                    replacedRow.add(c.value)
                }
            }
            replacedGrid5x5.add(replacedRow)
        }

        val firstRow = replacedGrid5x5.subList(0, 131)
        val secondRow = replacedGrid5x5.subList(131, 262)
        val thirdRow = replacedGrid5x5.subList(262, 393)
        val fourthRow = replacedGrid5x5.subList(393, 524)
        val fifthRow = replacedGrid5x5.subList(524, 655)

        val grid01 = firstRow.map { it.subList(131, 262) } // smol SE partial corner
        val grid02 = firstRow.map { it.subList(262, 393) } // top corner
        val grid03 = firstRow.map { it.subList(393, 524) } // smol SW partial corner
        val grid11 = secondRow.map { it.subList(131, 262) } // chunky NW partial corner
        val grid12 = secondRow.map { it.subList(262, 393) } // 'alt' filled
        val grid13 = secondRow.map { it.subList(393, 524) } // chunky NE partial corner
        val grid20 = thirdRow.map { it.subList(0, 131) } // left corner
        val grid22 = thirdRow.map { it.subList(262, 393) } // center ('base' filled)
        val grid24 = thirdRow.map { it.subList(524, 655) } // right corner
        val grid30 = fourthRow.map { it.subList(0, 131) } // smol NE partial corner
        val grid31 = fourthRow.map { it.subList(131, 262) } // chunky SW partial corner
        val grid33 = fourthRow.map { it.subList(393, 524) } // chunky SE partial corner
        val grid34 = fourthRow.map { it.subList(524, 655) } // smol NW partial corner
        val grid42 = fifthRow.map { it.subList(262, 393) } // bottom corner

        val oddSpots = grid22.countSpots()
        val evenSpots = grid12.countSpots()

        val numChunky = (n - 3) / 2
        val chunkySE = grid33.countSpots() * numChunky
        val chunkySW = grid31.countSpots() * numChunky
        val chunkyNW = grid11.countSpots() * numChunky
        val chunkyNE = grid13.countSpots() * numChunky

        val numSmol = (n - 1) / 2
        val smolSE = grid01.countSpots() * numSmol
        val smolSW = grid03.countSpots() * numSmol
        val smolNW = grid34.countSpots() * numSmol
        val smolNE = grid30.countSpots() * numSmol

        val rightCorner = grid24.countSpots()
        val bottomCorner = grid42.countSpots()
        val topCorner = grid02.countSpots()
        val leftCorner = grid20.countSpots()

        return oddSpots * numberOfBaseFullSquares +
               evenSpots * numberOfAltFullSquares +
               chunkySE + chunkySW + chunkyNW + chunkyNE +
               smolSE + smolSW + smolNW + smolNE +
               rightCorner + bottomCorner + topCorner + leftCorner
    }

    val testInput = readInput("Day21_test")
    check(part1(testInput, 6) == 16)

    val input = readInput("Day21")
    part1(input, 64).print("Part 1")
    part2(input, 26501365).print("Part 2")
}