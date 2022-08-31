package br.com.lucas.pomodoroapp.core.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.DrawableRes
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import br.com.lucas.pomodoroapp.R
import br.com.lucas.pomodoroapp.ui.listTaskScreen.ListTaskActivity

fun showNotification(
    context: Context,
    contentTitle: String,
    messageBody: String,
    channelId: String,
    channelName: String,
    notificationID: Int,
    notificationCategory: String,
    notificationPriority: Int = NotificationCompat.PRIORITY_DEFAULT,
    contentIntent: Intent = Intent(context, ListTaskActivity::class.java),
    @DrawableRes smallIcon: Int = R.drawable.ic_pomodoro,
) {
    val notificationBuilder = getNotificationBuilder(
        context,
        contentTitle,
        messageBody,
        channelId,
        channelName,
        notificationID,
        notificationCategory,
        notificationPriority,
        contentIntent,
        smallIcon
    )
    val notificationManager = getNotificationManager(context)
    notificationManager?.notify(notificationID, notificationBuilder.build())
}

fun getNotificationManager(context: Context) =
    ContextCompat.getSystemService(
        context,
        NotificationManager::class.java
    )

fun getNotificationBuilder(
    context: Context,
    contentTitle: String,
    messageBody: String,
    channelId: String,
    channelName: String,
    notificationID: Int,
    notificationCategory: String,
    notificationPriority: Int = NotificationCompat.PRIORITY_DEFAULT,
    contentIntent: Intent = Intent(context, ListTaskActivity::class.java),
    @DrawableRes smallIcon: Int = R.drawable.ic_pomodoro,
): NotificationCompat.Builder {
    val updatedChannelId = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        getNotificationChannel(context, channelId, channelName) else ""

    val contentPendingIntent = PendingIntent.getActivity(
        context,
        notificationID,
        contentIntent,
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        } else {
            PendingIntent.FLAG_UPDATE_CURRENT
        }
    )

    return NotificationCompat.Builder(
        context,
        updatedChannelId
    )
        .setSmallIcon(smallIcon)
        .setContentTitle(contentTitle)
        .setContentText(messageBody)
        .setContentIntent(contentPendingIntent)
        .setCategory(notificationCategory)
        .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
        .setPriority(notificationPriority)
}

@RequiresApi(Build.VERSION_CODES.O)
private fun getNotificationChannel(
    context: Context,
    channelId: String,
    channelName: String,
): String {
    val channel = NotificationChannel(
        channelId,
        channelName, NotificationManager.IMPORTANCE_NONE
    )
    val service = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    service.createNotificationChannel(channel)
    return channelId
}