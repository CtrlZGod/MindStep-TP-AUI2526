package com.ctrlzgod.mindstep.notifications

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.ctrlzgod.mindstep.R

class ReminderReceiver : BroadcastReceiver() {

    // Esta função corre automaticamente no exato segundo em que o alarme dispara!
    override fun onReceive(context: Context, intent: Intent) {

        // Chamamos o Gestor de Notificações do Android
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Vamos construir o design da nossa notificação
        // Repara que usamos o ID "mindstep_reminders" que criámos na MindStepApplication!
        val notification = NotificationCompat.Builder(context, "mindstep_reminders")
            .setSmallIcon(R.mipmap.ic_launcher_round) // Usa o ícone da tua app
            .setContentTitle("MindStep - Momento de Pausa \uD83D\uDCA7")
            .setContentText("Já bebeste água ou meditaste hoje? O teu bem-estar agradece!")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true) // A notificação desaparece sozinha depois de lida
            .build()

        // Disparar a notificação para o ecrã!
        // (O número 1001 é apenas um ID único para esta notificação específica)
        notificationManager.notify(1001, notification)
    }
}