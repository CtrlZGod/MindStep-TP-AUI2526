package com.ctrlzgod.mindstep.ui.screens

import android.Manifest
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.ctrlzgod.mindstep.data.local.MoodRecord
import com.ctrlzgod.mindstep.ui.components.MoodCard
import com.ctrlzgod.mindstep.notifications.ReminderManager

@Composable
fun DashboardScreen(records: List<MoodRecord>) {
    // Precisamos de saber onde estamos (o Context) para mostrar os pop-ups (Toasts) e agendar o alarme
    val context = LocalContext.current

    // O mecanismo moderno do Jetpack Compose que pede autorização ao utilizador no ecrã (obrigatório no Android 13+)
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            // Se o utilizador disser "Sim", ativamos o lembrete!
            ReminderManager.scheduleReminder(context, 20, 0) // Agendado para as 20h00
            Toast.makeText(context, "Lembrete ativado para as 20:00!", Toast.LENGTH_SHORT).show()
        } else {
            // Se disser "Não", respeitamos a decisão dele (Boa prática de Usabilidade!)
            Toast.makeText(context, "Permissão negada. Não te vamos chatear!", Toast.LENGTH_LONG).show()
        }
    }

    // Usamos uma Column para empilhar o nosso novo Botão em cima da tua lista
    Column(modifier = Modifier.fillMaxSize()) {

        // O nosso novo Botão para ativar o Lembrete de Água/Meditação
        Button(
            onClick = {
                // Verificamos se o telemóvel é Android 13 ou superior
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    // Dispara o pop-up do Android a pedir permissão
                    permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                } else {
                    // Se for um Android mais antigo, não precisa de pedir permissão no ecrã, basta agendar
                    ReminderManager.scheduleReminder(context, 20, 0)
                    Toast.makeText(context, "Lembrete ativado para as 20:00!", Toast.LENGTH_SHORT).show()
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Notifications,
                contentDescription = "Ícone de Notificações",
                modifier = Modifier.padding(end = 8.dp)
            )
            Text("Ativar Lembrete (20:00)")
        }

        // --- DAQUI PARA BAIXO É O TEU CÓDIGO ORIGINAL INTACTO ---
        if (records.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("O teu diário está vazio. Clica no + para começar!")
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
            ) {
                items(records) { record ->
                    MoodCard(record = record)
                    Spacer(modifier = Modifier.height(8.dp)) // Apenas um pequeno espaço entre cartões
                }
            }
        }
    }
}