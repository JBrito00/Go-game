package isel.tds.go.storage

import isel.tds.go.model.Game
import isel.tds.go.model.Stone
import isel.tds.go.model.toStoneOrNull

object GameSerializer : Serializer<Game> {
    override fun serialize(data: Game) = buildString {
        appendLine( data.score.entries.joinToString(" ") { (plyr,pts) ->
            "$plyr=$pts"
        } )
        appendLine( data.firstStone )
        data.board?.let { appendLine(BoardSerializer.serialize(it)) }
    }
    override fun deserialize(text: String) =
        text.split("\n").let{ (score,player,board) -> Game(
            score = score.split(" ").map { it.split("=") }
                .associate { (plyr,pts) ->
                    plyr.toStoneOrNull() to pts.toInt()
                },
            firstStone = Stone.valueOf(player),
            board = if (board.isBlank()) null
            else BoardSerializer.deserialize(board)
        ) }
}
