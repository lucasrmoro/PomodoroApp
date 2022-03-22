package br.com.lucas.pomodoroapp.database.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class PomodoroDurations(
    val pomodoroTime: Int = 25,
    val shortBreakTime: Int = 5,
    val longBreakTime: Int = 20,
    val numberOfCycles: Int = 4
): Parcelable