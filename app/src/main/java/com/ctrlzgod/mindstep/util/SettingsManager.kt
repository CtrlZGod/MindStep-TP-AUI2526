package com.ctrlzgod.mindstep.util

import android.content.Context

/**
 * Guarda as preferências do utilizador em SharedPreferences,
 * para sobreviverem ao fecho da aplicação.
 */
class SettingsManager(context: Context) {

    private val prefs =
        context.getSharedPreferences("mindstep_settings", Context.MODE_PRIVATE)

    var reduceAnimations: Boolean
        get() = prefs.getBoolean(KEY_REDUCE_ANIMATIONS, false)
        set(value) { prefs.edit().putBoolean(KEY_REDUCE_ANIMATIONS, value).apply() }

    var voiceFeedback: Boolean
        get() = prefs.getBoolean(KEY_VOICE_FEEDBACK, false)
        set(value) { prefs.edit().putBoolean(KEY_VOICE_FEEDBACK, value).apply() }

    private companion object {
        const val KEY_REDUCE_ANIMATIONS = "reduce_animations"
        const val KEY_VOICE_FEEDBACK = "voice_feedback"
    }
}
