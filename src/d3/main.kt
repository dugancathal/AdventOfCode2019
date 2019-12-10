package d3

import commons.*
import java.io.File

private val input by lazy { File("src/d3/input/gmail.in").readText() }

fun main() {
    val (A, B) = input.lines().map(::wireMap)
    val I = A.keys intersect B.keys

    val ans1 = I.map { it.manDist(Pos2.ORIGIN) }.min()!!

    println("Part 1: $ans1")

    val ans2 = I.map { A.getValue(it) + B.getValue(it) }.min()!!
    println("Part 2: $ans2")
}

fun wire(string: String) = sequence {
    var pos = Pos2.ORIGIN
    for (ins in string.splitToSequence(",")) {
        val dir = Dir2.fromChar(ins[0])
        val reps = ins.drop(1).toInt()
        repeat(reps) {
            pos += dir
            yield(pos)
        }
    }
}

fun wireMap(string: String): Map<Pos2, Int> {
    val res = HashMap<Pos2, Int>()

    wire(string).forEachIndexed { index, pos ->
        res.putIfAbsent(pos, index + 1)
    }

    return res
}