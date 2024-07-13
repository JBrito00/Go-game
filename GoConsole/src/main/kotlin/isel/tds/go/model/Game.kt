package isel.tds.go.model

data class Game(
    val board: Board? = null,
    val firstStone: Stone = Stone.BLACK,
    val score: Map<Stone?, Int> = (Stone.entries + null).associateWith { 0 },
    val consecutivePasses: Int = 0
)

fun Game.play(pos: Position): Game {
    checkNotNull(board) { "Game not started" }
    val b = board.play(pos)
    return copy( board = b,
        score = when (b) {
            is BoardWin -> score.advance(b.winner)
            else -> score
        },
        consecutivePasses = 0
    )
}

private fun Map<Stone?,Int>.advance(stone: Stone?) =
    this + (stone to this[stone]!! + 1)

fun Game.pass(): Game {
    checkNotNull(board) { "Game not started" }
    return if (board is BoardRun) {
        val newBoard = board.pass()
        val isPass = newBoard == board
        val newConsecutivePasses = if (!isPass) consecutivePasses + 1 else 0
        val newGame = copy(
            board = newBoard,
            score = when (newBoard) {
                is BoardWin -> score.advance(newBoard.winner)
                else -> score
            },
            consecutivePasses = newConsecutivePasses
        )
        if (newConsecutivePasses >= 2) {
            val winner = determineWinner(newBoard)
            println("Game over due to consecutive passes.")
            return Game(BoardWin(newBoard.boardCells, winner))
        } else {
            newGame
        }
    } else {
        this
    }
}

private fun determineWinner(board: Board): Stone {
    return when (board) {
        is BoardWin -> board.winner
        is BoardRun -> {
            val emptyGroups = Group.getAllEmptyGroups(board.boardCells, Position(0), emptyList())
            val territories = Group.calculateTerritories(board.boardCells, emptyGroups)
            val blackScore = board.blackCaptures + territories.first - 3.5
            val whiteScore = board.whiteCaptures + territories.second
            println("Black score: $blackScore")
            println("White score: $whiteScore")

            if (blackScore > whiteScore) Stone.BLACK
            else Stone.WHITE
        }
    }
}

fun Game.newBoard(): Game = Game(
    Board(start = firstStone),
    firstStone.other,
    if (board is BoardRun) score.advance(board.turn.other) else score
)
