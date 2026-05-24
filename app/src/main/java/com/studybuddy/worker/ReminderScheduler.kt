package com.studybuddy.worker

import android.content.Context
import androidx.work.*
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ReminderScheduler @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val workManager = WorkManager.getInstance(context)

    fun scheduleReminder(taskId: Int, dueDateMillis: Long, dueTime: String, reminderMinutes: Int) {
        // Parse time to get total millis
        val parts = dueTime.split(":")
        if (parts.size != 2) return
        
        val hour = parts[0].toIntOrNull() ?: 0
        val minute = parts[1].toIntOrNull() ?: 0
        
        val targetTime = dueDateMillis + (hour * 3600000L) + (minute * 60000L)
        val reminderTimeMillis = targetTime - (reminderMinutes * 60 * 1000L)
        val delayMillis = reminderTimeMillis - System.currentTimeMillis()

        if (delayMillis <= 0) return

        val data = Data.Builder()
            .putInt("taskId", taskId)
            .build()

        val workRequest = OneTimeWorkRequestBuilder<ReminderWorker>()
            .setInitialDelay(delayMillis, TimeUnit.MILLISECONDS)
            .setInputData(data)
            .addTag("reminder_$taskId")
            .build()

        workManager.enqueueUniqueWork(
            "reminder_$taskId",
            ExistingWorkPolicy.REPLACE,
            workRequest
        )
    }

    fun cancelReminder(taskId: Int) {
        workManager.cancelUniqueWork("reminder_$taskId")
    }
}
