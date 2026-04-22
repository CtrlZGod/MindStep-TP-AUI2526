package com.ctrlzgod.mindstep.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface MoodRecordDao {

    @Insert
    fun insertRecord(record: MoodRecord)
    @Query("SELECT * FROM mood_records ORDER BY timestamp DESC")
    fun getAllRecords(): Flow<List<MoodRecord>>
}