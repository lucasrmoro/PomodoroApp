package br.com.lucas.pomodoroapp.database

import android.content.Context
import androidx.room.Room

object DataBaseConnect {
    fun getTaskDao(context: Context) = Room.databaseBuilder(
        context,
        AppDataBase::class.java, "pomodoro-db"
    ).build().taskDao()
}