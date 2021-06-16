package br.com.lucas.pomodoroapp.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity
data class Task(
    @PrimaryKey(autoGenerate = true)
    val uid: Int,
    @ColumnInfo
    val taskName: String,
    @ColumnInfo
    val taskMinutes: Int
) : Serializable
