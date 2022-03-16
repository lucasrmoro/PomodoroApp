package br.com.lucas.pomodoroapp.helpers

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.SystemClock
import android.provider.Settings
import androidx.core.app.AlarmManagerCompat
import br.com.lucas.pomodoroapp.core.receiver.AlarmReceiver
import java.util.concurrent.TimeUnit

class AlarmManagerHelper(
    private val context: Context,
    private val requestAlarmPermissionIntent: Intent = Intent().apply {
        action = Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM
    },
) {
    private val alarmManager: AlarmManager? =
        context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager

    private val pendingIntent: PendingIntent?
        get() = PendingIntent.getBroadcast(
            context,
            BROADCAST_REQUEST_CODE,
            Intent(context, AlarmReceiver::class.java),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )


    fun setAlarm(alarmTime: Int, saveTaskTimer: () -> Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (alarmManager?.canScheduleExactAlarms() == true) {
                scheduleAlarm(alarmTime)
                saveTaskTimer()
            } else {
                context.startActivity(requestAlarmPermissionIntent)
            }
        } else {
            scheduleAlarm(alarmTime)
            saveTaskTimer()
        }
    }

    fun cancelAlarm() {
        alarmManager?.cancel(pendingIntent)
    }

    private fun scheduleAlarm(
        alarmTime: Int,
    ) {
        alarmManager?.let { manager ->
            AlarmManagerCompat.setExactAndAllowWhileIdle(
                manager,
                AlarmManager.ELAPSED_REALTIME_WAKEUP,
                SystemClock.elapsedRealtime() +
                        TimeUnit.MINUTES.toMillis(alarmTime * 1L),
                pendingIntent ?: return
            )
        }
    }

    companion object {
        private const val BROADCAST_REQUEST_CODE = 123
    }
}