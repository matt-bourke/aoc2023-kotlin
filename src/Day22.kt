fun main() {
    data class Point3D(var x: Int, var y: Int, var z: Int)
    data class Block(val start: Point3D, val end: Point3D)
    fun Block.overlaps(other: Block): Boolean {
        for (blockX in other.start.x..other.end.x) {
            for (x in start.x..end.x) {
                for (y in start.y..end.y) {
                    for (blockY in other.start.y..other.end.y) {
                        if (blockX == x && blockY == y) {
                            return true
                        }
                    }
                }
            }
        }
        return false
    }

    fun Block.getSupports(stack: List<Block>): ArrayList<Block> {
        val supports = ArrayList<Block>()
        val blocksInLayerBelow = stack.filter { it.end.z == start.z - 1 }
        for (block in blocksInLayerBelow) {
            if (this.overlaps(block)) {
                supports.add(block)
            }
        }
        return supports
    }

    fun Block.getSupporting(stack: List<Block>): ArrayList<Block> {
        val blocksInLayerAbove = stack.filter { it.start.z == end.z + 1 }
        val supporting = ArrayList<Block>()
        for (block in blocksInLayerAbove) {
            if (this.overlaps(block)) {
                supporting.add(block)
            }
        }
        return supporting
    }

    fun Block.canDescend(stack: List<Block>): Boolean {
        if (start.z == 1) return false
        return this.getSupports(stack).isEmpty()
    }

    fun List<Block>.descend() {
        for (block in this) {
            while (block.canDescend(this)) {
                block.start.z--
                block.end.z--
            }
        }
    }

    fun parse(input: List<String>): List<Block> {
        return input.map { line ->
            val (start, end) = line.split("~")
            val (sx, sy, sz) = start.split(",").map { it.toInt() }
            val (ex, ey, ez) = end.split(",").map { it.toInt() }
            Block(Point3D(sx, sy, sz), Point3D(ex, ey, ez))
        }.sortedBy { it.start.z }
    }

    fun part1(input: List<String>): Int {
        val blocks = parse(input)
        blocks.descend()

        // can remove block if ALL block it's supporting, is supported by another block
        return blocks.count { block ->
            block.getSupporting(blocks).all { supporting ->
                supporting.getSupports(blocks).any { s -> s != block }
            }
        }
    }

    fun part2(input: List<String>): Int {
        val blocks = parse(input)
        blocks.descend()

        val cascades = HashMap<Block, Int>()
        val markedForDestruction = HashSet<Block>()
        val sortedBlocks = blocks.sortedBy { it.start.z }
        for (block in sortedBlocks) {
            markedForDestruction.add(block)
            var lastZMarked = block.end.z + 1
            for (above in sortedBlocks.filter { it.start.z > block.end.z }) {
                if (above.start.z > lastZMarked) break

                if (above.getSupports(sortedBlocks).all { markedForDestruction.contains(it) }) {
                    markedForDestruction.add(above)

                    if (above.end.z >= lastZMarked) {
                        lastZMarked = above.end.z + 1
                    }
                }
            }
            cascades[block] = markedForDestruction.size - 1
            markedForDestruction.clear()
        }

        return cascades.values.sum()
    }

    val testInput = readInput("Day22_test")
    check(part1(testInput) == 5)
    check(part2(testInput) == 7)

    val input = readInput("Day22")
    part1(input).print("Part 1")
    part2(input).print("Part 2")
}