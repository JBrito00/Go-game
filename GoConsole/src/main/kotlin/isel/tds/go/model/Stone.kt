package isel.tds.go.model

enum class Stone(val symbol: Char) {
    WHITE('0'), BLACK('#');
    val other get() = if( this == WHITE) BLACK else WHITE
}

fun String.toStoneOrNull() = Stone.entries.firstOrNull {it.name==this}
fun String.toStone() = Stone.valueOf(this )