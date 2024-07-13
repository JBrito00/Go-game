package isel.tds.go.storage

import isel.tds.go.model.*
import kotlin.collections.emptyMap

object BoardSerializer: Serializer<Board> {
    override fun serialize(data: Board): String =
        when (data) {
            is BoardRun -> "run ${data.turn} ${data.whiteCaptures} ${data.blackCaptures}"
            is BoardWin -> "win ${data.winner}"
        } + " | " +
                data.boardCells.entries.joinToString(" ")
                { (pos, plyr) -> "${pos.index}:$plyr" }


    override fun deserialize(text: String): Board {

        val splittedText = text.split(" | ")

        val left = splittedText[0]
        val right = splittedText[1]
        val moves =
            if (right.isBlank()) emptyMap<Position,Stone>()
            else right.split(" ")
                .map { it.split(":") }
                .associate { (idx, plyr) ->
                    Position(idx.toInt()) to plyr.toStone()
                }
        val (type, plyr, whiteCaptures, blackCaptures) = left.split(" ")
        return when (type) {
            "run" -> BoardRun(moves, plyr.toStone(), whiteCaptures.toInt(), blackCaptures.toInt())
            "win" -> BoardWin(moves, plyr.toStone())
            else -> error("Invalid board type: $type")
        }
    }
}