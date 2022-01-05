package br.com.lucas.pomodoroapp.helpers

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.SystemClock
import androidx.core.app.AlarmManagerCompat
import dagger.hilt.android.internal.Contexts.getApplication
import java.util.concurrent.TimeUnit

class AlarmManagerHelper(private val context: Context) {
    fun setExactAlarm(
        broadcastReceiverClass: Class<*>,
        broadcastRequestCode: Int,
        putExtraKey: String,
        putExtraValue: String,
        time: Int
    ) {
        buildAlarmManager()?.let { manager ->
            AlarmManagerCompat.setExactAndAllowWhileIdle(
                manager,
                AlarmManager.ELAPSED_REALTIME_WAKEUP,
                SystemClock.elapsedRealtime() +
                        TimeUnit.MINUTES.toMillis(1L),
                getPendingIntent(
                    broadcastReceiverClass,
                    broadcastRequestCode,
                    putExtraKey,
                    putExtraValue)
            )
        }
    }

    private fun getPendingIntent(
        broadcastReceiverClass: Class<*>,
        broadcastRequestCode: Int,
        putExtraKey: String,
        putExtraValue: String
    ): PendingIntent {
        val notifyIntent = Intent(context, broadcastReceiverClass)

        return PendingIntent.getBroadcast(
            getApplication(context),
            broadcastRequestCode,
            notifyIntent.putExtra(putExtraKey, putExtraValue),
            PendingIntent.FLAG_UPDATE_CURRENT
        )
    }

    private fun buildAlarmManager(): AlarmManager? =
        context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager
}