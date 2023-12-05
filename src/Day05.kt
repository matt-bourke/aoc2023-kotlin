import kotlin.io.path.Path
import kotlin.io.path.readText

fun main() {
    fun parseSeedsPart1(input: String): List<Long> {
        return input.split(": ")[1].split(" ").map{ it.toLong() }
    }

    fun parseSeedsPart2(input: String): List<LongRange> {
        val seedRanges = ArrayList<LongRange>()
        val seedNumbers = input.split(": ")[1].split(" ").map{ it.toLong() }
        val seedPairs = seedNumbers.size / 2
        for (n in 0..<seedPairs) {
            seedRanges.add(seedNumbers[n*2]..<(seedNumbers[n*2] + seedNumbers[(n*2)+1]))
        }
        return seedRanges
    }

    fun parseMapSection(section: String): List<Triple<Long, Long, Long>> {
        val ranges = ArrayList<Triple<Long, Long, Long>>()
        val lines = section.split("\r\n")
        for (i in 1..<lines.size) {
            val (destinationStart, sourceStart, range) = lines[i].split(" ").map { it.toLong() }
            ranges.add(Triple(destinationStart, sourceStart, range))
        }
        return ranges
    }

    fun part1(input: String): Long {
        val sections = input.split("\r\n\r\n")
        val seeds = parseSeedsPart1(sections[0])
        val seedToSoil = parseMapSection(sections[1])
        val soilToFertiliser = parseMapSection(sections[2])
        val fertiliserToWater = parseMapSection(sections[3])
        val waterToLight = parseMapSection(sections[4])
        val lightToTemperature = parseMapSection(sections[5])
        val temperatureToHumidity = parseMapSection(sections[6])
        val humidityToLocation = parseMapSection(sections[7])

        var minLocation = Long.MAX_VALUE
        for (seed in seeds) {
            var soil = seed
            for (soilMap in seedToSoil) {
                if (seed in soilMap.second..<(soilMap.second + soilMap.third)) {
                    soil = soilMap.first + (seed - soilMap.second)
                    break
                }
            }

            var fertiliser = soil
            for (fertiliserMap in soilToFertiliser) {
                if (soil in fertiliserMap.second..<(fertiliserMap.second + fertiliserMap.third)) {
                    fertiliser = fertiliserMap.first + (soil - fertiliserMap.second)
                    break
                }
            }

            var water = fertiliser
            for (waterMap in fertiliserToWater) {
                if (fertiliser in waterMap.second..<(waterMap.second + waterMap.third)) {
                    water = waterMap.first + (fertiliser - waterMap.second)
                    break
                }
            }

            var light = water
            for (lightMap in waterToLight) {
                if (water in lightMap.second..<(lightMap.second + lightMap.third)) {
                    light = lightMap.first + (water - lightMap.second)
                    break
                }
            }

            var temperature = light
            for (temperatureMap in lightToTemperature) {
                if (light in temperatureMap.second..<(temperatureMap.second + temperatureMap.third)) {
                    temperature = temperatureMap.first + (light - temperatureMap.second)
                    break
                }
            }

            var humidity = temperature
            for (humidityMap in temperatureToHumidity) {
                if (temperature in humidityMap.second..<(humidityMap.second + humidityMap.third)) {
                    humidity = humidityMap.first + (temperature - humidityMap.second)
                    break
                }
            }

            var location = humidity
            for (locationMap in humidityToLocation) {
                if (humidity in locationMap.second..<(locationMap.second + locationMap.third)) {
                    location = locationMap.first + (humidity - locationMap.second)
                    break
                }
            }

            if (location < minLocation) {
                minLocation = location
            }
        }

        return minLocation
    }

    fun part2(input: String): Long {
        val sections = input.split("\r\n\r\n")
        val seedRanges = parseSeedsPart2(sections[0])
        val seedToSoil = parseMapSection(sections[1])
        val soilToFertiliser = parseMapSection(sections[2])
        val fertiliserToWater = parseMapSection(sections[3])
        val waterToLight = parseMapSection(sections[4])
        val lightToTemperature = parseMapSection(sections[5])
        val temperatureToHumidity = parseMapSection(sections[6])
        val humidityToLocation = parseMapSection(sections[7])

        var globalMinimumLocation = Long.MAX_VALUE
        for (seedRange in seedRanges) {
            println("Beginning range $seedRange")
            var minLocation = Long.MAX_VALUE
            for (seed in seedRange) {
                var soil = seed
                for (soilMap in seedToSoil) {
                    if (seed in soilMap.second..<(soilMap.second + soilMap.third)) {
                        soil = soilMap.first + (seed - soilMap.second)
                        break
                    }
                }

                var fertiliser = soil
                for (fertiliserMap in soilToFertiliser) {
                    if (soil in fertiliserMap.second..<(fertiliserMap.second + fertiliserMap.third)) {
                        fertiliser = fertiliserMap.first + (soil - fertiliserMap.second)
                        break
                    }
                }

                var water = fertiliser
                for (waterMap in fertiliserToWater) {
                    if (fertiliser in waterMap.second..<(waterMap.second + waterMap.third)) {
                        water = waterMap.first + (fertiliser - waterMap.second)
                        break
                    }
                }

                var light = water
                for (lightMap in waterToLight) {
                    if (water in lightMap.second..<(lightMap.second + lightMap.third)) {
                        light = lightMap.first + (water - lightMap.second)
                        break
                    }
                }

                var temperature = light
                for (temperatureMap in lightToTemperature) {
                    if (light in temperatureMap.second..<(temperatureMap.second + temperatureMap.third)) {
                        temperature = temperatureMap.first + (light - temperatureMap.second)
                        break
                    }
                }

                var humidity = temperature
                for (humidityMap in temperatureToHumidity) {
                    if (temperature in humidityMap.second..<(humidityMap.second + humidityMap.third)) {
                        humidity = humidityMap.first + (temperature - humidityMap.second)
                        break
                    }
                }

                var location = humidity
                for (locationMap in humidityToLocation) {
                    if (humidity in locationMap.second..<(locationMap.second + locationMap.third)) {
                        location = locationMap.first + (humidity - locationMap.second)
                        break
                    }
                }

                if (location < minLocation) {
                    minLocation = location
                }

                if (minLocation < globalMinimumLocation) {
                    globalMinimumLocation = minLocation
                }
            }
        }

        return globalMinimumLocation
    }

    val testInput = Path("src/input_files/Day05_test.txt").readText()
    check(part1(testInput).toInt() == 35)
    check(part2(testInput).toInt() == 46)

    val input = Path("src/input_files/Day05.txt").readText()
    part1(input).print("Part 1")
    part2(input).print("Part 2")
}