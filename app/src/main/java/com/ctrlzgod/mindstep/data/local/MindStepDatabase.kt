package com.ctrlzgod.mindstep.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

// O @Database diz ao Room quais são as tabelas (entities) que existem aqui dentro
// A "version = 1" é importante: se no futuro adicionarmos coisas à tabela, mudamos para 2!
@Database(entities = [MoodRecord::class], version = 1, exportSchema = false)
abstract class MindStepDatabase : RoomDatabase() {

    // Dizemos à base de dados quem é o "funcionário" (DAO) responsável por gerir as tabelas
    abstract fun moodRecordDao(): MoodRecordDao

}