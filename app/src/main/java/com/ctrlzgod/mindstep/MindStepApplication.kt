package com.ctrlzgod.mindstep

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.room.Room
import com.ctrlzgod.mindstep.data.local.MindStepDatabase

class MindStepApplication : Application() {

    // O teu cofre de dados (Intacto!)
    val database: MindStepDatabase by lazy {
        Room.databaseBuilder(
            this,
            MindStepDatabase::class.java,
            "mindstep_database" // nome guardado no telefone
        ).build()
    }

    // Esta função corre automaticamente assim que a app é aberta
    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        // O Android só exige Canais a partir da versão 8.0 (Oreo - API 26)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Lembretes MindStep"
            val descriptionText = "Avisos importantes para beber água e meditar"

            // IMPORTANCE_HIGH garante que o telemóvel faz som e mostra um pop-up no topo do ecrã
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel("mindstep_reminders", name, importance).apply {
                description = descriptionText
            }

            // Registar o canal no Sistema Android
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}