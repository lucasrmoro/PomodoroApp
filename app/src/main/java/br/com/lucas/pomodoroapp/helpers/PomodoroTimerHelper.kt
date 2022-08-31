package br.com.lucas.pomodoroapp.helpers

import android.app.Notification
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import br.com.lucas.pomodoroapp.R
import br.com.lucas.pomodoroapp.core.services.CountdownTimerService
import br.com.lucas.pomodoroapp.core.utils.showNotification
import br.com.lucas.pomodoroapp.database.model.Task
import br.com.lucas.pomodoroapp.helpers.PomodoroStep.*
import timber.log.Timber
import javax.inject.Inject

class PomodoroTimerHelper @Inject constructor(
    private val preferencesHelper: PreferencesHelper,
    private val context: Context,
) {

    var taskWithPomodoroTimerEnabled
        get() = preferencesHelper.taskWithPomodoroTimerEnabled
        private set(value) { preferencesHelper.taskWithPomodoroTimerEnabled = value }

    fun isPomodoroTimerEnabled(value: Boolean, task: Task? = null) {
        val serviceIntent = Intent(context, CountdownTimerService::class.java)
        if (value) {
            task?.let { taskWithPomodoroTimerEnabled = it }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                ContextCompat.startForegroundService(context, serviceIntent)
            } else {
                context.startService(serviceIntent)
            }
        } else {
            preferencesHelper.clearPomodoroTimerSpecs()
            ContextCompat.getSystemService(context, CountdownTimerService::class.java)?.apply {
                stopSelf()
                stopForeground(false)
            }
        }
    }

    fun syncPomodoroTimerSteps() {
        taskWithPomodoroTimerEnabled?.pomodoroDurations?.run {
            when (preferencesHelper.currentPomodoroTimerStep) {
                POMODORO_TIME -> {
                    if (preferencesHelper.pomodoroStepsCompleted < 4) {
                        preferencesHelper.currentPomodoroTimerStep = SHORT_BREAK
                    } else {
                        preferencesHelper.currentPomodoroTimerStep = LONG_BREAK
                    }
                    preferencesHelper.increasePomodoroStepsCompleted()
                }
                SHORT_BREAK -> {
                    if(preferencesHelper.shortBreaksCompleted < 4) {
                        preferencesHelper.currentPomodoroTimerStep = POMODORO_TIME
                    } else {
                        preferencesHelper.currentPomodoroTimerStep = FINISHED
                    }
                    preferencesHelper.increaseShortBreaksCompleted()
                }
                LONG_BREAK -> {
                    if (preferencesHelper.pomodoroCyclesCompleted < numberOfCycles) {
                        preferencesHelper.currentPomodoroTimerStep = POMODORO_TIME
                        preferencesHelper.increasePomodoroCyclesCompleted()
                        preferencesHelper.clearPomodoroStepsStats()
                    } else {
                        preferencesHelper.currentPomodoroTimerStep = FINISHED
                    }
                }
                else -> {}
            }
        }
        Timber.d("Current Pomodoro Timer Step : ${preferencesHelper.currentPomodoroTimerStep}")
        Timber.d("Pomodoro Timer Steps Completed ${preferencesHelper.pomodoroStepsCompleted}")
        Timber.d("Short Breaks Completed ${preferencesHelper.shortBreaksCompleted}")
        Timber.d("Pomodoro Cycles Completed ${preferencesHelper.pomodoroCyclesCompleted}")
        if (preferencesHelper.currentPomodoroTimerStep != FINISHED) {
            startCountdownTimerService()
        } else {
            preferencesHelper.clearPomodoroTimerSpecs()
            showPomodoroTimerFinishedNotification()
        }
    }

    private fun startCountdownTimerService() {
        val serviceIntent = Intent(context, CountdownTimerService::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(serviceIntent)
        } else {
            context.startService(serviceIntent)
        }
    }

    private fun showPomodoroTimerFinishedNotification() {
        showNotification(
            context = context,
            contentTitle = "Pomodoro Timer",
            messageBody = "Parabéns você completou terminou acabou o Pomodoro Timer",
            channelId = context.getString(R.string.pomodoro_notification_channel_id),
            channelName = context.getString(R.string.pomodoro_notification_channel_name),
            notificationID = CountdownTimerService.COUNTDOWN_NOTIFICATION_ID,
            notificationCategory = Notification.CATEGORY_EVENT,
            notificationPriority = NotificationCompat.PRIORITY_MAX
        )
    }
}