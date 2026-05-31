package com.ctrlzgod.mindstep.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface MoodRecordDao {

    @Insert
    fun insertRecord(record: MoodRecord)

    @Update
    fun updateRecord(record: MoodRecord)

    @Query("DELETE FROM mood_records WHERE id IN (:ids)")
    fun deleteByIds(ids: List<Int>)

    @Query("SELECT * FROM mood_records ORDER BY timestamp DESC")
    fun getAllRecords(): Flow<List<MoodRecord>>
}