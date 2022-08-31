package br.com.lucas.pomodoroapp.core.services

import android.app.Notification
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Binder
import android.os.CountDownTimer
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import br.com.lucas.pomodoroapp.R
import br.com.lucas.pomodoroapp.core.extensions.toMinutesAndSeconds
import br.com.lucas.pomodoroapp.core.receiver.CountdownTimerReceiver
import br.com.lucas.pomodoroapp.core.utils.getNotificationBuilder
import br.com.lucas.pomodoroapp.core.utils.getNotificationManager
import br.com.lucas.pomodoroapp.helpers.PomodoroStep.*
import br.com.lucas.pomodoroapp.helpers.PreferencesHelper
import dagger.hilt.android.AndroidEntryPoint
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@AndroidEntryPoint
class CountdownTimerService : Service() {

    @Inject
    lateinit var context: Context

    @Inject
    lateinit var preferencesHelper: PreferencesHelper

    private var timer: CountDownTimer? = null
    private val notificationManager by lazy { getNotificationManager(context) }
    private val notificationBuilder by lazy { getNotificationBuilder(
        context = context,
        messageBody = context.getString(R.string.label_pomodoro_timer),
        contentTitle = "",
        channelId = context.getString(R.string.pomodoro_notification_channel_id),
        channelName = context.getString(R.string.pomodoro_notification_channel_name),
        notificationID = COUNTDOWN_NOTIFICATION_ID,
        notificationCategory = Notification.CATEGORY_SERVICE,
        notificationPriority = NotificationCompat.PRIORITY_MAX
    ) }

    override fun onCreate() {
        super.onCreate()
        LocalBroadcastManager.getInstance(context)
            .registerReceiver(CountdownTimerReceiver(),
                IntentFilter(CountdownTimerReceiver.COUNTDOWN_BROADCAST_INTENT_ACTION))
    }

    override fun onBind(intent: Intent?): IBinder = LocalBinder()

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        startForegroundCountdown()
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
        timer?.cancel()
        super.onDestroy()
    }

    private fun startForegroundCountdown() {
        val pomodoroDurations = preferencesHelper.taskWithPomodoroTimerEnabled?.pomodoroDurations
        val countdownTimeLength = when(preferencesHelper.currentPomodoroTimerStep) {
            POMODORO_TIME -> 10
            SHORT_BREAK -> 5
            LONG_BREAK -> 8
            else -> null
        }
        val timeInMillis = countdownTimeLength?.let { TimeUnit.SECONDS.toMillis(it.toLong()) }
        timer = timeInMillis?.let { initCountdownTimer(it) }
        startForeground(COUNTDOWN_NOTIFICATION_ID, notificationBuilder.build())
    }

    private fun initCountdownTimer(
        countdownTimeLength: Long,
        countdownInterval: Long = ONE_SECOND_IN_MILLIS,
    ): CountDownTimer {
        return object : CountDownTimer(countdownTimeLength, countdownInterval) {
            override fun onTick(remainingMillis: Long) {
                val builder = notificationBuilder.apply { setContentTitle(remainingMillis.toMinutesAndSeconds()) }
                notificationManager?.notify(COUNTDOWN_NOTIFICATION_ID, builder.build())
            }

            override fun onFinish() {
                stopForeground(false)
                stopService(Intent(context, this@CountdownTimerService::class.java))
                stopSelf()
                val countdownFinishIntent = Intent(CountdownTimerReceiver.COUNTDOWN_BROADCAST_INTENT_ACTION)
                LocalBroadcastManager.getInstance(context)
                    .sendBroadcast(countdownFinishIntent)
            }
        }.start()
    }

    inner class LocalBinder : Binder() {
        fun getService(): CountdownTimerService = this@CountdownTimerService
    }

    companion object {
        const val COUNTDOWN_NOTIFICATION_ID = 101
        private const val ONE_SECOND_IN_MILLIS = 1000.toLong()
    }
}