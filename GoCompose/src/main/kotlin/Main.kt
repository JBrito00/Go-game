import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.window.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import isel.tds.go.model.*
import isel.tds.go.mongo.MongoDriver
import isel.tds.go.viewmodel.*
import isel.tds.go.viewmodel.AppViewModel.InputName

val CELL_SIDE = 75.dp
val GRID_THICKNESS = 5.dp
val BOARD_SIDE = CELL_SIDE * BOARD_SIZE + GRID_THICKNESS * (BOARD_SIZE - 1)

@Composable
@Preview
fun FrameWindowScope.App(driver: MongoDriver, exitFunction: () -> Unit) {
    val scope = rememberCoroutineScope()
    val vm = remember { AppViewModel(driver, scope) }

    MenuBar {
        Menu("Game") {
            Item("Start", onClick = vm::showNewGameDialog)
            Item("Join", onClick = vm::showJoinGameDialog)
            Item("Exit", onClick = { vm.exit(); exitFunction() })
        }
        Menu("Play"){
            //Item("Pass", onClick = vm::pass)
            //Item("Captures", onClick = vm::showCaptures)
            Item("Score", onClick = vm::showScore)
        }
        Menu("Options"){
            //Item("Show last",)
        }
    }
    MaterialTheme {
        boardView(
            boardCells = vm.board?.boardCells,
            board = vm.board,
            stone = vm.me,
            onClick = vm::play
        )

        if(vm.viewScore){ ScoreDialog(vm.score, vm::hideScore)}
        vm.inputName?.let{
            StartOrJoinDialog(
                type = it,
                onCancel = vm::cancelInput,
                onAction = if(it==InputName.NEW) vm::newGame else vm::joinGame
            )
        }
        //vm.errorMessage?.let { ErrorDialog(it, onClose = vm::hideError) }
        //if(vm.isWaiting) waitingIndicator()
    }
}


//@Composable
//fun numberBar(board: Board?) {
//    Column {
//        for (i in 1..BOARD_SIZE) {
//            Text(text = i.toString(), style = MaterialTheme.typography.h4)
//        }
//    }
//    Cell(board?.boardCells?.get(Position(0, 0)), size = 50.dp)
//}
//
//
//
//@Composable
//fun StatusBar(board: Board?) {
//    Row {
//        val (txt, player) = when (board) {
//            is BoardRun -> "Turn:" to board.turn
//            is BoardWin -> "Winner:" to board.winner
//            null -> "Game not started" to null
//        }
//        Text(text = txt, style = MaterialTheme.typography.h4)
//        Cell(player, size = 50.dp)
//    }
//}

/**
@Composable
fun FrameWindowScope.app(onExit: () -> Unit) {
val scope = rememberCoroutineScope()
val vm = remember { ReversiViewModel(scope) }
menuReversi(vm, onExit)
ReversiDialog(vm)
Column {
boardView(vm.game, onClick = vm::play)
statusBar(vm.game)
}
}

 */

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun StartOrJoinDialog(
    type: InputName,
    onCancel: ()->Unit,
    onAction: (String)->Unit) {

    var name by remember { mutableStateOf("") }  // Name in edition
    AlertDialog(
        onDismissRequest = onCancel,
        title = { Text(text = "Name to ${type.txt}",
            style = MaterialTheme.typography.h5
        )
        },
        text = {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Name of game") }
            )
        },
        confirmButton = {
            TextButton(enabled = true,//Name.isValid(name),
                onClick = { onAction(name)}//Name(name)) }
            ) { Text(type.txt) }
        },
        dismissButton = {
            TextButton(onClick = onCancel){ Text("cancel") }
        }
    )
}

@OptIn(ExperimentalMaterialApi::class, ExperimentalStdlibApi::class)
@Composable
fun ScoreDialog(score: Map<Stone?, Int>, closeDialog: () -> Unit) =
    AlertDialog(
        onDismissRequest = closeDialog,
        confirmButton = { TextButton(onClick=closeDialog){ Text("Close") } },
        text = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column( horizontalAlignment = Alignment.CenterHorizontally){
                    Stone.entries.forEach { stone ->
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            cellView( stone, size = 20.dp)
                            Text(
                                text = " - ${score[stone]}",
                                style = MaterialTheme.typography.h4
                            )
                        }
                    }
                }
            }
        }
    )

fun main() =
    MongoDriver("go").use { driver ->
        application {
            Window(
                onCloseRequest = ::exitApplication,
                title = "Go",
                state = WindowState(size = DpSize.Unspecified),
                resizable = false,
            ) {
                App(driver, ::exitApplication)
            }
        }
    }

