package com.ctrlzgod.mindstep.notifications

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import java.util.Calendar

object ReminderManager {

    // O @SuppressLint diz ao Android Studio: "Não te preocupes, nós já pedimos a permissão no Manifest!"
    @SuppressLint("ScheduleExactAlarm")
    fun scheduleReminder(context: Context, hour: Int, minute: Int) {
        // Chamamos o Gestor de Alarmes do sistema
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        // Preparamos a "carta" com a morada do nosso carteiro (ReminderReceiver)
        val intent = Intent(context, ReminderReceiver::class.java)

        // O PendingIntent é uma intenção que fica "adormecida" até a hora certa chegar
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            1001, // O ID único deste alarme
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Definimos a hora exata no calendário do telemóvel
        val calendar = Calendar.getInstance().apply {
            timeInMillis = System.currentTimeMillis()
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
        }

        // Regra de ouro: se a hora escolhida já passou hoje, agendamos para amanhã!
        if (calendar.timeInMillis <= System.currentTimeMillis()) {
            calendar.add(Calendar.DAY_OF_YEAR, 1)
        }

        // Agendamos o alarme!
        // O "RTC_WAKEUP" garante que o telemóvel acorda (mesmo com ecrã desligado) para disparar o alarme
        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            pendingIntent
        )
    }
}