fun calculatePossibleSpotsMemoized(report: String, counts: List<Int>,
                                   seen: HashMap<Pair<String, List<Int>>, Long>): Long {
    if (seen.contains(Pair(report, counts))) {
        // hallelujah
        return seen.getValue(Pair(report, counts))
    }

    val len = counts[0]
    val window = report.windowed(len)
    val extendedWindow = report.windowed(len + 1)

    if (report.length < len) {
        if (!seen.contains(Pair(report, counts))) {
            seen[Pair(report, counts)] = 0
        }
        return 0L
    }

    var total = 0L
    for ((index, check) in window.zip(extendedWindow).withIndex()) {
        if (check.first.first() != '#' && check.first.any { it == '.' }) {
            continue
        }

        if (counts.size == 1 && check.first.first() == '#' && report.substring(len + index).contains('#')) {
            // can't possibly have a solution, return current total
            if (!seen.contains(Pair(report, counts))) {
                seen[Pair(report, counts)] = total
            }
            return total
        }

        if (counts.size == 1 && check.first.first() != '#' && report.substring(len + index).contains('#')) {
            // # exists further along, but not bound by starting # yet, try moving further
            continue
        }

        if (check.first.first() == '#') {
            if (check.first.any { it == '.' }) {
                // no possible solutions as # bounds start, but . in range
                if (!seen.contains(Pair(report, counts))) {
                    seen[Pair(report, counts)] = total
                }
                return total
            }

            if (check.second.last() == '#') {
                // can't fit here and can't move further along
                if (!seen.contains(Pair(report, counts))) {
                    seen[Pair(report, counts)] = total
                }
                return total
            }

            // must start here
            if (counts.size > 1) {
                // more to count
                val childCount = calculatePossibleSpotsMemoized(
                    report.substring(len + 1 + index),
                    counts.subList(1, counts.size), seen
                )
                total += childCount
            } else {
                // this is last number, and we can't move further along (bound by starting #) therefore return
                if (!seen.contains(Pair(report, counts))) {
                    seen[Pair(report, counts)] = total + 1
                }
                return total + 1
            }

            if (!seen.contains(Pair(report, counts))) {
                seen[Pair(report, counts)] = total
            }
            return total
        }

        // must start with ? and only contain ? and #
        if (check.second.last() == '#') {
            // not a possible spot, as next number would need to be adjacent
            continue
        }

        // default recursion
        if (counts.size > 1) {
            // more to count
            val childCount = calculatePossibleSpotsMemoized(
                report.substring(len + 1 + index),
                counts.subList(1, counts.size), seen
            )
            total += childCount
        } else {
            total++
        }
    }

    if (report.substring(report.length - len).none { it == '.' } && counts.size == 1) {
        if (!seen.contains(Pair(report, counts))) {
            seen[Pair(report, counts)] = total + 1
        }
        return total + 1
    }

    if (!seen.contains(Pair(report, counts))) {
        seen[Pair(report, counts)] = total
    }
    return total
}

fun main() {
    fun part1(input: List<String>): Long {
        val result = input.sumOf { line ->
            val (report, counts) = line.split(" ")
            val countsNum = counts.split(",").map { it.toInt() }
            val seen = HashMap<Pair<String, List<Int>>, Long>()
            calculatePossibleSpotsMemoized(report, countsNum, seen)
        }

        return result
    }

    fun part2(input: List<String>): Long {
        val result = input.sumOf { line ->
            val (report, counts) = line.split(" ")
            var reportExtended = report
            var countsExtended = counts
            for (i in 0..3) {
                reportExtended += "?$report"
                countsExtended += ",$counts"
            }

            val countsNum = countsExtended.split(",").map { it.toInt() }
            val seen = HashMap<Pair<String, List<Int>>, Long>()
            calculatePossibleSpotsMemoized(reportExtended, countsNum, seen)
        }

        return result
    }

    val testInput = readInput("Day12_test")
    check(part1(testInput) == 21L)
    check(part2(testInput) == 525152L)

    val input = readInput("Day12")
    part1(input).print("Part 1")
    part2(input).print("Part 2")
}