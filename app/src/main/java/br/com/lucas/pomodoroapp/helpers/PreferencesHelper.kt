package br.com.lucas.pomodoroapp.helpers

import android.content.Context
import android.content.SharedPreferences
import br.com.lucas.pomodoroapp.database.model.Task
import com.google.gson.Gson
import javax.inject.Inject

class PreferencesHelper @Inject constructor(
    val context: Context,
) {
    private val currentPrefs: SharedPreferences = context.getSharedPreferences(
        MY_PREFS, Context.MODE_PRIVATE
    )

    var taskWithPomodoroTimerEnabled: Task?
        get() {
            val jsonTask = currentPrefs.getString(TASK_WITH_POMODORO_TIMER_ENABLED, null)
            return jsonTask?.let { Gson().fromJson(it, Task::class.java) }
        }
        set(value) {
            val jsonTask = Gson().toJson(value)
            currentPrefs.edit().putString(TASK_WITH_POMODORO_TIMER_ENABLED, jsonTask).apply()
        }

    var currentPomodoroTimerStep: PomodoroStep
        get() = PomodoroStep.getByValue(currentPrefs.getInt(CURRENT_POMODORO_TIMER_STEP, 1))
        set(pomodoroStep) {
            currentPrefs.edit().putInt(CURRENT_POMODORO_TIMER_STEP, pomodoroStep.value).apply()
        }

    val pomodoroStepsCompleted: Int
        get() = currentPrefs.getInt(POMODORO_STEPS_COMPLETED, 0)

    val shortBreaksCompleted: Int
        get() = currentPrefs.getInt(SHORT_BREAKS_COMPLETED, 0)

    val longBreaksCompleted: Int
        get() = currentPrefs.getInt(LONG_BREAKS_COMPLETED, 0)

    val pomodoroCyclesCompleted: Int
        get() = currentPrefs.getInt(POMODORO_CYCLES_COMPLETED, 0)

    fun increasePomodoroStepsCompleted() {
        currentPrefs.edit().putInt(POMODORO_STEPS_COMPLETED, pomodoroStepsCompleted + 1).apply()
    }

    fun increasePomodoroCyclesCompleted() {
        currentPrefs.edit().putInt(POMODORO_CYCLES_COMPLETED, pomodoroCyclesCompleted + 1).apply()
    }

    fun increaseShortBreaksCompleted() {
        currentPrefs.edit().putInt(SHORT_BREAKS_COMPLETED, shortBreaksCompleted + 1).apply()
    }

    fun increaseLongBreaksCompleted() {
        currentPrefs.edit().putInt(LONG_BREAKS_COMPLETED, longBreaksCompleted + 1).apply()
    }

    fun clearPomodoroTimerSpecs() {
        currentPrefs.edit().clear().apply()
    }

    fun clearPomodoroStepsStats() {
        currentPrefs.edit().apply {
            remove(SHORT_BREAKS_COMPLETED)
            remove(LONG_BREAKS_COMPLETED)
            remove(POMODORO_STEPS_COMPLETED)
        }.apply()
    }

    companion object {
        const val MY_PREFS = "myPrefs"
        const val TASK_WITH_POMODORO_TIMER_ENABLED = "Task with Pomodoro Timer enabled"
        const val CURRENT_POMODORO_TIMER_STEP = "Current Pomodoro Timer step"

        // Quantity of Pomodoro steps completed
        const val POMODORO_STEPS_COMPLETED = "Pomodoro steps completed"
        const val SHORT_BREAKS_COMPLETED = "Short breaks completed"
        const val LONG_BREAKS_COMPLETED = "Long breaks completed"
        const val POMODORO_CYCLES_COMPLETED = "Pomodoro cycles completed"
    }
}