package com.ctrlzgod.mindstep.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface MoodRecordDao {

    // Removemos o 'suspend' e o ': Long' para fugir aos bugs do KSP.
    // Mais tarde (no ViewModel), vamos garantir que esta gravação é feita em segundo plano!
    @Insert
    fun insertRecord(record: MoodRecord)

    // A leitura com Flow continua igual (esta não costuma dar problemas)
    @Query("SELECT * FROM mood_records ORDER BY timestamp DESC")
    fun getAllRecords(): Flow<List<MoodRecord>>
}