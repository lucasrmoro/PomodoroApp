package br.com.lucas.pomodoroapp.database.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity
data class Task(
    @PrimaryKey(autoGenerate = true)
    val uid: Int,
    @ColumnInfo
    var taskName: String,
    @ColumnInfo
    var pomodoroDurations: PomodoroDurations
) : Serializable
