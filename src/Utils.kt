import java.math.BigInteger
import java.security.MessageDigest
import kotlin.io.path.Path
import kotlin.io.path.readLines
import kotlin.io.path.readText
import kotlin.math.abs
import kotlin.math.max
import kotlin.system.exitProcess

/**
 * Reads lines from the given input txt file.
 */
fun readInput(name: String): List<String> {
    try {
        return Path("src/input_files/$name.txt").readLines()
    } catch (e: Exception) {
        System.err.println("[ ERROR ] Failed to open file: $name\n└╴(${e.javaClass})")
        exitProcess(1)
    }
}

/**
 * Reads raw text from the given input txt file.
 */
fun readInputRaw(name: String): String {
    try {
        return Path("src/input_files/$name.txt").readText()
    } catch (e: Exception) {
        System.err.println("[ ERROR ] Failed to open file: $name\n└╴(${e.javaClass})")
        exitProcess(1)
    }
}

/**
 * Converts string to md5 hash.
 */
fun String.md5() = BigInteger(1, MessageDigest.getInstance("MD5").digest(toByteArray()))
    .toString(16)
    .padStart(32, '0')

/**
 * The cleaner shorthand for printing output.
 */
fun Any?.print(prefix: String? = null) = println("${ if (prefix != null) "$prefix: " else ""}$this")

/**
 * Checks if two integer ranges overlap
 */
fun IntRange.overlaps(other: IntRange): Boolean {
    return this.first <= other.last && other.first <= this.last
}

/**
 * Computes the product of all values in a list
 */
fun <T> Collection<T>.productOf(selector: (T, Int) -> Int): Int {
    if (this.isEmpty()) {
        return 0
    }

    val s = ArrayList<String>()
    s.isEmpty()

    var product = 1
    for ((i, element) in this.withIndex()) {
        product *= selector(element, i)
    }
    return product
}

/**
 * Calculated sum of selector, providing index
 */
fun <T> Iterable<T>.sumOfIndexed(selector: (Int, T) -> Int): Int {
    var sum = 0
    for ((i, element) in this.withIndex()) {
        sum += selector(i, element)
    }
    return sum
}

/**
 * Find 'Least Common Multiple' (LCM) of two numbers
 */
fun findLCM(x: Long, y: Long): Long {
    val largest = max(x, y)
    val upperBound = x * y
    var currentLCM = largest
    while (currentLCM <= upperBound) {
        if (currentLCM % x == 0L && currentLCM % y == 0L) {
            // both currentLCM divisible by both x and y => found result
            break
        }
        currentLCM += largest
    }
    return currentLCM
}

/**
 * Find manhattan distance between two points
 */
fun manhattan(x1: Int, y1: Int, x2: Int, y2: Int): Int {
    return abs(y2 - y1) + abs(x2 - x1)
}

/**
 * Returns inputs as pair where first = min and second = max
 */
fun findMinMax(a: Int, b: Int): Pair<Int, Int> {
    return if (b < a) {
        Pair(b, a)
    } else {
        Pair(a, b)
    }
}