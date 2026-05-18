package com.ctrlzgod.mindstep

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.room.Room
import com.ctrlzgod.mindstep.data.local.MindStepDatabase

class MindStepApplication : Application() {

    val database: MindStepDatabase by lazy {
        Room.databaseBuilder(
            this,
            MindStepDatabase::class.java,
            "mindstep_database"
        ).build()
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    private fun createNotificationChannel() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Lembretes MindStep"
            val descriptionText = "Avisos importantes para beber água e meditar"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel("mindstep_reminders", name, importance).apply {
                description = descriptionText
            }

            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}