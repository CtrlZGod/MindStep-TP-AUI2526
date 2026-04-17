package com.ctrlzgod.mindstep

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            // A nossa função principal que desenha a interface arranca aqui
            MindStepApp()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MindStepApp() {
    // O Scaffold cria a estrutura base exigida pelo professor
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("MindStep") }
            )
        },
        bottomBar = {
            // A barra de navegação no fundo do ecrã
            BottomAppBar {
                NavigationBarItem(
                    selected = true, // Mais tarde faremos isto mudar dinamicamente
                    onClick = { /* Ação para navegar para o Dashboard */ },
                    icon = { Icon(Icons.Default.Home, contentDescription = "Início") },
                    label = { Text("Início") }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = { /* Ação para navegar para Definições/Perfil */ },
                    icon = { Icon(Icons.Default.Person, contentDescription = "Perfil") },
                    label = { Text("Perfil") }
                )
            }
        },
        floatingActionButton = {
            // O botão redondo de destaque para o Registo de Humor
            FloatingActionButton(onClick = { /* Ação para abrir o ecrã de novo registo */ }) {
                Icon(Icons.Default.Add, contentDescription = "Novo Registo")
            }
        }
    ) { innerPadding ->
        // O "miolo" ou conteúdo principal da nossa app entra aqui
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding), // É crucial usar este padding para o conteúdo não ficar escondido debaixo das barras!
            contentAlignment = Alignment.Center
        ) {
            Text("O esqueleto da MindStep está pronto!")
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun MindStepAppPreview() {
    MindStepApp()
}