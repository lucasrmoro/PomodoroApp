package br.com.lucas.pomodoroapp.helpers

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.SystemClock
import androidx.core.app.AlarmManagerCompat
import br.com.lucas.pomodoroapp.core.receiver.AlarmReceiver
import dagger.hilt.android.internal.Contexts.getApplication
import java.util.concurrent.TimeUnit

class AlarmManagerHelper(private val context: Context) {
    private val alarmManager: AlarmManager? =
        context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager

    fun setExactAlarm(
        alarmTime: Int
    ) {
        alarmManager?.let { manager ->
            AlarmManagerCompat.setExactAndAllowWhileIdle(
                manager,
                AlarmManager.ELAPSED_REALTIME_WAKEUP,
                SystemClock.elapsedRealtime() +
                        TimeUnit.MINUTES.toMillis(alarmTime * 1L),
                getPendingIntent(alarmTime)
            )
        }
    }

    private fun getPendingIntent(
        alarmTime: Int
    ): PendingIntent {
        val notifyIntent = Intent(context, AlarmReceiver::class.java).apply {
            putExtra(AlarmReceiver.TASK_NAME, alarmTime)
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