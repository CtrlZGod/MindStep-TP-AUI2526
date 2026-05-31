package com.ctrlzgod.mindstep.util

import android.content.Context
import android.speech.tts.TextToSpeech
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import java.util.Locale

/**
 * Locução (Text-to-Speech). Permite à aplicação "falar" em voz alta —
 * usado para ler notas, anunciar ações e ler a tendência do humor,
 * apoiando utilizadores com deficiência visual mesmo sem o TalkBack ativo.
 */
class TtsController(context: Context) {

    private var ready = false
    private var tts: TextToSpeech? = null

    init {
        tts = TextToSpeech(context.applicationContext) { status ->
            if (status == TextToSpeech.SUCCESS) {
                tts?.language = Locale("pt", "PT")
                ready = true
            }
        }
    }

    fun speak(text: String) {
        if (ready && text.isNotBlank()) {
            tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "mindstep_utterance")
        }
    }

    fun shutdown() {
        tts?.stop()
        tts?.shutdown()
        tts = null
    }
}

/**
 * Cria um TtsController ligado ao ciclo de vida da composição,
 * libertando os recursos do motor de voz quando deixa de ser necessário.
 */
@Composable
fun rememberTtsController(): TtsController {
    val context = LocalContext.current
    val controller = remember { TtsController(context) }
    DisposableEffect(Unit) {
        onDispose { controller.shutdown() }
    }
    return controller
}
