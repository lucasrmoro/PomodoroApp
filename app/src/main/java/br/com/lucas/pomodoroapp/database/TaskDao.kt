package br.com.lucas.pomodoroapp.database

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {
    @Insert
    suspend fun insertTask(task: Task)
    @Query("SELECT * FROM task")
    suspend fun getAll() : Flow<List<Task>>
    @Update
    suspend fun updateTask(task: Task)
    @Delete
    suspend fun deleteTask(task: Task)
}