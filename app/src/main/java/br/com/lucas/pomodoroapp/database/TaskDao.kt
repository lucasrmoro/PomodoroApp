package br.com.lucas.pomodoroapp.database

import androidx.room.*

@Dao
interface TaskDao {
    @Insert
    suspend fun insertTask(task: Task)
    @Query("SELECT * FROM task")
    suspend fun getAll() : List<Task>
    @Update
    suspend fun updateTask(task: Task)
    @Delete
    suspend fun deleteTask(task: Task)
}