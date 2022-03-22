package br.com.lucas.pomodoroapp.database

import androidx.room.Database
import androidx.room.RoomDatabase
import br.com.lucas.pomodoroapp.database.model.Task

@Database(entities = [Task::class], version = 1)
abstract class AppDataBase : RoomDatabase() {
    abstract fun taskDao() : TaskDao
}