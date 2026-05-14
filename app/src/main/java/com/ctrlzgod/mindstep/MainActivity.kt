package com.ctrlzgod.mindstep

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.* // Importante para o collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ctrlzgod.mindstep.data.local.MindStepDatabase
import com.ctrlzgod.mindstep.ui.MindStepViewModel
import com.ctrlzgod.mindstep.ui.screens.AddRecordScreen
import com.ctrlzgod.mindstep.ui.screens.DashboardScreen // Novo Import
import com.ctrlzgod.mindstep.ui.theme.MindStepTheme

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            // 1. Configuração da Base de Dados e ViewModel
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

            // 2. ligacaobase de dados em tempo real
            val allRecords by viewModel.allRecords.collectAsState()

            // 3. Estado do ecrã atual
            var currentScreen by remember { mutableStateOf("home") }

            MindStepTheme {
                Scaffold(
                    topBar = {
                        CenterAlignedTopAppBar(title = { Text("MindStep") })
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