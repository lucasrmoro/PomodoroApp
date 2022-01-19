package br.com.lucas.pomodoroapp.helpers

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.SystemClock
import androidx.core.app.AlarmManagerCompat
import br.com.lucas.pomodoroapp.core.receiver.AlarmReceiver
import br.com.lucas.pomodoroapp.database.Task
import dagger.hilt.android.internal.Contexts.getApplication
import java.util.concurrent.TimeUnit

class AlarmManagerHelper(private val context: Context) {
    private val alarmManager: AlarmManager? =
        context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager

    fun setExactAlarm(
        task: Task
    ) {
        alarmManager?.let { manager ->
            AlarmManagerCompat.setExactAndAllowWhileIdle(
                manager,
                AlarmManager.ELAPSED_REALTIME_WAKEUP,
                SystemClock.elapsedRealtime() +
                        TimeUnit.MINUTES.toMillis(task.taskMinutes * 1L),
                getPendingIntent(task)
            )
        }
    }

    private fun getPendingIntent(
        task: Task
    ): PendingIntent {
        val notifyIntent = Intent(context, AlarmReceiver::class.java).apply {
            putExtra(AlarmReceiver.TASK_NAME, task.taskName)
        }

        return PendingIntent.getBroadcast(
            getApplication(context),
            BROADCAST_REQUEST_CODE,
            notifyIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )
    }

    companion object {
        private const val BROADCAST_REQUEST_CODE = 123
    }
}