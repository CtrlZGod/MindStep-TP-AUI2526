package com.ctrlzgod.mindstep.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "mood_records")
data class MoodRecord(

    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val timestamp: Long = System.currentTimeMillis(),

    val moodLevel: Int,

    val anxietyLevel: Int,

    val notes: String? = null
)