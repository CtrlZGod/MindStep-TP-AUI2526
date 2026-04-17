package com.ctrlzgod.mindstep.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ctrlzgod.mindstep.data.local.MoodRecord
import com.ctrlzgod.mindstep.data.local.MoodRecordDao
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MindStepViewModel(private val dao: MoodRecordDao) : ViewModel() {

    // 1. LER OS DADOS: Pegamos na corrente de dados do DAO e transformamos num Estado (State)
    // que o Jetpack Compose consegue "observar" automaticamente no ecrã.
    val allRecords = dao.getAllRecords()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // 2. GUARDAR DADOS: Função que a Interface vai chamar quando o utilizador clicar em "Guardar"
    fun addRecord(mood: Int, anxiety: Int, notes: String? = null) {
        // O viewModelScope.launch garante que guardar na base de dados acontece
        // "em pano de fundo" e não bloqueia a aplicação.
        viewModelScope.launch {
            val newRecord = MoodRecord(
                moodLevel = mood,
                anxietyLevel = anxiety,
                notes = notes
            )
            dao.insertRecord(newRecord)
        }
    }
}