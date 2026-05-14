package com.ctrlzgod.mindstep

import android.app.Application
import androidx.room.Room
import com.ctrlzgod.mindstep.data.local.MindStepDatabase

class MindStepApplication : Application() {

    val database: MindStepDatabase by lazy {
        Room.databaseBuilder(
            this,
            MindStepDatabase::class.java,
            "mindstep_database" // nome guardado no telefoen
        ).build()
    }
}