package com.ctrlzgod.mindstep

import android.app.Application
import androidx.room.Room
import com.ctrlzgod.mindstep.data.local.MindStepDatabase

class MindStepApplication : Application() {

    // Usamos o "lazy" para que a base de dados só seja construída
    // no exato momento em que for precisa pela primeira vez.
    val database: MindStepDatabase by lazy {
        Room.databaseBuilder(
            this,
            MindStepDatabase::class.java,
            "mindstep_database" // O nome do ficheiro físico que vai ficar guardado no telemóvel
        ).build()
    }
}