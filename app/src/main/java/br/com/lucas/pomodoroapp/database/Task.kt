package br.com.lucas.pomodoroapp.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity
data class Task(
    @PrimaryKey(autoGenerate = true)
    val uid: Int,
    @ColumnInfo
    var taskName: String,
    @ColumnInfo
    var taskMinutes: Int
) : Serializable {
    @Ignore
    private var isSelected: Boolean = false

    fun isTaskSelected(): Boolean{
        return isSelected
    }

    fun toggleTask(){
        isSelected = !isSelected
    }

    fun resetTaskSelection(){
        isSelected = false
    }
}
