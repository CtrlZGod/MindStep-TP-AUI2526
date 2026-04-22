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

            //base de dados e viewmodel
            val context = LocalContext.current
            val database = MindStepDatabase.getDatabase(context)
            val dao = database.moodRecordDao()
            val viewModelFactory = object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    @Suppress("UNCHECKED_CAST")
                    return MindStepViewModel(dao) as T
                }
            }
            val mindStepViewModel: MindStepViewModel = viewModel(factory = viewModelFactory)

            //trigger da UI principal
            MindStepApp(mindStepViewModel)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MindStepApp(viewModel: MindStepViewModel) {
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
                    onClick = {  },
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

        //area onde os ecras vão aparecer
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (currentScreen) {
                "home" -> {
                    // Placeholder temporário do ecrã inicial
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("O teu diário está vazio. Clica no + para começar!")
                    }
                }
                "add_record" -> {

                    AddRecordScreen(
                        onSaveRecord = { mood, anxiety, notes ->
                            viewModel.addRecord(mood, anxiety, notes)
                            currentScreen = "home"
                        }
                    )
                }
            }
        }
    }
}