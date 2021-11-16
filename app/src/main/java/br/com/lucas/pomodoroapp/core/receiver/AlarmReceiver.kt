package br.com.lucas.pomodoroapp.core.receiver

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import br.com.lucas.pomodoroapp.R
import br.com.lucas.pomodoroapp.core.utils.sendNotification
import br.com.lucas.pomodoroapp.ui.listTaskScreen.ListTaskActivity

class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val taskName = intent.getStringExtra(TASK_NAME) ?: ""
        Log.d(TAG, "$taskName should finishes")

        val notificationManager = ContextCompat.getSystemService(
            context,
            NotificationManager::class.java
        ) as NotificationManager

        notificationManager.sendNotification(
            "It's time to do a break!",
            context
        )
    }

    companion object {
        const val TAG = "AlarmReceiver"
        const val TASK_NAME = "TASK NAME KEY"
    }
}