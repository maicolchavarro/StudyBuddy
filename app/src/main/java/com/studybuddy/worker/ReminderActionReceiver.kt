package com.studybuddy.worker

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationManagerCompat
import com.studybuddy.domain.usecase.CompleteTaskUseCase
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class ReminderActionReceiver : BroadcastReceiver() {

    @Inject
    lateinit var completeTaskUseCase: CompleteTaskUseCase

    override fun onReceive(context: Context, intent: Intent) {
        val taskId = intent.getIntExtra(EXTRA_TASK_ID, -1)
        if (taskId <= 0) return

        val pendingResult = goAsync()
        CoroutineScope(SupervisorJob() + Dispatchers.IO).launch {
            when (intent.action) {
                ACTION_COMPLETE -> completeTaskUseCase(taskId, true)
                ACTION_DISMISS -> Unit
            }
            NotificationManagerCompat.from(context).cancel(taskId)
            pendingResult.finish()
        }
    }

    companion object {
        const val ACTION_COMPLETE = "com.studybuddy.action.COMPLETE_TASK"
        const val ACTION_DISMISS = "com.studybuddy.action.DISMISS_TASK"
        const val EXTRA_TASK_ID = "extra_task_id"
    }
}
