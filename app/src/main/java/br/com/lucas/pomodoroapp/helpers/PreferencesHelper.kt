package br.com.lucas.pomodoroapp.helpers

import android.content.Context
import android.content.SharedPreferences
import javax.inject.Inject

class PreferencesHelper @Inject constructor(
    val context: Context,
) {
    private val currentPrefs: SharedPreferences = context.getSharedPreferences(
        MY_PREFS, Context.MODE_PRIVATE)

    val taskTimerEnabled: Int
        get() = currentPrefs.getInt(ACTIVE_POMODORO_TIMER, -1)

    fun saveTaskTimer(taskId: Int) {
        currentPrefs.edit().putInt(ACTIVE_POMODORO_TIMER, taskId).apply()
    }

    fun deleteTaskTimer() {
        currentPrefs.edit().remove(ACTIVE_POMODORO_TIMER).apply()
    }

    companion object {
        const val MY_PREFS = "myPrefs"
        const val ACTIVE_POMODORO_TIMER = "Active Pomodoro Timer"
    }
}