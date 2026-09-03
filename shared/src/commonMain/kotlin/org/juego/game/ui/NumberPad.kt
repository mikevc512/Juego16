package org.juego.game.ui

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.juego.game.game.CELL_COUNT

@Composable
fun NumberPad(
    usedNumbers: Set<Int>,
    enabled: Boolean,
    onNumber: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        listOf(1..8, 9..CELL_COUNT).forEach { range ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                range.forEach { number ->
                    val used = number in usedNumbers
                    NumberKey(
                        number = number,
                        used = used,
                        enabled = enabled,
                        onClick = { onNumber(number) },
                        modifier = Modifier.weight(1f),
                    )
                }
            }
        }
    }
}

@Composable
private fun NumberKey(
    number: Int,
    used: Boolean,
    enabled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val targetBackground = when {
        !enabled -> GivenCell.copy(alpha = 0.55f)
        used -> TealSoft
        else -> BoardCream
    }
    val background by animateColorAsState(targetBackground)
    val textColor = when {
        !enabled -> MutedInk
        used -> Teal.copy(alpha = 0.55f)
        else -> Ink
    }

    Box(
        modifier = modifier
            .height(48.dp)
            //.shadow(2.dp, RoundedCornerShape(10.dp))
            .clip(RoundedCornerShape(10.dp))
            .background(background)
            .border(1.dp, GridLine.copy(alpha = 0.35f), RoundedCornerShape(10.dp))
            .clickable(enabled = enabled, onClick = onClick)
            .padding(vertical = 8.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = number.toString(),
            color = textColor,
            fontWeight = FontWeight.SemiBold,
            fontFamily = FontFamily.Serif,
            fontSize = 18.sp,
        )
    }
}
