package br.com.lucas.pomodoroapp.core.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        val taskName = intent?.getStringExtra(TASK_NAME) ?: ""
        Log.d(TAG, "$taskName should finishes")

        // TODO - AlarmFeature: Create a notification showing the task has been finished

        // TODO - AlarmFeature: Notification tapping should take the user to the Main Screen
    }

    companion object {
        const val TAG = "AlarmReceiver"
        const val TASK_NAME = "TASK NAME KEY"
    }
}