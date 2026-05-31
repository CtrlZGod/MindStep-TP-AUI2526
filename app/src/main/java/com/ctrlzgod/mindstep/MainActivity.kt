package com.ctrlzgod.mindstep

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.snap
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ctrlzgod.mindstep.data.local.MindStepDatabase
import com.ctrlzgod.mindstep.data.local.MoodRecord
import com.ctrlzgod.mindstep.ui.MindStepViewModel
import com.ctrlzgod.mindstep.ui.screens.AddRecordScreen
import com.ctrlzgod.mindstep.ui.screens.DashboardScreen
import com.ctrlzgod.mindstep.ui.screens.ProfileScreen
import com.ctrlzgod.mindstep.ui.theme.MindStepTheme
import com.ctrlzgod.mindstep.util.SettingsManager
import com.ctrlzgod.mindstep.util.rememberTtsController
import kotlinx.coroutines.launch // Importante para executar rotinas em segundo plano

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val context = LocalContext.current
            val database = MindStepDatabase.getDatabase(context)
            val dao = database.moodRecordDao()
            val viewModelFactory = object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    @Suppress("UNCHECKED_CAST")
                    return MindStepViewModel(dao) as T
                }
            }
            val viewModel: MindStepViewModel = viewModel(factory = viewModelFactory)

            val allRecords by viewModel.allRecords.collectAsState()
            var currentScreen by remember { mutableStateOf("home") }

            val settings = remember { SettingsManager(context) }
            val tts = rememberTtsController()
            // Preferências de acessibilidade, persistidas entre arranques
            var reduceAnimations by remember { mutableStateOf(settings.reduceAnimations) }
            var voiceFeedback by remember { mutableStateOf(settings.voiceFeedback) }
            // Registo em edição (null = a criar um novo)
            var editingRecord by remember { mutableStateOf<MoodRecord?>(null) }

            //PASSOS
            // guardar o texto dos passos
            var todaySteps by remember { mutableStateOf("A calcular...") }
            val coroutineScope = rememberCoroutineScope()
            val healthConnectManager = remember { com.ctrlzgod.mindstep.data.health.HealthConnectManager(context) }

            val permissionLauncher = rememberLauncherForActivityResult(
                contract = androidx.health.connect.client.PermissionController.createRequestPermissionResultContract()
            ) { grantedPermissions ->
                if (grantedPermissions.containsAll(healthConnectManager.permissions)) {
                    // user aceitou -> lê os passos sem bloqueios
                    coroutineScope.launch {
                        val steps = healthConnectManager.readStepsToday()
                        todaySteps = "$steps passos"
                    }
                } else {
                    todaySteps = "Sem acesso"
                }
            }

            LaunchedEffect(currentScreen) {
                if (currentScreen == "home" && healthConnectManager.isAvailable()) {
                    val granted = healthConnectManager.healthConnectClient.permissionController.getGrantedPermissions()
                    if (granted.containsAll(healthConnectManager.permissions)) {
                        val steps = healthConnectManager.readStepsToday()
                        todaySteps = "$steps passos"
                    } else {
                        permissionLauncher.launch(healthConnectManager.permissions)
                    }
                }
            }

            // Locução: anuncia o ecrã atual quando o feedback por voz está ativo
            LaunchedEffect(currentScreen) {
                if (voiceFeedback) {
                    val name = when (currentScreen) {
                        "home" -> "Início"
                        "add_record" -> "Novo registo"
                        "edit" -> "Editar registo"
                        "profile" -> "Definições"
                        else -> ""
                    }
                    if (name.isNotEmpty()) tts.speak(name)
                }
            }

            MindStepTheme {
                Scaffold(
                    topBar = {
                        //NÚMERO EM TEMPO REAL
                        CenterAlignedTopAppBar(title = { Text("MindStep | $todaySteps") })
                    },
                    bottomBar = {
                        BottomAppBar {
                            NavigationBarItem(
                                selected = currentScreen == "home",
                                onClick = { currentScreen = "home" },
                                icon = { Icon(Icons.Default.Home, contentDescription = "Ir para Início") },
                                label = { Text("Início") }
                            )
                            NavigationBarItem(
                                selected = currentScreen == "profile",
                                onClick = { currentScreen = "profile" },
                                icon = { Icon(Icons.Default.Person, contentDescription = "Ver Perfil") },
                                label = { Text("Perfil") }
                            )
                        }
                    },
                    floatingActionButton = {
                        if (currentScreen == "home") {
                            FloatingActionButton(onClick = {
                                editingRecord = null
                                currentScreen = "add_record"
                            }) {
                                Icon(Icons.Default.Add, contentDescription = "Registar Humor de Hoje")
                            }
                        }
                    }
                ) { innerPadding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    ) {
                        AnimatedContent(
                            targetState = currentScreen,
                            transitionSpec = {
                                if (reduceAnimations) {
                                    fadeIn(animationSpec = snap()) togetherWith
                                        fadeOut(animationSpec = snap())
                                } else {
                                    fadeIn(animationSpec = tween(300)) togetherWith
                                        fadeOut(animationSpec = tween(300))
                                }
                            },
                            label = "screen_transition"
                        ) { screen ->
                            when (screen) {
                                "home" -> {
                                    DashboardScreen(
                                        records = allRecords,
                                        onRecordClick = { record ->
                                            editingRecord = record
                                            currentScreen = "edit"
                                        },
                                        onDeleteRecords = { ids -> viewModel.deleteRecords(ids) },
                                        voiceFeedback = voiceFeedback,
                                        speak = { tts.speak(it) }
                                    )
                                }
                                "add_record" -> {
                                    AddRecordScreen(
                                        onSaveRecord = { mood, anxiety, notes ->
                                            viewModel.addRecord(mood, anxiety, notes)
                                            if (voiceFeedback) tts.speak("Registo guardado")
                                            currentScreen = "home"
                                        },
                                        speak = { tts.speak(it) }
                                    )
                                }
                                "edit" -> {
                                    val rec = editingRecord
                                    if (rec != null) {
                                        AddRecordScreen(
                                            existingRecord = rec,
                                            onSaveRecord = { mood, anxiety, notes ->
                                                viewModel.updateRecord(
                                                    rec.copy(
                                                        moodLevel = mood,
                                                        anxietyLevel = anxiety,
                                                        notes = notes
                                                    )
                                                )
                                                if (voiceFeedback) tts.speak("Registo atualizado")
                                                editingRecord = null
                                                currentScreen = "home"
                                            },
                                            speak = { tts.speak(it) }
                                        )
                                    }
                                }
                                "profile" -> {
                                    ProfileScreen(
                                        records = allRecords,
                                        reduceAnimations = reduceAnimations,
                                        onReduceAnimationsChange = {
                                            reduceAnimations = it
                                            settings.reduceAnimations = it
                                        },
                                        voiceFeedback = voiceFeedback,
                                        onVoiceFeedbackChange = {
                                            voiceFeedback = it
                                            settings.voiceFeedback = it
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}