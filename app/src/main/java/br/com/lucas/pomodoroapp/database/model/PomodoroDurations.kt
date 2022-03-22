package br.com.lucas.pomodoroapp.database.model

data class PomodoroDurations(
    val pomodoroTime: Int,
    val shortBreakTime: Int,
    val longBreakTime: Int,
    val numberOfCycles: Int
)