package isel.tds.go.model

class BoardRun(boardCells: BoardCells, val turn: Stone, val whiteCaptures: Int, val blackCaptures: Int) : Board(boardCells) {
    constructor(boardCells: BoardCells, turn: Stone) : this(boardCells, turn, 0, 0)

    override fun equals(other: Any?): Boolean {
        return super.equals(other) && other is BoardRun && turn == other.turn &&
                whiteCaptures == other.whiteCaptures && blackCaptures == other.blackCaptures
    }
}

