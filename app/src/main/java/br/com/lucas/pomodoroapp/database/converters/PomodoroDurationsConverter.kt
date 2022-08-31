package br.com.lucas.pomodoroapp.database.converters

import androidx.room.TypeConverter
import br.com.lucas.pomodoroapp.database.model.PomodoroDurations
import com.google.gson.Gson

class PomodoroDurationsConverter {

    @TypeConverter
    fun pomodoroDurationsToString(pomodoroDurations: PomodoroDurations): String =
        Gson().toJson(pomodoroDurations)


    @TypeConverter
    fun pomodoroDurationsFromString(pomodoroDurations: String): PomodoroDurations =
        Gson().fromJson(pomodoroDurations, PomodoroDurations::class.java)

}