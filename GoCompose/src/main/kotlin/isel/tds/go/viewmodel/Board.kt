package isel.tds.go.viewmodel

import androidx.compose.foundation.Image
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.*
import isel.tds.go.model.*

const val BOARD_DIM = 9
val cellSize = 54.dp
val lineSize = 4.dp
val boardInformationSize = cellSize / 3
val boardSize = cellSize * BOARD_DIM + lineSize * BOARD_DIM + boardInformationSize
val GRID_THICKNESS = 3.dp


/**
 * Draws the board
 */
@Composable
fun boardView(boardCells: BoardCells?,board: Board?, stone: Stone?, onClick: (Position) -> Unit) {
    background()
    drawGrid()
    Column(
        modifier = Modifier.size(boardSize),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        collView()
        boardRowDivision()
        playZoneView(boardCells, onClick = onClick)
        //StatusBar(board, stone)
    }
    StatusBar(board, stone)
}


@Composable
fun background() {
    Image(
        painter = painterResource("board.png"),
        contentDescription = "background",
        alignment = Alignment.TopStart
    )
}

@Composable
fun drawGrid1() {
    Column(
        modifier = Modifier
            .width(boardSize)
            .background(Color.Transparent)
    ) {
        repeat(BOARD_DIM) { row ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(cellSize)
            ) {
                repeat(BOARD_DIM) { col ->
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .background(Color.Transparent)
                    )
                    if (col < BOARD_DIM - 1) {
                        Spacer(
                            modifier = Modifier
                                .width(GRID_THICKNESS)
                                .fillMaxHeight()
                                .background(Color.Black)
                        )
                    }
                }
            }
            if (row < BOARD_DIM - 1) {
                Spacer(
                    modifier = Modifier
                        .width(boardSize)
                        .height(GRID_THICKNESS)
                        .background(Color.Black)
                )
            }
        }
    }
}

/**
 * Draws a black grid on the board
 */
@Composable
fun drawGrid() {
    Canvas(
        modifier = Modifier
            .size(cellSize - lineSize)
            .border(width = lineSize, color = Color.Transparent)
    ) {
        repeat(BOARD_DIM - 1) { row ->
            repeat(BOARD_DIM - 1) { col ->
                val pos = Position(row, col)

                drawRect(
                    color = Color.DarkGray,
                    size = Size(width = cellSize.toPx(), height = cellSize.toPx()),
                    topLeft = Offset(
                        x = (((row * 54).dp + cellSize) - lineSize + row * lineSize ).toPx(),
                        y = (((col * 54).dp + cellSize) - lineSize + col * lineSize).toPx()
                    ),
                    style = Stroke(width = 4.dp.toPx())
                )

            }
        }
    }
}


@Composable
fun collView() {
    Row {
        Spacer(modifier = Modifier.size(boardInformationSize + lineSize / 2, boardInformationSize))
        repeat(BOARD_DIM) {
            Box(modifier = Modifier.size(cellSize, boardInformationSize)) {
                Text(
                    ('A' + it).toString(),
                    modifier = Modifier.align(Alignment.Center),
                    color = Color.Black
                )
            }
            if (it == BOARD_DIM - 1) Spacer(modifier = Modifier.size(lineSize / 2))
            else Spacer(modifier = Modifier.size(lineSize))
        }
    }

}

@Composable
fun boardRowDivision() {
    Row(
        modifier = Modifier.size(boardSize, lineSize / 2).fillMaxSize()
    ) {
        Box(modifier = Modifier.size(boardInformationSize).background(Color.DarkGray)) {}
    }
}

@Composable
fun cellView(
    stone: Stone?,
    size: Dp = cellSize,
    onClick: () -> Unit = {}
) {
    val modifier = Modifier.size(size)
    if (stone == null) {
        Box(modifier.clickable(onClick = onClick))
    } else {
        Box(
            modifier = modifier,
            contentAlignment = Alignment.Center
        ) {
            val filename = when (stone) {
                Stone.BLACK -> "blackStone.png"
                Stone.WHITE -> "whiteStone.png"
            }
            Image(
                painter = painterResource(filename),
                contentDescription = "Stone $stone",
                modifier = modifier
            )
        }
    }
}

@Composable
fun rowView(row: Int) {
    Box(modifier = Modifier.size(boardInformationSize, cellSize + lineSize)) {
        Text(row.toString(), Modifier.align(Alignment.Center), color = Color.Black)
    }
    Spacer(modifier = Modifier.size(lineSize / 2, cellSize))
}


@Composable
fun playZoneView(boardCells: BoardCells?, onClick: (Position) -> Unit) {
    repeat(BOARD_DIM) { row ->
        //var mod = Modifier.fillMaxWidth()
        val mod = if (row < BOARD_DIM) Modifier.fillMaxWidth() else Modifier.fillMaxWidth().height(cellSize)
        Row(
            modifier = mod,
        ) {

            repeat(BOARD_DIM) { col ->
                if (col == 0) rowView(BOARD_DIM - 1 - row + 1)
                val pos = Position(row, col)
                cellView(
                    boardCells?.get(pos),
                    onClick = { onClick(pos) }
                )
                if (col == BOARD_DIM - 1) Spacer(modifier = Modifier.size(lineSize / 2))
                else Spacer(modifier = Modifier.size(lineSize))
            }
        }
    }
}

@Composable
fun StatusBar(board: Board?, me: Stone?) =
    Row {
        me?.let{
            Text("You ", style = MaterialTheme.typography.h4)
            cellView(stone = it, size=50.dp)
            Spacer(Modifier.width(30.dp))
        }
        val (txt, stone) = when(board){
            is BoardRun -> "Turn:" to board.turn
            is BoardWin -> "Winner:" to board.winner
            //is BoardDraw -> "Draw" to null
            null -> "Game not started" to null
        }
        Text(text=txt, style= MaterialTheme.typography.h4 )
        cellView(stone, size = 50.dp)
    }