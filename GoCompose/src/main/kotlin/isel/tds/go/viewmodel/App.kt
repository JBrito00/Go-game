package isel.tds.go.viewmodel

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.window.FrameWindowScope

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