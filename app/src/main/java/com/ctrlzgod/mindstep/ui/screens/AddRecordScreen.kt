package com.ctrlzgod.mindstep.ui.screens

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.speech.RecognizerIntent
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ctrlzgod.mindstep.util.HapticHelper
import java.util.Locale

@Composable
fun AddRecordScreen(
    onSaveRecord: (mood: Int, anxiety: Int, notes: String) -> Unit
) {
    var moodLevel by remember { mutableIntStateOf(3) }
    var anxietyLevel by remember { mutableIntStateOf(3) }
    var notes by remember { mutableStateOf("") }

    val haptic = LocalHapticFeedback.current
    val context = LocalContext.current

    // Reconhecimento de voz: preenche as notas por ditado (input por voz)
    val speechLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val spoken = result.data
                ?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
                ?.firstOrNull()
            if (!spoken.isNullOrBlank()) {
                notes = if (notes.isBlank()) spoken else "$notes $spoken"
            }
        }
    }

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
                // intensidade da vibração proporcional ao nível de ansiedade
                HapticHelper.vibrateForAnxiety(context, anxietyLevel)
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

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedButton(
            onClick = {
                val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                    putExtra(
                        RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                        RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
                    )
                    putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale("pt", "PT").toLanguageTag())
                    putExtra(RecognizerIntent.EXTRA_PROMPT, "Fala agora...")
                }
                try {
                    speechLauncher.launch(intent)
                } catch (e: ActivityNotFoundException) {
                    Toast.makeText(
                        context,
                        "Reconhecimento de voz indisponível neste dispositivo.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .semantics { contentDescription = "Ditar nota por voz" }
        ) {
            Text("🎙  Ditar nota por voz")
        }

        Spacer(modifier = Modifier.weight(1f))

        //Guardar
        Button(
            onClick = {
                // vibração de sucesso (objetivo atingido: registo guardado)
                HapticHelper.vibrateSuccess(context)
                onSaveRecord(moodLevel, anxietyLevel, notes)
            },
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