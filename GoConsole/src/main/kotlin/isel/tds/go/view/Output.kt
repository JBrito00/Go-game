package isel.tds.go.view

import isel.tds.go.model.*

fun Board.show() {
    val maxDigits = BOARD_SIZE.toString().length
    print(" ".repeat(maxDigits + 1))
    ('A'..<'A' + BOARD_SIZE).forEach { print(" $it ") }
    println()

    Position.values.forEach { pos ->
        if (pos.col < 1) print((BOARD_SIZE - pos.row).toString())
        if (pos.col == 0) print(" ")
        print(" ${boardCells[pos]?.symbol ?: '.'} ")
        if (pos.col == BOARD_SIZE - 1) println()
    }
    println(
        when (this) {
            is BoardWin -> "Winner: ${winner.name}"
            is BoardRun -> "Turn: ${turn.symbol} ($turn)   Captures: #:${blackCaptures} O:${whiteCaptures})"
        }
    )
}

fun Game.show() = board?.show()

fun Game.showScore() {
    print("Score:")
    score.forEach { (player, value) ->
        print(" ${player ?: "Draws"}=$value ")
    }
    println()
}
