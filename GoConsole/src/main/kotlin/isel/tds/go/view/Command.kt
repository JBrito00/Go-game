package isel.tds.go.view


import isel.tds.go.model.*
import isel.tds.go.storage.TextFileStorage

abstract class Command(val argSyntax: String = "") {
    open fun execute(args: List<String>, game: Game): Game = throw IllegalStateException("GameOver")

    open val isToFinish = false
}

object Play : Command("pos") {
    override fun execute(args: List<String>, game: Game): Game {
        checkNotNull(game.board) { "Game not started" }

        val arg = requireNotNull(args.firstOrNull()) { "Missing index" }
        val posNumber = requireNotNull(arg[0].digitToIntOrNull()) { "Invalid row$arg" }
        val posLetter = requireNotNull(arg[1].toUpperCase().toInt()) { "Invalid column $arg" }
        val pos = ((BOARD_SIZE - posNumber) * BOARD_SIZE) + posLetter - 'A'.toInt()
        println(BOARD_SIZE - posNumber)
        println(pos)
        if (pos !in 0..BOARD_CELLS) throw IllegalArgumentException(
            "Invalid index $arg"
        )
        println(pos.toPosition())

        return game.play(pos.toPosition())
    }

}

object Pass : Command() {
    override fun execute(args: List<String>, game: Game): Game {
        checkNotNull(game.board) { "Game not started" }
        return game.pass()
    }
}

fun getCommands(storage: TextFileStorage<String, Game>): Map<String, Command> {
    return mapOf<String, Command>(
        "PLAY" to Play,
        "PASS" to Pass,
        "NEW" to object : Command() {
            override fun execute(args: List<String>, game: Game): Game = game.newBoard()
        },
        "EXIT" to object : Command() {
            override val isToFinish = true
        },
        "SCORE" to object : Command() {
            override fun execute(args: List<String>, game: Game): Game =
                game.also { it.showScore() }
        },
        "SAVE" to object : Command() {
            override fun execute(args: List<String>, game: Game): Game {
                require(args.isNotEmpty()) { "Missing name" }
                requireNotNull(game.board) { "Game not started" }
                val name = args[0]
                require(name.isNotEmpty()) { "Name must not be empty" }
                storage.create(name, game)
                return game
            }
        },
        "LOAD" to object : Command() {
            override fun execute(args: List<String>, game: Game): Game {
                val name = requireNotNull(args.firstOrNull()) { "Missing name" }
                return checkNotNull(storage.read(name)) { "Game $name not found" }
            }
        }
    )
}