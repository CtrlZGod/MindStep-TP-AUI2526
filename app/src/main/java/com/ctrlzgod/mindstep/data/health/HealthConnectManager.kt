package com.ctrlzgod.mindstep.data.health

import android.content.Context
import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.permission.HealthPermission
import androidx.health.connect.client.records.SleepSessionRecord
import androidx.health.connect.client.records.StepsRecord
import androidx.health.connect.client.request.ReadRecordsRequest
import androidx.health.connect.client.time.TimeRangeFilter
import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit

class HealthConnectManager(private val context: Context) {

    val healthConnectClient by lazy { HealthConnectClient.getOrCreate(context) }

    val permissions = setOf(
        HealthPermission.getReadPermission(StepsRecord::class),
        HealthPermission.getReadPermission(SleepSessionRecord::class)
    )

    fun isAvailable(): Boolean {
        return HealthConnectClient.getSdkStatus(context, "com.google.android.apps.healthdata") == HealthConnectClient.SDK_AVAILABLE
    }

    suspend fun readStepsToday(): Long {
        return try {
            // 1. define o periodo - desde as 00:00 até à hora atual
            val startOfDay = ZonedDateTime.now().truncatedTo(ChronoUnit.DAYS).toInstant()
            val now = ZonedDateTime.now().toInstant()

            val timeRangeFilter = TimeRangeFilter.between(startOfDay, now)

            // 2. pedido à googl
            val response = healthConnectClient.readRecords(
                ReadRecordsRequest(
                    recordType = StepsRecord::class,
                    timeRangeFilter = timeRangeFilter
                )
            )

            // 3. soma os passos
            var totalSteps = 0L
            for (record in response.records) {
                totalSteps += record.count
            }

            totalSteps
        } catch (e: Exception) {
            // caso falhe, retorna e não crasha
            0L
        }
    }
}