package br.com.lucas.pomodoroapp.core.extensions

import kotlin.math.absoluteValue

fun Int.convertMinutesToHour(): String {
    val hours = this / 60
    var minutes = hours * 60 - this
    minutes = minutes.absoluteValue

    var hoursText = hours.toString()
    if (hoursText.length == 1) {
        hoursText = "0$hoursText"
    }

    var minutesText = minutes.toString()
    if (minutesText.length == 1) {
        minutesText = "0$minutesText"
    }

    return ("$hoursText:$minutesText")
}