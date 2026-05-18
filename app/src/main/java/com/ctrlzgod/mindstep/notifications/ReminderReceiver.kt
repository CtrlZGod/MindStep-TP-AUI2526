package com.ctrlzgod.mindstep.notifications

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.ctrlzgod.mindstep.R

class ReminderReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notification = NotificationCompat.Builder(context, "mindstep_reminders")
            .setSmallIcon(R.mipmap.ic_launcher_round)
            .setContentTitle("MindStep - Momento de Pausa \uD83D\uDCA7")
            .setContentText("Já bebeste água ou meditaste hoje? O teu bem-estar agradece!")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()
        notificationManager.notify(1001, notification)
    }
}