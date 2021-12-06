package br.com.lucas.pomodoroapp.core.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.DrawableRes
import androidx.core.app.NotificationCompat
import br.com.lucas.pomodoroapp.R

private const val NOTIFICATION_ID = 0

fun NotificationManager.sendNotification(
    context: Context,
    messageBody: String,
    contentTitle: String,
    @DrawableRes smallIcon: Int,
    contentIntent: Intent,
    channelId: String,
    channelName: String,
    channelDescription: String
) {

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val notificationChannel = NotificationChannel(
            channelId,
            channelName,
            NotificationManager.IMPORTANCE_HIGH
        )

        notificationChannel.enableLights(true)
        notificationChannel.enableVibration(true)
        notificationChannel.description = channelDescription

        this.createNotificationChannel(notificationChannel)
    }

    val contentPendingIntent = PendingIntent.getActivity(
        context,
        NOTIFICATION_ID,
        contentIntent,
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        } else {
            PendingIntent.FLAG_UPDATE_CURRENT
        }
    )

    val builder = NotificationCompat.Builder(
        context,
        context.getString(R.string.pomdoro_notification_channel_id)
    )
        .setSmallIcon(smallIcon)
        .setContentTitle(contentTitle)
        .setContentText(messageBody)
        .setContentIntent(contentPendingIntent)
        .setAutoCancel(true)
        .setPriority(NotificationCompat.PRIORITY_MAX)

    notify(NOTIFICATION_ID, builder.build())
}