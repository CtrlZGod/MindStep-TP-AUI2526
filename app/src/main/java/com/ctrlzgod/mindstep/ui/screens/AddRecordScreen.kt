package com.ctrlzgod.mindstep.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun AddRecordScreen(
    onSaveRecord: (mood: Int, anxiety: Int, notes: String) -> Unit
) {
    var moodLevel by remember { mutableIntStateOf(3) }
    var anxietyLevel by remember { mutableIntStateOf(3) }
    var notes by remember { mutableStateOf("") }

    val haptic = LocalHapticFeedback.current

    val moodOptions = listOf(
        Pair("😢", "Muito Mal"),
        Pair("🙁", "Mal"),
        Pair("😐", "Normal"),
        Pair("🙂", "Bem"),
        Pair("😁", "Muito Bem")
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        //Humor
        Text(text = "Como te sentes hoje?", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(24.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            moodOptions.forEachIndexed { index, option ->
                val emojiValue = index + 1
                val isSelected = moodLevel == emojiValue

                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(if (isSelected) MaterialTheme.colorScheme.primaryContainer else Color.Transparent)
                        .clickable {
                            moodLevel = emojiValue
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        }
                        .semantics { contentDescription = "Humor: ${option.second}" }
                ) {
                    Text(text = option.first, fontSize = 32.sp)
                }
            }
        }

        Spacer(modifier = Modifier.height(40.dp))

        //Ansiedade
        Text(text = "Nível de Ansiedade: $anxietyLevel", style = MaterialTheme.typography.titleMedium)

        Slider(
            value = anxietyLevel.toFloat(),
            onValueChange = {
                anxietyLevel = it.toInt()
                // Uma pequena vibração sempre que o valor muda para dar feedback físico
                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
            },
            valueRange = 1f..5f,
            steps = 3, // Cria as "paragens" nos números 2, 3 e 4
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
                .semantics { contentDescription = "Ajustar nível de ansiedade. Atual: $anxietyLevel" }
        )

        Spacer(modifier = Modifier.height(40.dp))

        //Notas
        OutlinedTextField(
            value = notes,
            onValueChange = { notes = it },
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 120.dp)
                .semantics { contentDescription = "Campo opcional para notas diárias" },
            label = { Text("Notas (Opcional)") },
            placeholder = { Text("Detalhes do teu dia...") },
            maxLines = 5
        )

        Spacer(modifier = Modifier.weight(1f))

        //Guardar
        Button(
            onClick = { onSaveRecord(moodLevel, anxietyLevel, notes) },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
        ) {
            Text("Guardar Registo", style = MaterialTheme.typography.titleMedium)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AddRecordScreenPreview() {
    AddRecordScreen(onSaveRecord = { _, _, _ -> })
}