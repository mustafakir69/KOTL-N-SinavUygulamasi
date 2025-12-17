package com.example.sinav_uygulamasi

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppTheme {
                AppRoot()
            }
        }
    }
}

/** Uygulamanın TEK tipografi standardı (her yerde aynı, net ve büyük) */
@Composable
private fun AppTheme(content: @Composable () -> Unit) {
    val appTypography = Typography(
        headlineLarge = TextStyle(fontSize = 30.sp, fontWeight = FontWeight.Bold, lineHeight = 36.sp),
        headlineMedium = TextStyle(fontSize = 24.sp, fontWeight = FontWeight.Bold, lineHeight = 30.sp),
        titleLarge = TextStyle(fontSize = 20.sp, fontWeight = FontWeight.Bold, lineHeight = 26.sp),
        titleMedium = TextStyle(fontSize = 18.sp, fontWeight = FontWeight.SemiBold, lineHeight = 24.sp),
        bodyLarge = TextStyle(fontSize = 18.sp, fontWeight = FontWeight.Normal, lineHeight = 24.sp),
        bodyMedium = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.Normal, lineHeight = 22.sp),
        labelLarge = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.SemiBold, lineHeight = 18.sp)
    )

    MaterialTheme(
        typography = appTypography,
        content = content
    )
}

@Composable
fun AppRoot(vm: QuizViewModel = viewModel()) {
    val state by vm.uiState.collectAsState()

    val bg = Brush.verticalGradient(
        listOf(
            MaterialTheme.colorScheme.primary.copy(alpha = 0.16f),
            MaterialTheme.colorScheme.background
        )
    )

    // Süre sayacı sadece quiz ekranında aksın
    LaunchedEffect(state.screen) {
        while (state.screen == "QUIZ") {
            delay(1000)
            vm.tick()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(bg)
    ) {
        when (state.screen) {
            "MENU" -> MenuScreen(
                onStart = { vm.startQuiz(it) }
            )

            "QUIZ" -> {
                val type = state.quizType?.let { QuizType.valueOf(it) } ?: QuizType.KOTLIN
                val questions = vm.getQuestionsFor(type)
                QuizScreenModern(
                    title = type.title,
                    index = state.currentIndex,
                    total = questions.size,
                    seconds = state.elapsedSeconds,
                    question = questions[state.currentIndex],
                    selectedIndex = state.selectedIndex,
                    isAnswered = state.isAnswered,
                    isCorrect = state.isCorrect,
                    correctIndex = questions[state.currentIndex].correctIndex,
                    onSelect = vm::selectOption,
                    onNext = vm::next
                )
            }

            "RESULT" -> {
                val type = state.quizType?.let { QuizType.valueOf(it) } ?: QuizType.KOTLIN
                val total = vm.getQuestionsFor(type).size
                ResultScreenModern(
                    score = state.score,
                    total = total,
                    seconds = state.elapsedSeconds,
                    onRestart = vm::restartSameQuiz,
                    onMenu = vm::goMenu
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MenuScreen(onStart: (QuizType) -> Unit) {
    Scaffold(
        containerColor = Color.Transparent,
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Ana Menü", style = MaterialTheme.typography.titleLarge) }
            )
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .padding(padding)
                .padding(horizontal = 18.dp, vertical = 14.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {

            // ÜST TANITIM KARTI (çok daha şık, yazılar belirgin)
            ElevatedCard(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.elevatedCardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Sınav Uygulaması", style = MaterialTheme.typography.headlineMedium)
                    Text(
                        "Bir sınav türü seç. Bitirince sonuç ekranından tekrar başlatabilir veya ana menüye dönebilirsin.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.85f)
                    )
                }
            }

            // “Sınav çeşitleri” başlık satırı
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Sınav Çeşitleri", style = MaterialTheme.typography.titleLarge)
                Spacer(Modifier.weight(1f))
                AssistChip(
                    onClick = {},
                    label = { Text("Başlamak için seç", style = MaterialTheme.typography.labelLarge) },
                    leadingIcon = { Icon(Icons.Filled.Quiz, null) }
                )
            }

            // MENÜ KARTLARI (büyük yazı + net CTA)
            QuizType.entries.forEach { type ->
                ElevatedCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onStart(type) },
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.elevatedCardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
                            shape = RoundedCornerShape(14.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Quiz,
                                contentDescription = null,
                                modifier = Modifier.padding(12.dp)
                            )
                        }

                        Spacer(Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(type.title, style = MaterialTheme.typography.titleLarge)
                            Text(
                                "3 soru • Süre sayacı açık • Doğru/yanlış renkli",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.75f)
                            )
                        }

                        Spacer(Modifier.width(8.dp))

                        FilledTonalButton(
                            onClick = { onStart(type) },
                            shape = RoundedCornerShape(14.dp),
                            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 10.dp)
                        ) {
                            Text("Başla", style = MaterialTheme.typography.titleMedium)
                        }
                    }
                }
            }

            Spacer(Modifier.height(6.dp))


        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun QuizScreenModern(
    title: String,
    index: Int,
    total: Int,
    seconds: Int,
    question: Question,
    selectedIndex: Int,
    isAnswered: Boolean,
    isCorrect: Boolean?,
    correctIndex: Int,
    onSelect: (Int) -> Unit,
    onNext: () -> Unit
) {
    Scaffold(
        containerColor = Color.Transparent,
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(title, style = MaterialTheme.typography.titleLarge) },
                actions = {
                    AssistChip(
                        onClick = {},
                        label = { Text("${seconds}s", style = MaterialTheme.typography.labelLarge) },
                        leadingIcon = { Icon(Icons.Filled.AccessTime, null) },
                        modifier = Modifier.padding(end = 12.dp)
                    )
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(horizontal = 18.dp, vertical = 14.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            val progress = (index + 1f) / total.toFloat()
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(99.dp))
            )

            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Text(
                    "Soru ${index + 1} / $total",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.75f)
                )
                Spacer(Modifier.weight(1f))
                if (isAnswered) {
                    val txt = if (isCorrect == true) "Doğru" else "Yanlış"
                    AssistChip(
                        onClick = {},
                        label = { Text(txt, style = MaterialTheme.typography.labelLarge) },
                        leadingIcon = { Icon(Icons.Filled.CheckCircle, null) }
                    )
                }
            }

            ElevatedCard(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(18.dp)) {
                Column(Modifier.padding(18.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.Quiz, null, modifier = Modifier.size(22.dp))
                        Spacer(Modifier.width(10.dp))
                        Text("Soru", style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.75f))
                    }
                    Spacer(Modifier.height(10.dp))
                    Text(question.text, style = MaterialTheme.typography.headlineMedium)
                }
            }

            // Sabit renkler: doğru=yeşil, yanlış=kırmızı
            val correctBg = Color(0xFFD1FAE5)
            val correctBorder = Color(0xFF10B981)
            val wrongBg = Color(0xFFFEE2E2)
            val wrongBorder = Color(0xFFEF4444)

            val base = MaterialTheme.colorScheme.surface
            val disabledBg = MaterialTheme.colorScheme.surfaceVariant

            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                question.options.forEachIndexed { i, opt ->
                    val isSel = (selectedIndex == i)
                    val isCorrectOption = (i == correctIndex)

                    val target = when {
                        !isAnswered -> base
                        isAnswered && isCorrectOption -> correctBg
                        isAnswered && isSel && !isCorrectOption -> wrongBg
                        else -> disabledBg
                    }
                    val bg by animateColorAsState(target, label = "optBg")

                    val borderColor = when {
                        !isAnswered && isSel -> MaterialTheme.colorScheme.primary
                        isAnswered && isCorrectOption -> correctBorder
                        isAnswered && isSel && !isCorrectOption -> wrongBorder
                        else -> MaterialTheme.colorScheme.outlineVariant
                    }

                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 64.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .clickable(enabled = !isAnswered) { onSelect(i) },
                        color = bg,
                        border = BorderStroke(1.dp, borderColor),
                        tonalElevation = 2.dp,
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            val label = ('A' + i).toString()
                            Surface(
                                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Text(
                                    label,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                    style = MaterialTheme.typography.titleMedium
                                )
                            }
                            Spacer(Modifier.width(12.dp))
                            Text(opt, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            }

            Button(
                onClick = onNext,
                enabled = isAnswered,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(58.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text(if (index == total - 1) "Sınavı Bitir" else "Sonraki Soru", style = MaterialTheme.typography.titleMedium)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ResultScreenModern(
    score: Int,
    total: Int,
    seconds: Int,
    onRestart: () -> Unit,
    onMenu: () -> Unit
) {
    Scaffold(
        containerColor = Color.Transparent,
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Sonuç", style = MaterialTheme.typography.titleLarge) }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(20.dp)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            ElevatedCard(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(18.dp)) {
                Column(Modifier.padding(18.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Skor", style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.75f))
                    Spacer(Modifier.height(8.dp))
                    Text("$score / $total", style = MaterialTheme.typography.headlineLarge)
                    Spacer(Modifier.height(10.dp))
                    Text("Süre: ${seconds}s", style = MaterialTheme.typography.bodyLarge)
                }
            }

            Spacer(Modifier.height(18.dp))

            Button(
                onClick = onRestart,
                modifier = Modifier.fillMaxWidth().height(58.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text("Tekrar Başla", style = MaterialTheme.typography.titleMedium)
            }

            Spacer(Modifier.height(12.dp))

            OutlinedButton(
                onClick = onMenu,
                modifier = Modifier.fillMaxWidth().height(58.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text("Ana Menüye Dön", style = MaterialTheme.typography.titleMedium)
            }
        }
    }
}
