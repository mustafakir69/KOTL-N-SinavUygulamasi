package com.example.sinav_uygulamasi.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import com.example.sinav_uygulamasi.QuizUiState
import com.example.sinav_uygulamasi.QuizViewModel
import com.example.sinav_uygulamasi.ui.design.AppColors
import com.example.sinav_uygulamasi.ui.design.Dimens

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(s: QuizUiState, vm: QuizViewModel) {

    val cfg = LocalConfiguration.current
    val isLandscape = cfg.screenWidthDp > cfg.screenHeightDp

    // İstersen Settings için de max width koy:
    val contentMaxWidth = when {
        cfg.screenWidthDp >= 1000 -> 900.dp
        cfg.screenWidthDp >= 700 -> 720.dp
        else -> 640.dp
    }

    Scaffold(
        containerColor = Color.Transparent,
        topBar = {
            TopAppBar(
                title = { Text("Ayarlar", style = MaterialTheme.typography.titleLarge) },
                navigationIcon = {
                    IconButton(onClick = { vm.goMenu() }) { Icon(Icons.Filled.ArrowBack, null) }
                }
            )
        }
    ) { pad ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(pad)
                .padding(Dimens.ScreenPadding),
            contentAlignment = if (isLandscape) Alignment.TopStart else Alignment.TopCenter
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()                    // ✅ landscape’te kenara yaslı his
                    .widthIn(max = contentMaxWidth),   // ✅ çok genişlemesin
                verticalArrangement = Arrangement.spacedBy(Dimens.Gap)
            ) {
                ElevatedCard(
                    shape = RoundedCornerShape(Dimens.CardRadius),
                    colors = CardDefaults.elevatedCardColors(containerColor = AppColors.Card)
                ) {
                    Column(
                        Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                "Turuncu Tema",
                                style = MaterialTheme.typography.titleMedium,
                                modifier = Modifier.weight(1f)
                            )
                            Switch(
                                checked = s.orangeTheme,
                                onCheckedChange = { vm.saveSettings(it, s.largeText) }
                            )
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                "Büyük Yazı",
                                style = MaterialTheme.typography.titleMedium,
                                modifier = Modifier.weight(1f)
                            )
                            Switch(
                                checked = s.largeText,
                                onCheckedChange = { vm.saveSettings(s.orangeTheme, it) }
                            )
                        }
                    }
                }
            }
        }
    }
}
