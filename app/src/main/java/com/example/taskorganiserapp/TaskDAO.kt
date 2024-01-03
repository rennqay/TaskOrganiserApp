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
interface TaskItemDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTaskItem(taskItem: TaskItem): Long

    @Update
    suspend fun updateTaskItem(taskItem: TaskItem)

    @Delete
    suspend fun deleteTaskItem(taskItem: TaskItem)

    @Query("SELECT * FROM task_item_table WHERE listID = :listID ORDER BY date, time")
    fun getTasksForListByDateTime(listID: Long): Flow<List<TaskItem>>

    @Query("SELECT * FROM task_item_table WHERE listID = :listID ORDER BY name")
    fun getTasksForListByAlphabeticalOrder(listID: Long): Flow<List<TaskItem>>

    @Query("SELECT * FROM task_item_table WHERE listID = :listID ORDER BY priority")
    fun getTasksForListByPriority(listID: Long): Flow<List<TaskItem>>

    @Query("SELECT * FROM task_item_table WHERE listID = :listID")
    fun getTasksForList(listID: Long): Flow<List<TaskItem>>

    @Query("SELECT * FROM task_item_table WHERE name LIKE '%' || :name || '%'")
    fun getTasksByName(name: String): Flow<List<TaskItem>>

    @Query("SELECT * FROM task_item_table ORDER BY date, time")
    fun getAllTasksByDateTime(): Flow<List<TaskItem>>

    @Query("SELECT * FROM task_item_table ORDER BY name")
    fun getAllTasksByAlphabeticalOrder(): Flow<List<TaskItem>>

    @Query("SELECT * FROM task_item_table ORDER BY priority")
    fun getAllTasksByPriority(): Flow<List<TaskItem>>

    @Query("SELECT * FROM task_item_table")
    fun getAllTasks(): Flow<List<TaskItem>>

    @Query("DELETE FROM task_item_table WHERE listID = :listID")
    fun deleteTasksInList(listID: Long)
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