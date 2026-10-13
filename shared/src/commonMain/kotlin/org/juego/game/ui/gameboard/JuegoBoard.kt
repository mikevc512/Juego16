package org.juego.game.ui.gameboard

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.juego.game.game.BOARD_SIZE
import org.juego.game.game.GameState
import org.juego.game.game.LineState
import org.juego.game.ui.BoardCream
import org.juego.game.ui.Coral
import org.juego.game.ui.CorrectGreen
import org.juego.game.ui.ErrorRed
import org.juego.game.ui.GivenCell
import org.juego.game.ui.GridLine
import org.juego.game.ui.Ink
import org.juego.game.ui.SelectedCell
import org.juego.game.ui.Teal

@Composable
fun JuegoBoard(
    state: GameState,
    cellSize: Dp,
    onCellClick: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val analysis = state.analysis
    val sumSize = cellSize * 0.78f

    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Spacer(Modifier.size(cellSize))
            repeat(BOARD_SIZE) { col ->
                Box(Modifier.size(cellSize), contentAlignment = Alignment.Center) {
                    SumCircle(
                        value = state.puzzle.colSums[col],
                        lineState = analysis.colStates[col],
                        size = sumSize,
                    )
                }
            }
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column {
                repeat(BOARD_SIZE) { row ->
                    Box(Modifier.size(cellSize), contentAlignment = Alignment.Center) {
                        SumCircle(
                            value = state.puzzle.rowSums[row],
                            lineState = analysis.rowStates[row],
                            size = sumSize,
                        )
                    }
                }
            }
            BoardGrid(
                state = state,
                analysisDuplicates = analysis.duplicateIndices,
                cellSize = cellSize,
                onCellClick = onCellClick,
            )
        }
    }
}

@Composable
private fun BoardGrid(
    state: GameState,
    analysisDuplicates: Set<Int>,
    cellSize: Dp,
    onCellClick: (Int) -> Unit,
) {
    Column(
        modifier = Modifier
            .shadow(6.dp, RoundedCornerShape(10.dp))
            .clip(RoundedCornerShape(10.dp))
            .border(3.dp, GridLine, RoundedCornerShape(10.dp))
            .background(BoardCream),
    ) {
        repeat(BOARD_SIZE) { row ->
            Row {
                repeat(BOARD_SIZE) { col ->
                    val index = row * BOARD_SIZE + col
                    PuzzleCell(
                        value = state.entries[index],
                        given = state.puzzle.given[index],
                        selected = state.selectedIndex == index,
                        duplicate = index in analysisDuplicates,
                        showError = state.showingErrors && index in analysisDuplicates,
                        cellSize = cellSize,
                        onClick = { onCellClick(index) },
                    )
                }
            }
        }
    }
}

@Composable
private fun PuzzleCell(
    value: Int,
    given: Boolean,
    selected: Boolean,
    duplicate: Boolean,
    showError: Boolean,
    cellSize: Dp,
    onClick: () -> Unit,
) {
    val targetBackground = when {
        selected -> SelectedCell
        given -> GivenCell
        else -> BoardCream
    }
    val background by animateColorAsState(targetBackground)
    val textColor = when {
        showError || duplicate -> ErrorRed
        given -> Ink
        else -> Teal
    }

    Box(
        modifier = Modifier
            .size(cellSize)
            .border(1.dp, GridLine.copy(alpha = 0.55f))
            .background(background)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        if (value != 0) {
            Text(
                text = value.toString(),
                color = textColor,
                fontSize = (cellSize.value * 0.38f).sp,
                fontWeight = if (given) FontWeight.Bold else FontWeight.SemiBold,
                fontFamily = FontFamily.Serif,
            )
        }
    }
}

@Composable
private fun SumCircle(
    value: Int,
    lineState: LineState,
    size: Dp,
) {
    val fill = when (lineState) {
        LineState.Correct -> CorrectGreen
        LineState.Incorrect -> ErrorRed
        LineState.Incomplete -> Coral
    }
    val animatedFill by animateColorAsState(fill)

    Box(
        modifier = Modifier
            .size(size)
            .shadow(3.dp, CircleShape)
            .clip(CircleShape)
            .background(animatedFill)
            .border(2.dp, Color.White.copy(alpha = 0.55f), CircleShape),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = value.toString(),
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Serif,
            fontSize = (size.value * 0.34f).sp,
        )
    }
}