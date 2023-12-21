import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

enum class Pulse {
    High,
    Low
}

enum class OnOff {
    On,
    Off
}

data class Signal(val pulse: Pulse, val destination: SignalProcessor, val from: String)
fun Signal.process(modules: HashMap<String, SignalProcessor>): ArrayList<Signal> {
    val fromProcessor = modules.getValue(from)
    return modules.getValue(destination.name).process(pulse, fromProcessor)
}

interface SignalProcessor {
    val name: String
    val connections: ArrayList<SignalProcessor>
    fun process(pulse: Pulse, from: SignalProcessor): ArrayList<Signal>
    fun propagate(pulse: Pulse): ArrayList<Signal> {
        val queue = ArrayList<Signal>()
        for (connection in connections) {
            queue.add(Signal(pulse, connection, this.name))
        }
        return queue
    }
}

data class FlipFlop(override val name: String,
                    override val connections: ArrayList<SignalProcessor> = ArrayList(),
                    var state: OnOff = OnOff.Off) : SignalProcessor {
    override fun process(pulse: Pulse, from: SignalProcessor): ArrayList<Signal> {
        if (pulse == Pulse.Low) {
            return if (state == OnOff.Off) {
                state = OnOff.On
                propagate(Pulse.High)
            } else {
                state = OnOff.Off
                propagate(Pulse.Low)
            }
        }

        return ArrayList()
    }

    override fun toString(): String {
        return "FlipFlop(name = $name,  state = $state, connections = ${connections.map { it.name }})"
    }
}

data class Conjunction(override val name: String,
                       val inputs: HashMap<String, Pulse> = HashMap(),
                       override val connections: ArrayList<SignalProcessor> = ArrayList()) : SignalProcessor {
    override fun process(pulse: Pulse, from: SignalProcessor): ArrayList<Signal> {
        inputs[from.name] = pulse

        return if (inputs.values.all { state -> state == Pulse.High }) {
            propagate(Pulse.Low)
        } else {
            propagate(Pulse.High)
        }
    }

    override fun toString(): String {
        val inputsString = ArrayList<String>()
        for (input in inputs) {
            inputsString.add("name = ${input.key}: state = ${input.value}")
        }
        return "Conjunction(name = $name, connections = ${connections.map { it.name }}, inputs = ${inputsString})"
    }
}

data class Broadcaster(override val name: String = "broadcaster",
                       override val connections: ArrayList<SignalProcessor> = ArrayList()): SignalProcessor {
    override fun process(pulse: Pulse, from: SignalProcessor): ArrayList<Signal> {
        return propagate(pulse)
    }

    override fun toString(): String {
        return "Broadcaster(name = $name, connections = ${connections.map { it.name }})"
    }
}

data class Button(override val name: String,
                  override val connections: ArrayList<SignalProcessor>): SignalProcessor {
    override fun process(pulse: Pulse, from: SignalProcessor): ArrayList<Signal> {
        return ArrayList()
    }
}

data class Output(override val name: String,
                  override val connections: ArrayList<SignalProcessor>): SignalProcessor {
    override fun process(pulse: Pulse, from: SignalProcessor): ArrayList<Signal> {
        return ArrayList()
    }

}

fun main() {
    fun part1(input: List<String>): Int {
        val signalQueue = LinkedList<Signal>()
        val modules = HashMap<String, SignalProcessor>()
        val parseMapping = HashMap<String, String>()
        for (line in input) {
            val (moduleName, connectionsName) = line.split(" -> ")

            val prefix = moduleName[0]
            var strippedNamed = moduleName
            if (prefix == '%' || prefix == '&') {
                strippedNamed = moduleName.drop(1)
            }
            parseMapping[strippedNamed] = connectionsName

            val module: SignalProcessor = when (prefix) {
                '%' -> FlipFlop(strippedNamed)
                '&' -> Conjunction(strippedNamed)
                else -> Broadcaster(strippedNamed)
            }

            modules[strippedNamed] = module
        }

        for (entry in parseMapping.entries) {
            val connections = entry.value.split(", ")
            for (connection in connections) {
                if (!modules.containsKey(connection)) {
                    // output module
                    val output = Output(connection, ArrayList())
                    modules[connection] = output
                    modules[entry.key]!!.connections.add(modules[connection]!!)
                } else {
                    modules[entry.key]!!.connections.add(modules[connection]!!)
                }

                if (modules[connection] is Conjunction) {
                    (modules[connection] as Conjunction).inputs[entry.key] = Pulse.Low
                }
            }
        }

        val broadcaster = modules["broadcaster"] as Broadcaster
        modules["button"] = Button("button", arrayListOf(broadcaster))

        // press the button, 1000 times
        var highPulses = 0
        var lowPulses = 0
        for (i in 1..1000) {
            signalQueue.add(Signal(Pulse.Low, broadcaster, "button"))
            while (signalQueue.isNotEmpty()) {
                val signal = signalQueue.pop()
                if (signal.pulse == Pulse.High) {
                    highPulses++
                } else {
                    lowPulses++
                }
                val propagations = signal.process(modules)
                for (propagation in propagations) {
                    signalQueue.add(propagation)
                }
            }
        }

        return highPulses * lowPulses
    }

    fun part2(input: List<String>): Long {
        val signalQueue = LinkedList<Signal>()
        val modules = HashMap<String, SignalProcessor>()
        val parseMapping = HashMap<String, String>()
        for (line in input) {
            val (moduleName, connectionsName) = line.split(" -> ")

            val prefix = moduleName[0]
            var strippedNamed = moduleName
            if (prefix == '%' || prefix == '&') {
                strippedNamed = moduleName.drop(1)
            }
            parseMapping[strippedNamed] = connectionsName

            val module: SignalProcessor = when (prefix) {
                '%' -> FlipFlop(strippedNamed)
                '&' -> Conjunction(strippedNamed)
                else -> Broadcaster(strippedNamed)
            }

            modules[strippedNamed] = module
        }

        for (entry in parseMapping.entries) {
            val connections = entry.value.split(", ")
            for (connection in connections) {
                if (!modules.containsKey(connection)) {
                    // output module
                    val output = Output(connection, ArrayList())
                    modules[connection] = output
                    modules[entry.key]!!.connections.add(modules[connection]!!)
                } else {
                    modules[entry.key]!!.connections.add(modules[connection]!!)
                }

                if (modules[connection] is Conjunction) {
                    (modules[connection] as Conjunction).inputs[entry.key] = Pulse.Low
                }
            }
        }

        val broadcaster = modules["broadcaster"] as Broadcaster
        modules["button"] = Button("button", arrayListOf(broadcaster))

        // graph is broken up into 4 sections
        // XJ, QS, KZ, and KM are the terminal points for each section, before being combines in GQ
        // finding time for each section to go high and then doing LCM is much faster
        // don't love this solution but hey it works
        var buttonPresses = 0L
        var cyclesToXJ = 0L
        var cyclesToQS = 0L
        var cyclesToKZ = 0L
        var cyclesToKM = 0L

        while (true) {
            buttonPresses += 1L
            signalQueue.add(Signal(Pulse.Low, broadcaster, "button"))
            while (signalQueue.isNotEmpty()) {
                if (cyclesToXJ * cyclesToQS * cyclesToKZ * cyclesToKM != 0L) {
                    var runningLCM = findLCM(cyclesToXJ, cyclesToQS)
                    runningLCM = findLCM(runningLCM, cyclesToKZ)
                    runningLCM = findLCM(runningLCM, cyclesToKM)
                    return runningLCM
                }

                val signal = signalQueue.pop()
                if (signal.pulse == Pulse.High) {
                    if (signal.from == "xj" && cyclesToXJ == 0L) cyclesToXJ = buttonPresses
                    if (signal.from == "qs" && cyclesToQS == 0L) cyclesToQS = buttonPresses
                    if (signal.from == "kz" && cyclesToKZ == 0L) cyclesToKZ = buttonPresses
                    if (signal.from == "km" && cyclesToKM == 0L) cyclesToKM = buttonPresses
                }
                val propagations = signal.process(modules)
                for (propagation in propagations) {
                    signalQueue.add(propagation)
                }
            }
        }
    }

    // test examples
    val testInput = readInput("Day20_test")
    check(part1(testInput) == 11687500)

    val input = readInput("Day20")
    part1(input).print("Part 1")
    part2(input).print("Part 2")
}