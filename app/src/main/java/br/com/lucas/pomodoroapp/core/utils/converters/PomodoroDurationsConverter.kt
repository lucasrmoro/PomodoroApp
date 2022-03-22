package br.com.lucas.pomodoroapp.core.utils.converters

import androidx.room.TypeConverter
import br.com.lucas.pomodoroapp.database.model.PomodoroDurations
import com.google.gson.Gson

class PomodoroDurationsConverter {

    @TypeConverter
    fun pomodoroDurationsToString(pomodoroDurations: PomodoroDurations): String =
        Gson().toJson(pomodoroDurations)

    @TypeConverter
    fun pomodoroDurationsFromString(pomodoroTimerSpecs: String): PomodoroDurations =
        Gson().fromJson(pomodoroTimerSpecs, PomodoroDurations::class.java)

}