package com.example.sinav_uygulamasi.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import com.example.sinav_uygulamasi.QuizUiState
import com.example.sinav_uygulamasi.QuizViewModel
import com.example.sinav_uygulamasi.ui.components.ContentContainer
import com.example.sinav_uygulamasi.ui.design.AppColors
import com.example.sinav_uygulamasi.ui.design.Dimens

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(s: QuizUiState, vm: QuizViewModel) {
    val cfg = LocalConfiguration.current

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
        ContentContainer(
            screenWidthDp = cfg.screenWidthDp,
            modifier = Modifier
                .fillMaxSize()
                .padding(pad)
                .padding(Dimens.ScreenPadding)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
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
                        Row {
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

                        Row {
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
