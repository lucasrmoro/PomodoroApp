package br.com.lucas.pomodoroapp.database.model

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Entity
@Parcelize
data class Task(
    @PrimaryKey(autoGenerate = true)
    val uid: Int,
    @ColumnInfo
    var taskName: String,
    @ColumnInfo
    var pomodoroDurations: PomodoroDurations
) : Parcelable