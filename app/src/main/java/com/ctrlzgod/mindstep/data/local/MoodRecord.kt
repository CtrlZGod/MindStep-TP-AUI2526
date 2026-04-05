package com.ctrlzgod.mindstep.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

// O @Entity diz ao Room que isto vai ser uma tabela na base de dados
@Entity(tableName = "mood_records")
data class MoodRecord(
    // O @PrimaryKey cria um ID único para cada registo automaticamente
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    // A data em que o utilizador fez o registo (guardada como timestamp/milisegundos)
    val timestamp: Long = System.currentTimeMillis(),

    // Nível de Humor (ex: 1 a 5, onde 1 é muito triste e 5 é muito feliz)
    val moodLevel: Int,

    // Nível de Ansiedade (ex: 1 a 5, onde 1 é muito relaxado e 5 é muito ansioso)
    val anxietyLevel: Int,

    // Uma nota opcional que o utilizador pode escrever (ou ditar por voz)
    val notes: String? = null
)