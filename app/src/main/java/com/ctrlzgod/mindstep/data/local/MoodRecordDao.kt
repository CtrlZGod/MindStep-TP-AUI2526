package com.ctrlzgod.mindstep.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

// A anotação @Dao diz ao Room que este é o nosso "funcionário" do arquivo
@Dao
interface MoodRecordDao {

    // Ordem 1: Inserir um novo registo
    // O Room trata de guardar a informação. Se houver um conflito, ele substitui.
    @Insert
    suspend fun insertRecord(record: MoodRecord) //"suspend" porque é uma operação que pode demorar (acesso à base de dados)

    // Ordem 2: Ler todos os registos
    // O @Query permite-nos fazer um pedido (uma "query") à base de dados.
    // Usamos o "ORDER BY timestamp DESC" para mostrar os mais recentes primeiro.
    @Query("SELECT * FROM mood_records ORDER BY timestamp DESC")
    fun getAllRecords(): Flow<List<MoodRecord>>
}