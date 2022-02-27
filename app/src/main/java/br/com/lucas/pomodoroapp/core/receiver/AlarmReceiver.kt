package br.com.lucas.pomodoroapp.core.receiver

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat
import br.com.lucas.pomodoroapp.R
import br.com.lucas.pomodoroapp.core.utils.sendNotification
import br.com.lucas.pomodoroapp.ui.listTaskScreen.ListTaskActivity
import timber.log.Timber

class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val taskName = intent.getStringExtra(TASK_NAME) ?: ""
        Timber.tag(TAG).d("$taskName should finishes")

        val notificationManager = ContextCompat.getSystemService(
            context,
            NotificationManager::class.java
        ) as NotificationManager

        val launchListActivity = Intent(context, ListTaskActivity::class.java)
        launchListActivity.flags = Intent.FLAG_ACTIVITY_NEW_TASK

        notificationManager.sendNotification(
            context,
            context.getString(R.string.time_to_do_a_break),
            context.getString(R.string.label_pomodoro_timer),
            R.drawable.ic_pomodoro,
            launchListActivity,
            context.getString(R.string.pomdoro_notification_channel_id),
            context.getString(R.string.pomodoro_notification_channel_name),
            context.getString(R.string.pomodoro_timer_reminder)
        )
    }

    companion object {
        const val TAG = "AlarmReceiver"
        const val TASK_NAME = "TASK NAME KEY"
    }
}