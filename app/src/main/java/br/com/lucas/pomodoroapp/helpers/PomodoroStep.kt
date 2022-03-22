package br.com.lucas.pomodoroapp.helpers

enum class PomodoroStep(val value: Int) {
    POMODORO_TIME(1), SHORT_BREAK(2), LONG_BREAK(3);

    companion object{
        private val stages = values().associateBy { it.value }
        fun getByValue(value: Int) = stages[value]
    }
}