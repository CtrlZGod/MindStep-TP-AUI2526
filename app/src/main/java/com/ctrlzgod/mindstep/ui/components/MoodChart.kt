package com.ctrlzgod.mindstep.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.ctrlzgod.mindstep.data.local.MoodRecord
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.TextStyle
import java.util.Locale
import kotlin.math.abs
import kotlin.math.roundToInt

/**
 * Representa a média de humor de um dia da janela semanal.
 * avgMood = Float.NaN quando não existem registos nesse dia.
 */
data class DailyMood(val date: LocalDate, val label: String, val avgMood: Float)

private val PT = Locale("pt", "PT")

/**
 * Agrega os registos dos últimos 7 dias (incluindo hoje) por dia,
 * calculando a média do nível de humor (1 a 5) de cada dia.
 * Devolve sempre 7 entradas (uma por dia), com NaN nos dias sem registos.
 */
fun computeWeeklyMood(
    records: List<MoodRecord>,
    zone: ZoneId = ZoneId.systemDefault()
): List<DailyMood> {
    val today = LocalDate.now(zone)
    val firstDay = today.minusDays(6)

    val byDate = records
        .map { it to Instant.ofEpochMilli(it.timestamp).atZone(zone).toLocalDate() }
        .filter { (_, d) -> !d.isBefore(firstDay) && !d.isAfter(today) }
        .groupBy { (_, d) -> d }

    return (0..6).map { offset ->
        val date = firstDay.plusDays(offset.toLong())
        val recs = byDate[date]?.map { it.first } ?: emptyList()
        val avg = if (recs.isEmpty()) Float.NaN
        else recs.map { it.moodLevel }.average().toFloat()
        DailyMood(
            date = date,
            label = date.dayOfWeek.getDisplayName(TextStyle.SHORT, PT),
            avgMood = avg
        )
    }
}

private fun moodWord(v: Float): String = when {
    v < 1.5f -> "muito baixo"
    v < 2.5f -> "baixo"
    v < 3.5f -> "neutro"
    v < 4.5f -> "bom"
    else -> "muito bom"
}

/**
 * Constrói a descrição textual dinâmica da tendência do gráfico.
 * É esta string que é lida pelos leitores de ecrã (TalkBack) e mostrada
 * por baixo do gráfico, tornando a informação visual acessível.
 */
fun buildMoodTrendDescription(daily: List<DailyMood>): String {
    val valid = daily.filter { !it.avgMood.isNaN() }
    if (valid.size < 2) {
        return "Ainda não há registos suficientes esta semana para calcular uma tendência. " +
            "Regista o teu humor durante mais alguns dias."
    }
    val first = valid.first().avgMood
    val last = valid.last().avgMood
    val pct = if (first != 0f) ((last - first) / first * 100f).roundToInt() else 0
    val avgAll = valid.map { it.avgMood }.average().toFloat()
    val media = String.format(PT, "%.1f", avgAll)
    val word = moodWord(avgAll)

    return when {
        abs(pct) < 5 ->
            "Esta semana o teu humor manteve-se estável, em média $word ($media em 5)."
        pct > 0 ->
            "Esta semana o teu humor subiu cerca de $pct%, terminando mais positivo. " +
                "Média da semana: $word ($media em 5)."
        else ->
            "Esta semana o teu humor desceu cerca de ${-pct}%. " +
                "Média da semana: $word ($media em 5). Talvez seja boa altura para abrandar e cuidar de ti."
    }
}

/**
 * Gráfico de linha do humor dos últimos 7 dias, desenhado em Jetpack Compose Canvas.
 *
 * Acessibilidade: o Canvas recebe uma contentDescription com a tendência resumida,
 * e a mesma descrição é apresentada como texto visível por baixo do gráfico
 * (requisito "Gráficos Acessíveis" do enunciado).
 */
@Composable
fun MoodChart(
    records: List<MoodRecord>,
    modifier: Modifier = Modifier
) {
    val daily = remember(records) { computeWeeklyMood(records) }
    val description = remember(daily) { buildMoodTrendDescription(daily) }

    val lineColor = MaterialTheme.colorScheme.primary
    val gridColor = MaterialTheme.colorScheme.outlineVariant
    val onSurfaceVariant = MaterialTheme.colorScheme.onSurfaceVariant

    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = "Tendência do humor (últimos 7 dias)",
            style = MaterialTheme.typography.titleMedium,
            color = onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(8.dp))

        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(160.dp)
                .padding(vertical = 8.dp)
                .semantics { contentDescription = description }
        ) {
            val w = size.width
            val h = size.height
            val minMood = 1f
            val maxMood = 5f
            val n = daily.size

            // Linhas de grelha horizontais para os níveis 1..5
            for (level in 1..5) {
                val y = h - (level - minMood) / (maxMood - minMood) * h
                drawLine(
                    color = gridColor,
                    start = Offset(0f, y),
                    end = Offset(w, y),
                    strokeWidth = 1f,
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(6f, 6f), 0f)
                )
            }

            val points = daily.mapIndexedNotNull { i, d ->
                if (d.avgMood.isNaN()) {
                    null
                } else {
                    val x = if (n <= 1) w / 2f else w * i / (n - 1)
                    val y = h - (d.avgMood - minMood) / (maxMood - minMood) * h
                    Offset(x, y)
                }
            }

            for (i in 0 until points.size - 1) {
                drawLine(
                    color = lineColor,
                    start = points[i],
                    end = points[i + 1],
                    strokeWidth = 6f
                )
            }
            points.forEach { p ->
                drawCircle(color = lineColor, radius = 8f, center = p)
            }
        }

        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = description,
            style = MaterialTheme.typography.bodyMedium,
            color = onSurfaceVariant
        )
    }
}
