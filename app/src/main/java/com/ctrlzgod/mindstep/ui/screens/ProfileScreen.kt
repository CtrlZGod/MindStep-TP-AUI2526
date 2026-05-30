package com.ctrlzgod.mindstep.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.ctrlzgod.mindstep.data.local.MoodRecord
import com.ctrlzgod.mindstep.util.ExportHelper

/**
 * Ecrã de Perfil / Definições.
 * Aloja dois requisitos do enunciado:
 *  - Controlo de animações (reduzir/desativar transições — sensibilidade vestibular).
 *  - Exportação de dados (resumo textual partilhável).
 */
@Composable
fun ProfileScreen(
    records: List<MoodRecord>,
    reduceAnimations: Boolean,
    onReduceAnimationsChange: (Boolean) -> Unit
) {
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp)
    ) {
        Text(text = "Definições", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(24.dp))

        // --- Acessibilidade: controlo de animações ---
        Text(text = "Acessibilidade", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(8.dp))

        Card(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = "Reduzir animações", style = MaterialTheme.typography.bodyLarge)
                    Text(
                        text = "Desativa as transições entre ecrãs (sensibilidade vestibular).",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Switch(
                    checked = reduceAnimations,
                    onCheckedChange = onReduceAnimationsChange,
                    modifier = Modifier.semantics {
                        contentDescription = "Reduzir animações de transição"
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // --- Exportação de dados ---
        Text(text = "Dados", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = { ExportHelper.share(context, records) },
            modifier = Modifier
                .fillMaxWidth()
                .semantics { contentDescription = "Exportar e partilhar resumo dos registos" }
        ) {
            Text("Exportar resumo dos dados")
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Gera um resumo textual do teu diário para partilhares com um profissional de saúde.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
