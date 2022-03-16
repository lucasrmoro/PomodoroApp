package br.com.lucas.pomodoroapp.mediators

import br.com.lucas.pomodoroapp.helpers.AlarmManagerHelper
import br.com.lucas.pomodoroapp.helpers.PreferencesHelper
import javax.inject.Inject

class AlarmMediator @Inject constructor(
    private val alarmManagerHelper: AlarmManagerHelper,
    private val preferencesHelper: PreferencesHelper,
) {
    val taskTimerEnabled: Int
        get() = preferencesHelper.taskTimerEnabled

    fun syncTaskTimer(
        isTimerEnabled: Boolean,
        taskId: Int,
        alarmTime: Int,
    ) {
        if (isTimerEnabled) {
            alarmManagerHelper.setAlarm(alarmTime){
                preferencesHelper.saveTaskTimer(taskId)
            }
        } else {
            preferencesHelper.deleteTaskTimer()
            alarmManagerHelper.cancelAlarm()
        }
    }
}