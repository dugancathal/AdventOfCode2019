@file:Suppress("NOTHING_TO_INLINE")

package d20

import commons.*
import java.io.File
import java.util.ArrayDeque
import java.util.PriorityQueue

private val input by lazy { File("src/d20/input/gmail.in").readText() }

fun main() {
    println("--- Day 20: Donut Maze ---")

    markTime()
    val grid = input.lines()
    val h = grid.size
    val w = grid.maxBy { it.length }!!.length

    val portalRegex = Regex("[A-Z]{2}")

    val gates = StringHashMap<Vec2>()
    val warps = HashMap<Vec2, Vec2>()

    fun connectGate(label: String, pos: Vec2) {
        gates[label]?.let {
            warps[pos] = it
            warps[it] = pos
            gates.remove(label)
            true
        } ?: run {
            gates[label] = pos
        }
    }

    for(y in 0 until h) {
        val row = grid[y]

        for(match in portalRegex.findAll(row)) {
            if(grid[match.range.last+1, y] == '.') {
                connectGate(match.value, Vec2(match.range.last+1, y))
            } else {
                connectGate(match.value, Vec2(match.range.first-1, y))
            }
        }
    }

    for(x in 0 until w) {
        val col = String(CharArray(h) { grid[x, it] })

        for(match in portalRegex.findAll(col)) {
            if(grid[x, match.range.last+1] == '.') {
                connectGate(match.value, Vec2(x, match.range.last+1))
            } else {
                connectGate(match.value, Vec2(x, match.range.first-1))
            }
        }
    }

    val start = gates["AA"]!!
    val end = gates["ZZ"]!!

    val ans1 = run {
        val closed = hashSetOf(start)
        val open = ArrayDeque<BFSEntry>()
        open.add(BFSEntry(start, 0))

        while(true) {
            val (pos, cost) = open.poll() ?: break
            if(pos == end) return@run cost

            val neighbors = mutableListOf<Vec2>()
            for(dir in Dir2.values) {
                val npos = pos + dir
                if(grid[npos] == '.') neighbors.add(npos)
            }
            warps[pos]?.let { neighbors.add(it) }

            for(npos in neighbors) {
                if(closed.add(npos)) {
                    open.add(BFSEntry(npos, cost + 1))
                }
            }
        }

        -1
    }
    println("Part 1: $ans1")
    printTime()

    markTime()
    val distMap: Map<Vec2, List<SuccEntry>> = run {
        val ans = HashMap<Vec2, List<SuccEntry>>()

        for(src in sequenceOf(start) + warps.keys) {
            val successors = mutableListOf<SuccEntry>()
            val closed = hashSetOf(src)
            val open = ArrayDeque<BFSEntry>()
            open.add(BFSEntry(src, 0))

            while(true) {
                val (pos, cost) = open.poll() ?: break

                for(dir in Dir2.values) {
                    val npos = pos + dir
                    val tile = grid[npos]
                    if(tile != '.' || closed.add(npos).not()) continue
                    when {
                        npos == end -> successors.add(SuccEntry(npos, 0, cost + 1))
                        warps.containsKey(npos) -> {
                            // if gate is on outer edge, our z will decrease, otherwise it will increase
                            val dz = if ((npos.x == 2 || npos.x == w-3) || (npos.y == 2 || npos.y == h-3)) -1 else 1
                            successors.add(SuccEntry(warps[npos]!!, dz, cost + 2))
                        }
                    }
                    open.add(BFSEntry(npos, cost + 1))
                }
            }
            ans[src] = successors
        }
        ans[end] = emptyList() // treat false exits as dead ends

        ans
    }


    val ans2 = run {
        val closed = HashMap<Vec3, Int>()
        val open = PriorityQueue<Dijk<Vec3>>(11, compareBy { it.cost })
        open.add(Dijk(start.z(0), 0))
        val goal = end.z(0)

        while(true) {
            val (state, cost) = open.poll() ?: break
            if(state == goal) return@run cost
            if(closed[state].let { it != null && it < cost }) continue
            for((dest, dz, dist) in distMap[state.x, state.y]!!) {
                val nz = state.z + dz
                if(nz < 0) continue // negative z is invalid

                val nstate = dest.z(nz)
                val ncost = cost + dist

                if(closed[nstate].let { it != null && it <= ncost }) continue
                closed[nstate] = ncost
                open.add(Dijk(nstate, ncost))
            }
        }

        -1
    }
    println("Part 2: $ans2")
    printTime()
}

operator fun List<String>.get(x: Int, y: Int) = if(y in indices && x in this[y].indices) this[y][x] else ' '
operator fun List<String>.get(pos: Vec2) = this[pos.x, pos.y]

data class BFSEntry(val pos: Vec2, val cost: Int)
data class SuccEntry(val pos: Vec2, val dz: Int, val cost: Int)
data class Dijk<T>(val state: T, val cost: Int)

fun Vec2.z(z: Int) = Vec3(x, y, z)

