package com.example.sinav_uygulamasi.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import com.example.sinav_uygulamasi.QuizType
import com.example.sinav_uygulamasi.QuizUiState
import com.example.sinav_uygulamasi.QuizViewModel
import com.example.sinav_uygulamasi.ui.components.ContentContainer
import com.example.sinav_uygulamasi.ui.components.ExamHistoryCard
import com.example.sinav_uygulamasi.ui.components.QuizTypeCard
import com.example.sinav_uygulamasi.ui.components.ScorePill
import com.example.sinav_uygulamasi.ui.design.AppColors
import com.example.sinav_uygulamasi.ui.design.Dimens

private fun historyBadge(entry: String): String {
    val type = entry.substringAfter("Tür:", "").substringBefore("|").trim()

    return when {
        type.contains("Kotlin", ignoreCase = true) -> "KT"
        type.contains("Jetpack", ignoreCase = true) || type.contains("Compose", ignoreCase = true) -> "JC"
        type.contains("Karışık", ignoreCase = true) || type.contains("Karisik", ignoreCase = true) -> "K"
        else -> "S"
    }
}


private fun iconBg(type: QuizType): Color = when (type) {
    QuizType.KOTLIN -> AppColors.Kotlin
    QuizType.COMPOSE -> AppColors.Compose
    QuizType.KARISIK -> AppColors.Mixed
}

private fun historyTitle(entry: String): String {
    val type = entry.substringAfter("Tür:", "").substringBefore("|").trim()
    return if (type.isNotBlank()) type else "Sınav"
}

private fun historySubtitle(entry: String): String {
    val skor = entry.substringAfter("Skor:", "").substringBefore("|").trim()
    val sure = entry.substringAfter("Süre:", "").trim()

    val s1 = if (skor.isNotBlank()) "Skor: $skor" else ""
    val s2 = if (sure.isNotBlank()) "Süre: $sure" else ""

    return listOf(s1, s2).filter { it.isNotBlank() }
        .joinToString(" • ")
        .ifBlank { entry }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MenuScreen(s: QuizUiState, vm: QuizViewModel) {
    val cfg = LocalConfiguration.current
    var showClearDialog by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = Color.Transparent,
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Ana Menü", style = MaterialTheme.typography.titleLarge) },
                actions = {
                    IconButton(onClick = { vm.goSettings() }) {
                        Icon(Icons.Filled.Settings, contentDescription = null)
                    }
                }
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
                        shape = RoundedCornerShape(Dimens.BigRadius),
                        colors = CardDefaults.elevatedCardColors(containerColor = AppColors.Card)
                    ) {
                        androidx.compose.foundation.layout.Column(
                            Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Text("Skorlar", style = MaterialTheme.typography.titleLarge, color = AppColors.TextDark)
                            Row(
                                Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                ScorePill("Kotlin", s.bestKotlin, AppColors.Kotlin, Modifier.weight(1f))
                                ScorePill("Compose", s.bestCompose, AppColors.Compose, Modifier.weight(1f))
                                ScorePill("Karışık", s.bestMixed, AppColors.Mixed, Modifier.weight(1f))
                            }
                        }
                    }
                }

                item {
                    Text("Sınav Çeşitleri", style = MaterialTheme.typography.titleLarge, color = AppColors.TextMenuTitle)
                }

                items(QuizType.entries) { type ->
                    val best = when (type) {
                        QuizType.KOTLIN -> s.bestKotlin
                        QuizType.COMPOSE -> s.bestCompose
                        QuizType.KARISIK -> s.bestMixed
                    }

                    QuizTypeCard(
                        title = type.title,
                        bestScore = best,
                        badgeColor = iconBg(type),
                        onStart = {
                            vm.updateType(type)
                            vm.start()
                        }
                    )
                }

                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "Sınav Geçmişi",
                            style = MaterialTheme.typography.titleLarge,
                            color = AppColors.TextDark,
                            modifier = Modifier.weight(1f)
                        )
                        TextButton(
                            onClick = { showClearDialog = true },
                            enabled = s.examHistory.isNotEmpty()
                        ) { Text("Temizle") }
                    }
                }

                val history = s.examHistory.reversed()
                if (history.isEmpty()) {
                    item {
                        ElevatedCard(
                            shape = RoundedCornerShape(Dimens.CardRadius),
                            colors = CardDefaults.elevatedCardColors(containerColor = AppColors.CardWarm)
                        ) {
                            Text(
                                "Henüz sınav yapılmadı.",
                                modifier = Modifier.padding(14.dp),
                                style = MaterialTheme.typography.bodyMedium,
                                color = AppColors.TextMid
                            )
                        }
                    }
                } else {
                    items(history) { entry ->
                        ExamHistoryCard(
                            badgeText = historyBadge(entry),
                            title = historyTitle(entry),
                            subtitle = historySubtitle(entry)
                        )
                    }

                }
            }

            if (showClearDialog) {
                AlertDialog(
                    onDismissRequest = { showClearDialog = false },
                    title = { Text("Sınav geçmişi temizlensin mi?") },
                    text = { Text("Bu işlem geri alınamaz.") },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                vm.clearExamHistory()
                                showClearDialog = false
                            }
                        ) { Text("Evet, temizle") }
                    },
                    dismissButton = {
                        TextButton(onClick = { showClearDialog = false }) { Text("Vazgeç") }
                    }
                )
            }
        }
    }
}
