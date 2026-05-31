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
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.ctrlzgod.mindstep.data.local.MoodRecord
import com.ctrlzgod.mindstep.ui.components.MoodCard
import com.ctrlzgod.mindstep.ui.components.MoodChart
import com.ctrlzgod.mindstep.notifications.ReminderManager
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    records: List<MoodRecord>,
    onRecordClick: (MoodRecord) -> Unit = {},
    onDeleteRecords: (List<Int>) -> Unit = {},
    voiceFeedback: Boolean = false,
    speak: (String) -> Unit = {}
) {
    val context = LocalContext.current

    var pendingHour by remember { mutableIntStateOf(20) }
    var pendingMinute by remember { mutableIntStateOf(0) }
    var showTimeDialog by remember { mutableStateOf(false) }

    // Seleção múltipla para eliminação
    var selectionMode by remember { mutableStateOf(false) }
    val selectedIds = remember { mutableStateListOf<Int>() }

    val doSchedule: (Int, Int) -> Unit = { h, m ->
        ReminderManager.scheduleReminder(context, h, m)
        val msg = String.format(Locale("pt", "PT"), "Lembrete diário marcado para as %02d:%02d", h, m)
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
        if (voiceFeedback) speak(msg)
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            doSchedule(pendingHour, pendingMinute)
        } else {
            Toast.makeText(context, "Permissão negada. Não te vamos chatear!", Toast.LENGTH_LONG).show()
        }
    }

    if (showTimeDialog) {
        val timeState = rememberTimePickerState(initialHour = 20, initialMinute = 0, is24Hour = true)
        AlertDialog(
            onDismissRequest = { showTimeDialog = false },
            title = { Text("Marcar lembrete") },
            text = { TimePicker(state = timeState) },
            confirmButton = {
                TextButton(onClick = {
                    pendingHour = timeState.hour
                    pendingMinute = timeState.minute
                    showTimeDialog = false
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                    } else {
                        doSchedule(pendingHour, pendingMinute)
                    }
                }) { Text("Marcar") }
            },
            dismissButton = {
                TextButton(onClick = { showTimeDialog = false }) { Text("Cancelar") }
            }
        )
    }

    Column(modifier = Modifier.fillMaxSize()) {

        if (selectionMode) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("${selectedIds.size} selecionado(s)", style = MaterialTheme.typography.titleMedium)
                Row {
                    TextButton(onClick = {
                        selectedIds.clear()
                        selectionMode = false
                    }) { Text("Cancelar") }
                    Button(
                        onClick = {
                            val ids = selectedIds.toList()
                            onDeleteRecords(ids)
                            if (voiceFeedback) speak("${ids.size} registos eliminados")
                            selectedIds.clear()
                            selectionMode = false
                        },
                        enabled = selectedIds.isNotEmpty()
                    ) { Text("Eliminar") }
                }
            }
        } else {
            Button(
                onClick = { showTimeDialog = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Notifications,
                    contentDescription = null,
                    modifier = Modifier.padding(end = 8.dp)
                )
                Text("Lembretes")
            }
        }

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
                item {
                    MoodChart(records = records, onSpeak = { speak(it) })
                    Spacer(modifier = Modifier.height(16.dp))
                }
                items(records, key = { it.id }) { record ->
                    val isSelected = selectedIds.contains(record.id)
                    MoodCard(
                        record = record,
                        selected = isSelected,
                        onClick = {
                            if (selectionMode) {
                                if (isSelected) selectedIds.remove(record.id) else selectedIds.add(record.id)
                                if (selectedIds.isEmpty()) selectionMode = false
                            } else {
                                onRecordClick(record)
                            }
                        },
                        onLongClick = {
                            if (!selectionMode) selectionMode = true
                            if (!selectedIds.contains(record.id)) selectedIds.add(record.id)
                        }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}
