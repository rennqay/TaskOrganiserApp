package com.example.taskorganiserapp

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface SubtaskItemDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSubtaskItem(subtaskItem: SubtaskItem): Long

    @Update
    suspend fun updateSubtaskItem(subtaskItem: SubtaskItem)

    @Delete
    suspend fun deleteSubtaskItem(subtaskItem: SubtaskItem)

    @Query("SELECT * FROM subtask_item_table WHERE taskID = :taskID")
    fun getSubtasksForTask(taskID: Long): Flow<List<SubtaskItem>>

    @Query("SELECT * FROM subtask_item_table")
    fun getAllSubtasks(): Flow<List<SubtaskItem>>
}

@Dao
interface TaskItemDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTaskItem(taskItem: TaskItem): Long

    @Update
    suspend fun updateTaskItem(taskItem: TaskItem)

    @Delete
    suspend fun deleteTaskItem(taskItem: TaskItem)

    @Query("SELECT * FROM task_item_table WHERE listID = :listID")
    fun getTasksForList(listID: Long): Flow<List<TaskItem>>

    @Query("SELECT * FROM task_item_table")
    fun getAllTasks(): Flow<List<TaskItem>>
}

@Dao
interface TaskListDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTaskList(taskList: TaskList): Long

    @Update
    suspend fun updateTaskList(taskList: TaskList)

    @Delete
    suspend fun deleteTaskList(taskList: TaskList)

    @Query("SELECT * FROM task_lists_table")
    fun getTaskLists(): Flow<List<TaskList>>
}