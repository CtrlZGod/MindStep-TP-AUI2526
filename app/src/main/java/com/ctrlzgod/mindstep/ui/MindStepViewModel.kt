package com.ctrlzgod.mindstep.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ctrlzgod.mindstep.data.local.MoodRecord
import com.ctrlzgod.mindstep.data.local.MoodRecordDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MindStepViewModel(private val dao: MoodRecordDao) : ViewModel() {

    val allRecords = dao.getAllRecords()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun addRecord(mood: Int, anxiety: Int, notes: String? = null) {
        viewModelScope.launch {
            val newRecord = MoodRecord(
                moodLevel = mood,
                anxietyLevel = anxiety,
                notes = notes
            )

            withContext(Dispatchers.IO) {
                dao.insertRecord(newRecord)
            }
        }
    }
}