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

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            // 1. Inicializa a Base de Dados e o DAO
            val context = LocalContext.current
            val database = MindStepDatabase.getDatabase(context)
            val dao = database.moodRecordDao()

            // 2. Prepara o ViewModel (fornecendo o DAO que ele precisa)
            val viewModelFactory = object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    @Suppress("UNCHECKED_CAST")
                    return MindStepViewModel(dao) as T
                }
            }
            val mindStepViewModel: MindStepViewModel = viewModel(factory = viewModelFactory)

            // 3. Arranca a App passando o ViewModel
            MindStepApp(mindStepViewModel)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MindStepApp(viewModel: MindStepViewModel) {
    // Variável que controla a navegação básica (Que ecrã mostrar?)
    var currentScreen by remember { mutableStateOf("home") }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("MindStep") })
        },
        bottomBar = {
            BottomAppBar {
                NavigationBarItem(
                    selected = currentScreen == "home",
                    onClick = { currentScreen = "home" },
                    icon = { Icon(Icons.Default.Home, contentDescription = "Ir para o Início") },
                    label = { Text("Início") }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = { /* Futuro: Ecrã de Perfil/Estatísticas */ },
                    icon = { Icon(Icons.Default.Person, contentDescription = "Ver Perfil") },
                    label = { Text("Perfil") }
                )
            }
        },
        floatingActionButton = {
            // Só mostramos o botão de adicionar se estivermos no ecrã inicial
            if (currentScreen == "home") {
                FloatingActionButton(onClick = { currentScreen = "add_record" }) {
                    Icon(Icons.Default.Add, contentDescription = "Registar Humor de Hoje")
                }
            }
        }
    ) { innerPadding ->

        // Área central do ecrã
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // A nossa "Navegação" simples
            when (currentScreen) {
                "home" -> {
                    // Placeholder temporário do ecrã inicial
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("O teu diário está vazio. Clica no + para começar!")
                    }
                }
                "add_record" -> {
                    // O nosso novo ecrã brilhante
                    AddRecordScreen(
                        onSaveRecord = { mood, anxiety, notes ->
                            // Envia os dados para a Base de Dados via ViewModel
                            viewModel.addRecord(mood, anxiety, notes)

                            // Volta automaticamente ao ecrã inicial após guardar!
                            currentScreen = "home"
                        }
                    )
                }
            }
        }
    }
}