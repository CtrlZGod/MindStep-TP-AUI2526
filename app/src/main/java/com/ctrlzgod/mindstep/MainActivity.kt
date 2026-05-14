package com.ctrlzgod.mindstep

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
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
import com.ctrlzgod.mindstep.ui.MindStepViewModel
import com.ctrlzgod.mindstep.ui.screens.AddRecordScreen
import com.ctrlzgod.mindstep.ui.screens.DashboardScreen
import com.ctrlzgod.mindstep.ui.theme.MindStepTheme
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
                                selected = false,
                                onClick = { /* Perfil ainda não implementado */ },
                                icon = { Icon(Icons.Default.Person, contentDescription = "Ver Perfil") },
                                label = { Text("Perfil") }
                            )
                        }
                    },
                    floatingActionButton = {
                        if (currentScreen == "home") {
                            FloatingActionButton(onClick = { currentScreen = "add_record" }) {
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
                        when (currentScreen) {
                            "home" -> {
                                DashboardScreen(records = allRecords)
                            }
                            "add_record" -> {
                                AddRecordScreen(
                                    onSaveRecord = { mood, anxiety, notes ->
                                        viewModel.addRecord(mood, anxiety, notes ?: "")
                                        currentScreen = "home"
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