package com.example.sinav_uygulamasi.ui.screens

import android.content.Intent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.sinav_uygulamasi.QuizUiState
import com.example.sinav_uygulamasi.QuizViewModel
import com.example.sinav_uygulamasi.ui.components.ActionButton
import com.example.sinav_uygulamasi.ui.components.ContentContainer
import com.example.sinav_uygulamasi.ui.design.AppColors
import com.example.sinav_uygulamasi.ui.design.Dimens

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResultScreen(s: QuizUiState, vm: QuizViewModel) {
    val cfg = LocalConfiguration.current
    val context = LocalContext.current

    val wrongs = remember(s.questions, s.answers) { vm.buildWrongReview() }
    val (score, total) = remember(s.questions, s.answers) { vm.getScorePair() }

    Scaffold(
        containerColor = Color.Transparent,
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Sonuç", style = MaterialTheme.typography.titleLarge) }
            )
        }
    ) { pad ->
        ContentContainer(
            screenWidthDp = cfg.screenWidthDp,
            modifier = Modifier
                .fillMaxSize()
                .padding(pad)
                .padding(Dimens.ScreenPadding)
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(Dimens.Gap),
                contentPadding = PaddingValues(bottom = 24.dp)
            ) {
                item {
                    ElevatedCard(
                        shape = RoundedCornerShape(Dimens.CardRadius),
                        colors = CardDefaults.elevatedCardColors(containerColor = AppColors.Card),
                        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 10.dp)
                    ) {
                        androidx.compose.foundation.layout.Column(
                            Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text("Özet", style = MaterialTheme.typography.titleLarge, color = AppColors.TextDark)
                            Text("Skor: $score/$total", style = MaterialTheme.typography.bodyLarge)
                            Text(
                                "Süre: ${s.elapsedSeconds}s",
                                style = MaterialTheme.typography.bodyMedium,
                                color = AppColors.TextMid
                            )
                        }
                    }
                }

                if (wrongs.isNotEmpty()) {
                    item {
                        Text("Yanlışlar", style = MaterialTheme.typography.titleLarge, color = AppColors.TextDark)
                    }

                    items(wrongs) { r ->
                        ElevatedCard(
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(min = 108.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.elevatedCardColors(containerColor = AppColors.Card)
                        ) {
                            androidx.compose.foundation.layout.Column(
                                modifier = Modifier.padding(14.dp),
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text(
                                    r.questionText,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    "Senin cevabın: ${r.selectedText ?: "Boş"}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = AppColors.WrongBorder,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    "Doğru cevap: ${r.correctText}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = AppColors.CorrectBorder,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }
                }

                item {
                    ActionButton(
                        text = "Tekrar Başla",
                        icon = Icons.Filled.Refresh,
                        color = AppColors.Restart,
                        onClick = { vm.restartSame() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(Dimens.ButtonHeight)
                    )
                }

                item {
                    ActionButton(
                        text = "Ana Menüye Dön",
                        icon = Icons.Filled.Home,
                        color = AppColors.Home,
                        onClick = { vm.goMenu() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(Dimens.ButtonHeight)
                    )
                }

                item {
                    ActionButton(
                        text = "Paylaş",
                        icon = Icons.Filled.Share,
                        color = AppColors.Share,
                        onClick = {
                            val text = vm.getScoreText()
                            val intent = Intent(Intent.ACTION_SEND).apply {
                                type = "text/plain"
                                putExtra(Intent.EXTRA_TEXT, text)
                            }
                            context.startActivity(Intent.createChooser(intent, "Paylaş"))
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(Dimens.ButtonHeight)
                    )
                }
            }
        }
    }
}
