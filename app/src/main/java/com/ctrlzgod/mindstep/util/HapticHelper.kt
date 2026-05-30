package com.ctrlzgod.mindstep.util

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager

/**
 * Centraliza o feedback háptico da aplicação.
 *
 * Requisito de acessibilidade do enunciado: "utilizar diferentes intensidades de
 * vibração para indicar níveis de ansiedade ou objetivos atingidos".
 *
 * A amplitude da vibração (1..255) é proporcional ao nível de ansiedade (1..5),
 * para que o utilizador "sinta" a intensidade sem precisar de olhar para o ecrã.
 */
object HapticHelper {

    private fun getVibrator(context: Context): Vibrator {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val manager =
                context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
            manager.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        }
    }

    /**
     * Vibra com intensidade proporcional ao nível de ansiedade.
     * Nível 1 → vibração curta e suave; nível 5 → vibração longa e forte.
     */
    fun vibrateForAnxiety(context: Context, level: Int) {
        val vibrator = getVibrator(context)
        if (!vibrator.hasVibrator()) return

        val clamped = level.coerceIn(1, 5)
        val amplitude = (clamped * 51).coerceIn(1, 255) // 51,102,153,204,255
        val duration = (30L + clamped * 30L)            // 60..180 ms

        if (vibrator.hasAmplitudeControl()) {
            vibrator.vibrate(VibrationEffect.createOneShot(duration, amplitude))
        } else {
            // Dispositivos sem controlo de amplitude: a duração transmite a intensidade.
            vibrator.vibrate(VibrationEffect.createOneShot(duration, VibrationEffect.DEFAULT_AMPLITUDE))
        }
    }

    /**
     * Padrão de vibração de sucesso (dois toques) para objetivos atingidos,
     * por exemplo ao guardar um registo do diário.
     */
    fun vibrateSuccess(context: Context) {
        val vibrator = getVibrator(context)
        if (!vibrator.hasVibrator()) return
        val timings = longArrayOf(0, 50, 80, 50)
        val amplitudes = intArrayOf(0, 180, 0, 255)
        if (vibrator.hasAmplitudeControl()) {
            vibrator.vibrate(VibrationEffect.createWaveform(timings, amplitudes, -1))
        } else {
            vibrator.vibrate(VibrationEffect.createWaveform(timings, -1))
        }
    }
}
