import java.math.BigInteger
import java.security.MessageDigest
import kotlin.io.path.Path
import kotlin.io.path.readLines
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