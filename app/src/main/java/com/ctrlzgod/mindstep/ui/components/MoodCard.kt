package com.ctrlzgod.mindstep.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.ctrlzgod.mindstep.data.local.MoodRecord
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun MoodCard(record: MoodRecord) {
    // 1. número do humor de volta no Emoji correspondente
    val moodEmoji = when (record.moodLevel) {
        1 -> "😢"
        2 -> "🙁"
        3 -> "😐"
        4 -> "🙂"
        5 -> "😁"
        else -> "😐"
    }

    // 2. data legível (ex: 14 Mai, 11:30)
    val dateString = SimpleDateFormat("dd MMM, HH:mm", Locale.getDefault()).format(Date(record.timestamp))

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .semantics {
                contentDescription = "Registo de $dateString. Humor nível ${record.moodLevel}. Ansiedade nível ${record.anxietyLevel}."
            },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = moodEmoji,
                style = MaterialTheme.typography.displaySmall,
                modifier = Modifier.padding(end = 16.dp)
            )

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Ansiedade: ${record.anxietyLevel}/5",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = dateString,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                )
            }
        }
    }
}