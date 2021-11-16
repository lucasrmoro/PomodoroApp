package br.com.lucas.pomodoroapp.core.utils

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import br.com.lucas.pomodoroapp.R
import br.com.lucas.pomodoroapp.ui.listTaskScreen.ListTaskActivity

private const val NOTIFICATION_ID = 0

fun NotificationManager.sendNotification(messageBody: String, context: Context){

    val contentIntent = Intent(context, ListTaskActivity::class.java)
    val contentPendingIntent = PendingIntent.getActivity(
        context,
        NOTIFICATION_ID,
        contentIntent,
        PendingIntent.FLAG_UPDATE_CURRENT
    )

    val builder = NotificationCompat.Builder(
        context,
        context.getString(R.string.pomdoro_notification_channel_id)
    )
        .setSmallIcon(R.drawable.ic_pomodoro)
        .setContentTitle(context.getString(R.string.label_pomodoro_timer))
        .setContentText(messageBody)
        .setContentIntent(contentPendingIntent)
        .setAutoCancel(true)
        .setPriority(NotificationCompat.PRIORITY_MAX)

    notify(NOTIFICATION_ID, builder.build())
}