package com.ctrlzgod.mindstep.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.ctrlzgod.mindstep.data.local.MoodRecord

@Composable
fun DashboardScreen(records: List<MoodRecord>) {
    // Aplicando Carga Cognitiva Reduzida:
    // O ecrã adapta-se se não houver dados.
    if (records.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text("O teu diário está vazio. Clica no + para começar!")
        }
    } else {
        // Se houver dados, mostramos esta mensagem temporária.
        // Mais à frente, vamos substituir isto pela lista de Cards!
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text("Sucesso! Tens ${records.size} registo(s) guardado(s).")
        }
    }
}