package com.example.sinav_uygulamasi.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.dp
import com.example.sinav_uygulamasi.QuizUiState
import com.example.sinav_uygulamasi.QuizViewModel
import com.example.sinav_uygulamasi.ui.components.AnswerOptionItem
import com.example.sinav_uygulamasi.ui.components.QuestionCard
import com.example.sinav_uygulamasi.ui.design.AppColors
import com.example.sinav_uygulamasi.ui.design.Dimens

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuizScreen(s: QuizUiState, vm: QuizViewModel) {

    val cfg = LocalConfiguration.current
    val screenWidthDp = cfg.screenWidthDp
    val screenHeightDp = cfg.screenHeightDp
    val isLandscape = screenWidthDp > screenHeightDp

    // ✅ responsive max width
    val contentMaxWidth = when {
        screenWidthDp >= 1000 -> 1100.dp
        screenWidthDp >= 840 -> 840.dp    // tablet landscape
        screenWidthDp >= 600 -> 720.dp    // tablet portrait
        else -> 640.dp                    // telefon
    }

    // ✅ Landscape: köşeye yasla, Portrait: üst-orta
    val listAlign = if (isLandscape) Alignment.TopStart else Alignment.TopCenter

    val haptic = LocalHapticFeedback.current

    val q = s.questions[s.currentIndex]
    val selected = s.answers[s.currentIndex]

    Scaffold(
        containerColor = androidx.compose.ui.graphics.Color.Transparent,
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(s.selectedType.title, style = MaterialTheme.typography.titleLarge) },
                navigationIcon = {
                    IconButton(onClick = { vm.goMenu() }) { Icon(Icons.Filled.Close, null) }
                },
                actions = {
                    AssistChip(
                        onClick = {},
                        label = { Text("${s.elapsedSeconds}s", style = MaterialTheme.typography.labelLarge) },
                        leadingIcon = { Icon(Icons.Filled.AccessTime, null) },
                        modifier = Modifier.padding(end = 12.dp)
                    )
                }
            )
        }
    ) { pad ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(pad)
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxHeight()
                    .align(listAlign)                 // ✅ değişti
                    .padding(Dimens.ScreenPadding)
                    .fillMaxWidth()                   // ✅ eklendi: landscape’te kenara yaslı his
                    .widthIn(max = contentMaxWidth),  // ✅ responsive
                verticalArrangement = Arrangement.spacedBy(Dimens.Gap),
                contentPadding = PaddingValues(bottom = 24.dp)
            ) {

                item {
                    LinearProgressIndicator(
                        progress = { (s.currentIndex + 1f) / s.questions.size.toFloat() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(99.dp)),
                        color = AppColors.Orange
                    )
                }

                item {
                    Text(
                        "Soru ${s.currentIndex + 1}/${s.questions.size}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = AppColors.TextMid
                    )
                }

                item {
                    QuestionCard(
                        question = q.text,
                        explanation = q.explanation,
                        showExplanation = s.isAnswered
                    )
                }

                itemsIndexed(q.options) { idx, opt ->
                    val isSel = (selected == idx)
                    AnswerOptionItem(
                        label = ('A' + idx).toString(),
                        text = opt.text,
                        isSelected = isSel,
                        isCorrect = opt.isCorrect,
                        isAnswered = s.isAnswered,
                        onClick = {
                            vm.select(idx)
                            haptic.performHapticFeedback(
                                if (opt.isCorrect) HapticFeedbackType.LongPress
                                else HapticFeedbackType.TextHandleMove
                            )
                        }
                    )
                }

                item {
                    Row(
                        Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedButton(
                            onClick = { vm.prev() },
                            enabled = (s.currentIndex > 0),
                            modifier = Modifier
                                .weight(1f)
                                .height(Dimens.ButtonHeight),
                            shape = RoundedCornerShape(Dimens.ButtonRadius)
                        ) {
                            Text("Önceki", style = MaterialTheme.typography.titleMedium)
                        }

                        Button(
                            onClick = { vm.next() },
                            enabled = (s.answers[s.currentIndex] != null),
                            modifier = Modifier
                                .weight(1f)
                                .height(Dimens.ButtonHeight),
                            shape = RoundedCornerShape(Dimens.ButtonRadius),
                            colors = ButtonDefaults.buttonColors(containerColor = AppColors.Orange)
                        ) {
                            Text(
                                if (s.currentIndex == s.questions.lastIndex) "Bitir" else "Sonraki",
                                style = MaterialTheme.typography.titleMedium,
                                color = androidx.compose.ui.graphics.Color.White
                            )
                        }
                    }
                }
            }
        }
    }
}
