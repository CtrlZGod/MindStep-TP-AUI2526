package com.ctrlzgod.mindstep.util

import android.content.Context
import android.content.Intent
import com.ctrlzgod.mindstep.data.local.MoodRecord
import com.ctrlzgod.mindstep.ui.components.buildMoodTrendDescription
import com.ctrlzgod.mindstep.ui.components.computeWeeklyMood
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Exportação de dados (requisito funcional do enunciado):
 * gera um resumo textual dos registos para partilha com profissionais de saúde,
 * usando o sistema de partilha do Android (ACTION_SEND).
 */
object ExportHelper {

    private val PT = Locale("pt", "PT")

    fun buildSummary(records: List<MoodRecord>): String {
        val fmt = SimpleDateFormat("dd/MM/yyyy HH:mm", PT)
        val sb = StringBuilder()

        sb.appendLine("MindStep — Resumo do Diário de Bem-Estar")
        sb.appendLine("Gerado em: ${fmt.format(Date())}")
        sb.appendLine("Total de registos: ${records.size}")
        sb.appendLine()

        if (records.isNotEmpty()) {
            val weekly = computeWeeklyMood(records)
            sb.appendLine("Tendência da última semana:")
            sb.appendLine(buildMoodTrendDescription(weekly))
            sb.appendLine()
            sb.appendLine("Registos (mais recentes primeiro):")
            records.forEach { r ->
                val base = "- ${fmt.format(Date(r.timestamp))} | Humor ${r.moodLevel}/5 | Ansiedade ${r.anxietyLevel}/5"
                sb.appendLine(if (!r.notes.isNullOrBlank()) "$base | Notas: ${r.notes}" else base)
            }
        } else {
            sb.appendLine("Ainda não existem registos no diário.")
        }

        return sb.toString()
    }

    fun share(context: Context, records: List<MoodRecord>) {
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_SUBJECT, "Resumo MindStep")
            putExtra(Intent.EXTRA_TEXT, buildSummary(records))
        }
        context.startActivity(Intent.createChooser(intent, "Partilhar resumo"))
    }
}
