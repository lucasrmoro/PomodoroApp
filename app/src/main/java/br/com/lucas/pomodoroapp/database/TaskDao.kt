package br.com.lucas.pomodoroapp.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface TaskDao {
    @Insert
    suspend fun insertTask(task: Task)
    @Query("SELECT * FROM task")
    suspend fun getAll() : List<Task>
}