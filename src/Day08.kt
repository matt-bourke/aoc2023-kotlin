fun main() {
    fun part1(input: String): Int {
        val (sequence, map) = input.split("\r\n\r\n")
        val locations = HashMap<String, Pair<String, String>>()
        val mapRows = map.split("\r\n")

        for (row in mapRows) {
            val (location, nextLocations) = row.split(" = ")
            val (left, right) = nextLocations.subSequence(1, nextLocations.length-1).split(", ")
            locations[location] = Pair(left, right)
        }

        var steps = 0
        var currentLocation = "AAA"

        while (currentLocation != "ZZZ") {
            val selection = sequence[(steps % sequence.length)]
            when (selection) {
                'L' -> currentLocation = locations.getValue(currentLocation).first
                'R' -> currentLocation = locations.getValue(currentLocation).second
            }

            steps++
        }

        return steps
    }

    fun part2(input: String): Long {
        val (sequence, map) = input.split("\r\n\r\n")
        val locations = HashMap<String, Pair<String, String>>()
        val mapRows = map.split("\r\n")

        for (row in mapRows) {
            val (location, nextLocations) = row.split(" = ")
            val (left, right) = nextLocations.subSequence(1, nextLocations.length-1).split(", ")
            locations[location] = Pair(left, right)
        }

        var currentLCM = 1L
        for (location in locations.keys.filter { it.endsWith('A') }) {
            var steps = 0L
            var currentLocation = location

            while (!currentLocation.endsWith('Z')) {
                val selection = sequence[(steps % sequence.length.toLong()).toInt()]
                when (selection) {
                    'L' -> currentLocation = locations.getValue(currentLocation).first
                    'R' -> currentLocation = locations.getValue(currentLocation).second
                }

                steps++
            }
            currentLCM = findLCM(currentLCM, steps)
        }

        return currentLCM
    }

    var testInput = readInputRaw("Day08_Part1_test")
    check(part1(testInput) == 6)

    testInput = readInputRaw("Day08_Part2_test")
    check(part2(testInput) == 6L)

    val input = readInputRaw("Day08")
    part1(input).print("Part 1")
    part2(input).print("Part 2")
}