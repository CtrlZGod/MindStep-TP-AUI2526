package com.ctrlzgod.mindstep.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [MoodRecord::class], version = 1, exportSchema = false)
abstract class MindStepDatabase : RoomDatabase() {

    abstract fun moodRecordDao(): MoodRecordDao

    // O companion object permite chamar MindStepDatabase.getDatabase(...) a partir da MainActivity
    companion object {
        // @Volatile garante que se vários fios (threads) da app tentarem aceder, todos veem a mesma coisa atualizada
        @Volatile
        private var INSTANCE: MindStepDatabase? = null

        fun getDatabase(context: Context): MindStepDatabase {
            // Se a base de dados (INSTANCE) já existir, devolve-a.
            // Se não (?:), cria uma nova de forma sincronizada e segura.
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    MindStepDatabase::class.java,
                    "mindstep_database" // Este é o nome do ficheiro que será guardado no Android
                ).build()
                INSTANCE = instance

                // Devolve a instância recém-criada
                instance
            }
        }
    }
}