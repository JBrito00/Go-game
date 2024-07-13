package isel.tds.go.model

@JvmInline
value class Position private constructor(val index: Int) {

    val row: Int get() = index / BOARD_SIZE

    val col: Int get() = index % BOARD_SIZE

    val backSlash: Boolean get() = row == col

    val slash: Boolean get() = row + col == BOARD_SIZE - 1

    override fun toString(): String = "${BOARD_SIZE - row}${'A' + col}"

    companion object {
        val values = List(BOARD_CELLS) { idx -> Position(idx) }

        operator fun invoke(index: Int): Position {
            require(index in 0..<BOARD_CELLS) { "out of bounds" }
            return values[index]
        }

    }
}

fun Int.toPosition(): Position = Position(this)

class Row(private val row: Int) {
    val idx = row - 1

    override fun toString() = "Row $row"

    companion object {
        private val values = Array(BOARD_SIZE) { Row(it + 1) }
        operator fun invoke(number: Int): Row {
            require(number in 1..BOARD_SIZE) { "Invalid row $number" }
            return values[number - 1]
        }
    }
}