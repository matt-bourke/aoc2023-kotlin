enum class Comparison {
    Greater,
    Less
}

fun main() {
    fun part1(input: String): Int {
        data class Part(val x: Int, val m: Int, val a: Int, val s: Int)
        operator fun Part.get(cat: String) = when (cat) {
            "x" -> x; "m" -> m; "a" -> a; "s" -> s; else -> throw Exception("invalid category")
        }

        val (workflowLines, partsLines) = input.split("\r\n\r\n")
        val workflows = HashMap<String, ArrayList<(Part) -> String?>>()

        for (workflowLine in workflowLines.split("\r\n")) {
            val (name, rules) = workflowLine.dropLast(1).split("{")
            val workFlowRules = ArrayList<(Part) -> String?>()
            for (rule in rules.split(",")) {
                if (rule.contains(">")) {
                    val (category, rest) = rule.split(">")
                    val (compareVal, destination) = rest.split(":")
                    workFlowRules.add { part ->
                        if (part[category] > compareVal.toInt()) {
                            destination
                        } else {
                            null
                        }
                    }
                } else if (rule.contains("<")) {
                    val (category, rest) = rule.split("<")
                    val (compareVal, destination) = rest.split(":")
                    workFlowRules.add { part ->
                        if (part[category] < compareVal.toInt()) {
                            destination
                        } else {
                            null
                        }
                    }
                } else {
                    workFlowRules.add { _ -> rule }
                }
            }

            workflows[name] = workFlowRules
        }

        val parts = ArrayList<Part>()
        for (part in partsLines.split("\r\n")) {
            val categories = part.dropLast(1).split(",")
            val x = categories[0].split("=")[1].toInt()
            val m = categories[1].split("=")[1].toInt()
            val a = categories[2].split("=")[1].toInt()
            val s = categories[3].split("=")[1].toInt()
            parts.add(Part(x, m, a, s))
        }

        val accepted = ArrayList<Part>()
        for (part in parts) {
            val start = workflows["in"]!!
            var rulesToRun = start.toMutableList()

            while (rulesToRun.isNotEmpty()) {
                val rule = rulesToRun.first()
                rulesToRun.remove(rule)

                val next = rule(part)
                if (next != null) {
                    if (next == "A") {
                        accepted.add(part)
                        break
                    } else if (next == "R") {
                        break
                    } else {
                        rulesToRun.clear()
                        rulesToRun = workflows[next]!!.toMutableList()
                    }
                }
            }
        }

        return accepted.sumOf { part -> part.x + part.m + part.a + part.s }
    }

    fun part2(input: String): Long {
        data class WorkflowRule(val cat: String, val compare: Comparison, val value: Int, var destination: String)
        data class WorkflowData(val rules: ArrayList<WorkflowRule>, val elseDestination: String)
        data class State(var x: IntRange = IntRange(1, 4000), var m: IntRange = IntRange(1, 4000),
                         var a: IntRange = IntRange(1, 4000), var s: IntRange = IntRange(1, 4000))
        operator fun State.get(category: String) = when (category) {
            "x" -> x; "m" -> m; "a" -> a; "s" -> s; else -> throw Exception("invalid category in state")
        }
        operator fun State.set(category: String, value: IntRange) {
            when (category) {
                "x" -> x = value; "m" -> m = value; "a" -> a = value; "s" -> s = value
                else -> throw Exception("invalid category in state")
            }
        }

        fun traverse(currentName: String, graph: HashMap<String, WorkflowData>,
                     acceptedRanges: ArrayList<State>, state: State) {
            // traverse the graph using dfs, making changes to x, m, a, and s state
            // if A is encountered, add new entry to acceptedRanges array
            if (currentName == "R") return
            if (currentName == "A") {
                acceptedRanges.add(state)
                return
            }

            val current = graph.getValue(currentName)
            for (rule in current.rules) {
                when (rule.compare) {
                    Comparison.Greater -> {
                        // rule.cat must be greater than rule.value
                        // create new state with new ranges
                        val nextState = state.copy()
                        nextState[rule.cat] = IntRange(rule.value + 1, nextState[rule.cat].last)

                        // traverse next node(s)
                        traverse(rule.destination, graph, acceptedRanges, nextState)

                        // update current state to inverse of current rule for subsequent rules to run
                        state[rule.cat] = IntRange(state[rule.cat].first, rule.value)
                    }

                    Comparison.Less -> {
                        // rule.cat must be less than rule.value
                        val nextState = state.copy()
                        nextState[rule.cat] = IntRange(nextState[rule.cat].first, rule.value - 1)
                        traverse(rule.destination, graph, acceptedRanges, nextState)
                        state[rule.cat] = IntRange(rule.value, state[rule.cat].last)
                    }
                }
            }

            traverse(current.elseDestination, graph, acceptedRanges, state)
        }

        val workflowLines = input.split("\r\n\r\n")[0]
        val workflows = HashMap<String, WorkflowData>()

        for (workflowLine in workflowLines.split("\r\n")) {
            val (name, rules) = workflowLine.dropLast(1).split("{")
            val workFlowRules = ArrayList<WorkflowRule>()
            var elseDestination = ""
            for (rule in rules.split(",")) {
                if (rule.contains(">")) {
                    val (category, rest) = rule.split(">")
                    val (compareVal, destination) = rest.split(":")
                    workFlowRules.add(WorkflowRule(category, Comparison.Greater, compareVal.toInt(), destination))
                } else if (rule.contains("<")) {
                    val (category, rest) = rule.split("<")
                    val (compareVal, destination) = rest.split(":")
                    workFlowRules.add(WorkflowRule(category, Comparison.Less, compareVal.toInt(), destination))
                } else {
                    elseDestination = rule
                }
            }

            workflows[name] = WorkflowData(workFlowRules, elseDestination)
        }

        workflows["A"] = WorkflowData(ArrayList(), "A")
        workflows["R"] = WorkflowData(ArrayList(), "R")

        val acceptedRanges = ArrayList<State>()
        val initialState = State()
        traverse("in", workflows, acceptedRanges, initialState)

        return acceptedRanges.sumOf { range ->
            range.x.count().toLong() * range.m.count().toLong() * range.a.count().toLong() * range.s.count().toLong()
        }
    }

    // test examples
    val testInput = readInputRaw("Day19_test")
    check(part1(testInput) == 19114)
    check(part2(testInput) == 167409079868000L)

    val input = readInputRaw("Day19")
    part1(input).print("Part 1")
    part2(input).print("Part 2")
}