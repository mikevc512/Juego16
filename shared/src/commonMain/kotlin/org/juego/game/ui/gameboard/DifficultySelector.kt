package org.juego.game.ui.gameboard

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import kotlinproject.shared.generated.resources.Res
import kotlinproject.shared.generated.resources.app_name
import kotlinproject.shared.generated.resources.juego16_header
import kotlinproject.shared.generated.resources.tiles_given
import org.juego.game.game.Difficulty
import org.juego.game.presentation.GameIntent
import org.juego.game.ui.PaperBackground
import org.juego.game.ui.localizedName
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun DifficultySelector(
    onIntent: (GameIntent) -> Unit
) {
    LazyColumn(
        modifier = Modifier .fillMaxSize()
            .background(PaperBackground)
            .padding(horizontal = 20.dp, vertical = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        item {
            Image(
                painter = painterResource(Res.drawable.juego16_header),
                contentDescription = stringResource(Res.string.app_name),
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 100.dp),
                contentScale = ContentScale.Fit,
            )
        }

        Difficulty.entries.forEach { difficulty ->
            item {
                Card(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    shape = RoundedCornerShape(16.dp),
                    onClick = { onIntent(GameIntent.NewGame(difficulty = difficulty)) }
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = difficulty.localizedName(),
                            style = MaterialTheme.typography.titleLargeEmphasized,
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = stringResource(Res.string.tiles_given, difficulty.givenCount),
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}