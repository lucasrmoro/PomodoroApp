package br.com.lucas.pomodoroapp.database.model

data class PomodoroDurations(
    val pomodoroTime: Int = 25,
    val shortBreakTime: Int = 5,
    val longBreakTime: Int = 20,
    val numberOfCycles: Int = 4
)