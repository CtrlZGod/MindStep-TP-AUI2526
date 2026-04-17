package com.ctrlzgod.mindstep.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview

/**
Ecrã principal para o utilizador registar o seu estado de espírito.
 */
@Composable
fun AddRecordScreen(
    // Função de callback: avisa o resto da app quando o utilizador clica em "Guardar"
    onSaveRecord: (mood: Int, anxiety: Int, notes: String) -> Unit
) {
    // 1. GESTÃO DE ESTADO (State)
    // O 'remember' faz com que o Compose não se esqueça destes valores se o ecrã for redesenhado.
    // O 'mutableIntStateOf' permite que o valor mude ao longo do tempo.
    // Vamos assumir uma escala de 1 a 5. Começamos no 3 (neutro).
    var moodLevel by remember { mutableIntStateOf(3) }
    var anxietyLevel by remember { mutableIntStateOf(3) }
    var notes by remember { mutableStateOf("") }

    // 2. ESTRUTURA VISUAL (Layout)
    // A Column organiza todos os elementos de cima para baixo.
    Column(
        modifier = Modifier
            .fillMaxSize() // Faz com que a Column ocupe todo o ecrã disponível
            .padding(24.dp), // Margens generosas evitam que o ecrã pareça "apertado" (bom para a usabilidade)
        horizontalAlignment = Alignment.CenterHorizontally // Centra os itens horizontalmente
    ) {

        // Título da página
        Text(
            text = "Como te sentes hoje?",
            style = MaterialTheme.typography.headlineMedium
        )

        // Spacer cria um espaço vazio entre os elementos
        Spacer(modifier = Modifier.height(40.dp))

        // TODO: Passo 2 - Substituir este texto pelo nosso Seletor Visual de Humor
        Text(text = "[ Aqui vai entrar a nossa Escala Visual de Humor ]")

        Spacer(modifier = Modifier.height(40.dp))

        // TODO: Passo 3 - Substituir este texto pelo campo de notas
        Text(text = "[ Aqui vai entrar o campo para apontamentos ]")

        // O 'weight(1f)' preenche todo o espaço vazio restante, empurrando o botão para o fundo do ecrã!
        Spacer(modifier = Modifier.weight(1f))

        // 3. AÇÃO PRINCIPAL
        // Botão grande e fácil de clicar (boa prática de acessibilidade)
        Button(
            onClick = {
                // Quando clicado, envia os dados atuais gravados no "remember"
                onSaveRecord(moodLevel, anxietyLevel, notes)
            },
            modifier = Modifier
                .fillMaxWidth() // Ocupa toda a largura da ecrã (fácil de alcançar com o polegar)
                .height(56.dp)  // Altura mínima recomendada para botões acessíveis
        ) {
            Text("Guardar Registo", style = MaterialTheme.typography.titleMedium)
        }
    }
}

// A anotação @Preview diz ao Android Studio para desenhar isto no ecrã lateral.
// showBackground = true coloca um fundo branco, ajudando a ver melhor o ecrã.
@Preview(showBackground = true)
@Composable
fun AddRecordScreenPreview() {
    // Aqui chamamos o nosso ecrã
    AddRecordScreen(
        // Como é só uma pré-visualização visual, passamos uma função vazia {}
        // O preview não vai gravar nada na base de dados, por isso não faz mal.
        onSaveRecord = { mood, anxiety, notes ->
            // Não faz nada
        }
    )
}