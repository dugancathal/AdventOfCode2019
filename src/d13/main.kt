package d13

import commons.*
import d9.IntCodeVM
import java.io.File

private val input by lazy { File("src/d13/input/gmail.in").readText() }

fun main() {
    println("--- Day 13: Care Package ---")
    markTime()
    val prog = input.split(',').map { it.toLong() }

    val game1 = Game(prog)
    game1.start1()
    val ans1 = game1.grid.values.count { it == Game.Tile.Block }

    println("Part 1: $ans1")
    printTime()

    markTime()
    val game = Game(prog)
    game.startAuto()
    val ans2 = game.score
    println("Part 2: $ans2")
    printTime()

    // playManual(prog)
}

fun playManual(prog: List<Long>) {
    val game = Game(prog)
    game.startManual()
}

class Game(prog: List<Long>) {
    companion object {
        const val CONTROLS = "oeu"
    }

    val vm = IntCodeVM(prog)

    enum class Tile {
        Empty, Wall, Block, Paddle, Ball;
        companion object {
            val values = values().asList()
        }
    }

    val grid = HashMap<Vec2, Tile>().default(Tile.Empty)
    var score = 0L

    var ballPos = 0
    var paddlePos = 0

    fun readGrid() {
        for((x, y, id) in vm.output.chunked(3)) {
            if(x == -1L && y == 0L) score = id
            else {
                grid[x.toInt(), y.toInt()] = Tile.values[id.toInt()]
            }
        }
        vm.output.clear()
    }

    fun start1() {
        vm.execute()
        readGrid()
    }

    fun startManual() {
        vm.mem[0] = 2
        while(true) {
            vm.execute()
            readGrid()
            display()
            if(vm.isWaiting) {
                while(true) {
                    print("Input: ")
                    val ln = readLine()!!
                    if(ln.isBlank()) continue
                    val c = ln[0]
                    val i = CONTROLS.indexOf(c) - 1
                    if(i < -1) continue
                    vm.input(i)
                    break
                }
            } else break
        }
    }

    fun readGridAuto() {
        for((x, y, id) in vm.output.chunked(3)) {
            if(x == -1L && y == 0L) score = id
            else when(id.toInt()) {
                Tile.Ball.ordinal -> ballPos = x.toInt()
                Tile.Paddle.ordinal -> paddlePos = x.toInt()
            }
        }
        vm.output.clear()
    }

    fun startAuto() {
        vm.mem[0] = 2
        while(true) {
            vm.execute()
            readGridAuto()
            if(vm.isWaiting) {
                val i = ballPos.compareTo(paddlePos)
                vm.input(i)
            } else break
        }
        // display()
    }

    fun display() {
        printGrid(grid.keys.bounds()) { x, y ->
            when(grid[x, y]) {
                Tile.Empty -> ' '
                Tile.Wall -> '▓'
                Tile.Block -> 'O'
                Tile.Paddle -> '—'
                Tile.Ball -> '•'
            }
        }
        println("Score: $score")
    }
}