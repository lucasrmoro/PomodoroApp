package br.com.lucas.pomodoroapp.mediators

import android.content.Context
import android.content.Intent
import android.os.Build
import br.com.lucas.pomodoroapp.core.services.CountdownTimerService
import br.com.lucas.pomodoroapp.helpers.PreferencesHelper
import javax.inject.Inject

class AlarmMediator @Inject constructor(
    private val context: Context,
    private val preferencesHelper: PreferencesHelper,
) {
    val taskTimerEnabled: Int
        get() = preferencesHelper.taskTimerEnabled

    fun syncPomodoroCountdown(
        isTimerEnabled: Boolean,
        taskId: Int,
        stepTime: Int,
    ) {
        val serviceIntent = Intent(context, CountdownTimerService::class.java).apply {
            putExtra(CountdownTimerService.COUNTDOWN_TIME_LENGTH, stepTime)
        }
        if (isTimerEnabled) {
            if (preferencesHelper.taskTimerEnabled == -1) preferencesHelper.saveTaskTimer(taskId)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(serviceIntent)
            } else {
                context.startService(serviceIntent)
            }
        } else {
            preferencesHelper.deleteTaskTimer()
            context.stopService(serviceIntent)
        }
    }
}