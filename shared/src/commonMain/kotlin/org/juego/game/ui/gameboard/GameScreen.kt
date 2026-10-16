package org.juego.game.ui.gameboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.min
import org.juego.game.presentation.GameIntent
import org.juego.game.presentation.GameUiState
import org.juego.game.ui.PaperBackground
import org.juego.game.ui.dialog.EndGameDialog
import org.juego.game.ui.dialog.IncorrectPuzzleDialog
import org.juego.game.ui.dialog.PuzzleSolvedDialog

private const val LANDSCAPE_THRESHOLD = 680
private const val CELL_RATIO_PORTION_LANDSCAPE = 0.09f
private const val CELL_RATIO_PORTION_PORTRAIT = 0.11f
private const val CELL_WIDTH_RATIO = 0.16f


@Composable
fun GameScreen(
    uiState: GameUiState,
    onIntent: (GameIntent) -> Unit
) {
    val gameState = uiState.game
    var endGameDialog by remember { mutableStateOf(false) }

    DisposableEffect(Unit) {
        onIntent(GameIntent.SetGameScreenVisible(true))
        onDispose { onIntent(GameIntent.SetGameScreenVisible(false)) }
    }

     Column(modifier = Modifier.fillMaxSize().navigationBarsPadding()) {
         BoxWithConstraints(modifier = Modifier.fillMaxSize().background(PaperBackground)) {
             val landscape = maxWidth > maxHeight && maxWidth > LANDSCAPE_THRESHOLD.dp
             val cellSize  = getCellSize(maxHeight, maxWidth, landscape)

             if (landscape) {
                 GameScreenLandscape(uiState, gameState, cellSize, onIntent,
                     showDialog = {
                         endGameDialog = true
                     }
                 )
             } else {
                 GameScreenPortrait(
                     uiState, gameState, cellSize, onIntent,
                     showDialog = {
                        endGameDialog = true
                    }
                 )
             }

             IncorrectPuzzleDialog(
                 modifier = Modifier.align(Alignment.Center),
                 visible = uiState.game.showingErrors,
                 onDismiss = {
                     onIntent(GameIntent.DismissResultDialog)
                 }
             )

             PuzzleSolvedDialog(
                 modifier = Modifier.align(Alignment.Center),
                 visible = gameState.isSolved && !uiState.hasShownSuccessDialog,
                 elapsedSeconds = uiState.elapsedSeconds,
                 onNewGame = { onIntent(GameIntent.NewGame(gameState.puzzle.difficulty)) },
                 onDismiss = {
                     onIntent(GameIntent.DismissSuccessDialog)
                 }
             )

             EndGameDialog(
                 modifier = Modifier.align(Alignment.Center),
                 visible = endGameDialog,
                 onEndGame = {
                     endGameDialog = false
                     onIntent(GameIntent.EndGame)
                 },
                 onDismiss = { endGameDialog = false }
             )
         }
     }
}

private fun getCellSize(maxHeight: Dp, maxWidth: Dp, landscape: Boolean): Dp {
    return if (landscape) {
        min(
            min(maxHeight * CELL_WIDTH_RATIO, maxWidth * CELL_RATIO_PORTION_LANDSCAPE),
            72.dp
        )
    } else {
        min(
            min(maxWidth * CELL_WIDTH_RATIO, maxHeight * CELL_RATIO_PORTION_PORTRAIT),
            72.dp
        )
    }
}