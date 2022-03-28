package br.com.lucas.pomodoroapp.core.services

import android.app.Notification
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.content.IntentFilter
import android.os.Binder
import android.os.CountDownTimer
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import br.com.lucas.pomodoroapp.R
import br.com.lucas.pomodoroapp.core.extensions.toMinutesAndSeconds
import br.com.lucas.pomodoroapp.core.receiver.CountdownTimerReceiver
import br.com.lucas.pomodoroapp.core.utils.notification.getNotificationBuilder
import java.util.concurrent.TimeUnit


class CountdownTimerService : Service() {

    private lateinit var timer: CountDownTimer
    private lateinit var notificationManager: NotificationManager
    private lateinit var notificationBuilder: NotificationCompat.Builder

    override fun onCreate() {
        super.onCreate()
        setupNotificationVariables()
        LocalBroadcastManager.getInstance(this)
            .registerReceiver(CountdownTimerReceiver(),
                IntentFilter(CountdownTimerReceiver.COUNTDOWN_TIMER_FINISH))
    }

    override fun onBind(intent: Intent?): IBinder = LocalBinder()

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        val countdownTimeLength = intent.getIntExtra(COUNTDOWN_TIME_LENGTH, 25)
        val updatedTimeLength = TimeUnit.MINUTES.toMillis(countdownTimeLength.toLong())
        startForegroundCountdown(updatedTimeLength)
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
        timer.cancel()
        super.onDestroy()
    }

    private fun startForegroundCountdown(countdownTimeLength: Long) {
        timer = getCountdownTimer(countdownTimeLength).start()
        startForeground(COUNTDOWN_NOTIFICATION_ID, notificationBuilder.build())
    }

    private fun getCountdownTimer(
        countdownTimeLength: Long,
        countdownInterval: Long = 1000,
    ): CountDownTimer {
        return object : CountDownTimer(countdownTimeLength, countdownInterval) {
            override fun onTick(remainingMillis: Long) {
                notificationManager.notify(COUNTDOWN_NOTIFICATION_ID,
                    notificationBuilder
                        .setContentTitle(remainingMillis.toMinutesAndSeconds())
                        .build())
            }

            override fun onFinish() {
                stopSelf()
                val countdownFinishIntent = Intent(CountdownTimerReceiver.COUNTDOWN_TIMER_FINISH)
                LocalBroadcastManager.getInstance(this@CountdownTimerService)
                    .sendBroadcast(countdownFinishIntent)
            }
        }.start()
    }

    private fun setupNotificationVariables() {
        notificationManager = ContextCompat.getSystemService(
            this,
            NotificationManager::class.java
        ) as NotificationManager
        notificationBuilder = getNotificationBuilder(
            context = this,
            messageBody = getString(R.string.label_pomodoro_timer),
            contentTitle = "",
            channelId = getString(R.string.pomodoro_notification_channel_id),
            channelName = getString(R.string.pomodoro_notification_channel_name),
            notificationID = COUNTDOWN_NOTIFICATION_ID,
            notificationCategory = Notification.CATEGORY_SERVICE,
            notificationPriority = NotificationCompat.PRIORITY_MAX
        )
    }

    inner class LocalBinder : Binder() {
        fun getService(): CountdownTimerService = this@CountdownTimerService
    }

    companion object {
        const val COUNTDOWN_TIME_LENGTH = "Countdown time length according task step time"
        private const val COUNTDOWN_NOTIFICATION_ID = 101
    }
}