package br.com.lucas.pomodoroapp.ui

object DateHelper {

    const val MINUTES_ON_HOUR = 60

    fun checkTimeIsValid(hour: Int, minute: Int): Boolean{
        val hoursInMinutes = hour * MINUTES_ON_HOUR
        val total = hoursInMinutes + minute
        return total <= 60
    }

}