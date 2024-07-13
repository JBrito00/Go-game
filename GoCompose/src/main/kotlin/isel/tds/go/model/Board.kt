package isel.tds.go.model

const val BOARD_SIZE = 9
const val BOARD_CELLS: Int = BOARD_SIZE * BOARD_SIZE

typealias BoardCells = Map<Position, Stone>


sealed class Board(val boardCells: BoardCells) {
    override fun equals(other: Any?): Boolean {
        return other is Board && boardCells == other.boardCells
    }
}

class BoardWin(boardCells: BoardCells, val winner: Stone) : Board(boardCells) {
    val winningColor: Stone = winner
}

fun Board(start: Stone = Stone.BLACK): Board = BoardRun(emptyMap(), start)

fun Board.play(playPositionIdx: Position): Board = when (this) {
    is BoardRun -> {
        require(boardCells[playPositionIdx] == null) { "Position ${playPositionIdx.toString()} used" }
        val boardCellsAfterPlay = boardCells + (playPositionIdx to turn)
        val group = Group.getGroup(playPositionIdx, turn, boardCellsAfterPlay)
        val selfGroup = Group.getGroup(playPositionIdx, turn.other, boardCellsAfterPlay)
        val opponentGroup = group.filter { it != playPositionIdx }
        val boardAfterOpponentEat = Group.eatGroup(boardCellsAfterPlay, turn.other, opponentGroup)
        val boardAfterSelfEat = Group.eatGroup(boardCellsAfterPlay, turn, selfGroup)

        val captures = Group.capturedPieces(boardCellsAfterPlay, boardAfterOpponentEat, boardAfterSelfEat, turn)
        val newWhiteCaptures = whiteCaptures + captures.first
        val newBlackCaptures = blackCaptures + captures.second

        val emptyGroups = Group.getAllEmptyGroups(boardCellsAfterPlay, Position(0), emptyList())
        val territories = Group.calculateTerritories(boardCellsAfterPlay, emptyGroups)

        val blackScore = newBlackCaptures + territories.first - 3.5
        val whiteScore = newWhiteCaptures + territories.second
        val highestScore = maxOf(blackScore.toFloat(), whiteScore.toFloat())
        val color: Stone = if(highestScore == blackScore.toFloat()) Stone.BLACK else Stone.WHITE

        when {
            boardAfterOpponentEat == boardCellsAfterPlay -> BoardRun(boardAfterSelfEat, turn.other, newWhiteCaptures, newBlackCaptures)
            else ->  BoardRun(boardAfterOpponentEat, turn.other, newWhiteCaptures, newBlackCaptures)
        }
    }

    is BoardWin -> error("Game Over")
}

fun Board.pass(): Board = when (this) {
    is BoardRun -> BoardRun(boardCells, turn.other, whiteCaptures, blackCaptures)
    is BoardWin -> BoardWin(boardCells, winner)
}
