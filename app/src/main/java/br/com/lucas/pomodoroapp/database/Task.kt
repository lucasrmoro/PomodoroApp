package br.com.lucas.pomodoroapp.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Task(
    @PrimaryKey(autoGenerate = true) val uid: Int = 0,
    @ColumnInfo
    val taskName: String,
    @ColumnInfo
    val taskMinutes: Int
)