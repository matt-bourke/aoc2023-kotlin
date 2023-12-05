import kotlin.io.path.Path
import kotlin.io.path.readText
import kotlin.math.max
import kotlin.math.min

fun main() {
    fun parseSeedsPart1(input: String): List<Long> {
        return input.split(": ")[1].split(" ").map{ it.toLong() }
    }

    fun parseSeedsPart2(input: String): ArrayList<LongRange> {
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
        val mapTransforms = ArrayList<List<Triple<Long, Long, Long>>>()
        for (i in 1..<sections.size) {
            mapTransforms.add(parseMapSection(sections[i]))
        }

        var minLocation = Long.MAX_VALUE
        for (seed in seeds) {
            var transformedValue = seed
            for (transformationRanges in mapTransforms) {
                for (transform in transformationRanges) {
                    if (transformedValue in transform.second..<(transform.second + transform.third)) {
                        transformedValue = transform.first + (transformedValue - transform.second)
                        break
                    }
                }
            }

            if (transformedValue < minLocation) {
                minLocation = transformedValue
            }
        }

        return minLocation
    }

    fun part2(input: String): Long {
        val sections = input.split("\r\n\r\n")
        var seedRanges = parseSeedsPart2(sections[0])
        val mapTransforms = ArrayList<List<Triple<Long, Long, Long>>>()
        for (i in 1..<sections.size) {
            mapTransforms.add(parseMapSection(sections[i]))
        }

        for (mapTransform in mapTransforms) {
            val nextTransformedSeedRange = ArrayList<LongRange>()
            val remainderSeedRange = ArrayList<LongRange>()
            for (transformationMap in mapTransform) {
                remainderSeedRange.clear()
                val transformationRangeEnd = transformationMap.second + transformationMap.third
                val transformationDifference = transformationMap.first - transformationMap.second
                for (seedRange in seedRanges) {
                    if (seedRange.last < transformationMap.second ||
                        seedRange.first > transformationRangeEnd) {
                        remainderSeedRange.add(seedRange)
                    } else {
                        val minOverlap = max(seedRange.first, transformationMap.second)
                        val maxOverlap = min(seedRange.last, transformationRangeEnd)
                        val newStart = minOverlap + transformationDifference
                        val newEnd = maxOverlap + transformationDifference
                        nextTransformedSeedRange.add(LongRange(newStart, newEnd))

                        if (seedRange.first < transformationMap.second) {
                            remainderSeedRange.add(LongRange(seedRange.first, transformationMap.second - 1))
                        }

                        if (seedRange.last > transformationRangeEnd) {
                            remainderSeedRange.add(LongRange(transformationRangeEnd, seedRange.last))
                        }
                    }
                }
                seedRanges = ArrayList(remainderSeedRange)
            }
            seedRanges = ArrayList(remainderSeedRange)
            seedRanges.addAll(nextTransformedSeedRange)
        }

        return seedRanges.minOf { it.first }
    }

    val testInput = Path("src/input_files/Day05_test.txt").readText()
    check(part1(testInput).toInt() == 35)
    check(part2(testInput).toInt() == 46)

    val input = Path("src/input_files/Day05.txt").readText()
    part1(input).print("Part 1")
    part2(input).print("Part 2")
}