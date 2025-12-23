package com.example.sinav_uygulamasi.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.sinav_uygulamasi.ui.design.AppColors
import com.example.sinav_uygulamasi.ui.design.Dimens

@Composable
fun QuestionCard(
    question: String,
    explanation: String,
    showExplanation: Boolean,
    modifier: Modifier = Modifier
) {
    ElevatedCard(
        modifier = modifier,
        shape = RoundedCornerShape(Dimens.CardRadius),
        colors = CardDefaults.elevatedCardColors(containerColor = AppColors.BgTop),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 10.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = question,
                style = MaterialTheme.typography.headlineSmall, // daha stabil
                color = AppColors.TextDark,
                maxLines = 4,
                overflow = TextOverflow.Ellipsis
            )
            if (showExplanation) {
                Text(
                    text = explanation,
                    style = MaterialTheme.typography.bodyMedium,
                    color = AppColors.TextMid
                )
            }
        }
    }
}
