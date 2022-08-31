package br.com.lucas.pomodoroapp.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import br.com.lucas.pomodoroapp.database.converters.PomodoroDurationsConverter
import br.com.lucas.pomodoroapp.database.model.Task

@Database(entities = [Task::class], version = 2)
@TypeConverters(PomodoroDurationsConverter::class)
abstract class AppDataBase : RoomDatabase() {
    abstract fun taskDao() : TaskDao
}