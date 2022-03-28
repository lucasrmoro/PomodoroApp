package br.com.lucas.pomodoroapp.core.extensions

import java.util.concurrent.TimeUnit

fun Int.convertMinutesToHour(): String {
    val hours = TimeUnit.MINUTES.toHours(this.toLong())
    val minutes = this % 60
    return String.format("%02d:%02d", hours, minutes)
}

fun Long.toMinutesAndSeconds(): String {
    val minutes = TimeUnit.MILLISECONDS.toMinutes(this)
    val seconds = TimeUnit.MILLISECONDS.toSeconds(this) % 60
    return String.format("%02d:%02d", minutes, seconds)
}