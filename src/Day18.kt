fun main() {
    data class Point(var x: Long, var y: Long)

    fun calculateArea(vertices: ArrayList<Point>, perimeter: Long): Long {
        var xySum = 0L
        for (i in 0..<vertices.size - 1) {
            xySum += vertices[i].x * vertices[i + 1].y
        }

        var yxSum = 0L
        for (i in 0..<vertices.size - 1) {
            yxSum += vertices[i].y * vertices[i + 1].x
        }

        return ((xySum - yxSum) + (perimeter + 2)) / 2
    }

    fun part1(input: List<String>): Int {
        val vertices = ArrayList<Point>()
        val start = Point(0, 0)
        val end = Point(0, 0)
        var perimeter = 0L
        vertices.add(start)

        input.forEach { line ->
            val segments = line.split(" ")
            val distance = segments[1].toInt()
            when (segments[0]) {
                "R" -> {
                    end.x += distance
                    vertices.add(end.copy())
                    perimeter += end.x - start.x
                    start.x = end.x
                }
                "D" -> {
                    end.y += distance
                    vertices.add(end.copy())
                    perimeter += end.y - start.y
                    start.y = end.y
                }
                "L" -> {
                    end.x -= distance
                    vertices.add(end.copy())
                    perimeter += start.x - end.x
                    start.x = end.x
                }
                "U" -> {
                    end.y -= distance
                    vertices.add(end.copy())
                    perimeter += start.y - end.y
                    start.y = end.y
                }
                else -> throw Exception("invalid direction")
            }
        }

        return calculateArea(vertices, perimeter).toInt()
    }

    fun part2(input: List<String>): Long {
        val vertices = ArrayList<Point>()
        val start = Point(0, 0)
        val end = Point(0, 0)
        var perimeter = 0L
        vertices.add(start)

        input.forEach { line ->
            val hex = line.split(" ")[2]
            val distance = hex.substring(2, 7).toLong(radix = 16)
            when (hex.substring(7, 8)) {
                "0" -> {
                    end.x += distance
                    vertices.add(end.copy())
                    perimeter += end.x - start.x
                    start.x = end.x
                }
                "1" -> {
                    end.y += distance
                    vertices.add(end.copy())
                    perimeter += end.y - start.y
                    start.y = end.y
                }
                "2" -> {
                    end.x -= distance
                    vertices.add(end.copy())
                    perimeter += start.x - end.x
                    start.x = end.x
                }
                "3" -> {
                    end.y -= distance
                    vertices.add(end.copy())
                    perimeter += start.y - end.y
                    start.y = end.y
                }
                else -> throw Exception("invalid direction")
            }
        }

        return calculateArea(vertices, perimeter)
    }

    // test examples
    val testInput = readInput("Day18_test")
    check(part1(testInput) == 62)
    check(part2(testInput) == 952408144115L)

    val input = readInput("Day18")
    part1(input).print("Part 1")
    part2(input).print("Part 2")
}