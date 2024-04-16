package com.example.taskorganiserapp.Model.Database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.taskorganiserapp.Model.Entities.TaskItem
import com.example.taskorganiserapp.Model.Entities.TaskList
import kotlinx.coroutines.flow.Flow
import ulid.ULID

@Dao
interface TaskItemDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTaskItem(taskItem: TaskItem): Long

    @Update
    suspend fun updateTaskItem(taskItem: TaskItem)

    @Delete
    suspend fun deleteTaskItem(taskItem: TaskItem)

    @Query("UPDATE task_item_table SET isCompleted = :status WHERE id = :id")
    fun updateTaskItemStatusByID(id: ULID, status: Boolean)

    @Query("SELECT * FROM task_item_table WHERE listID = :listID ORDER BY date, time")
    fun getTasksForListByDateTime(listID: ULID): Flow<List<TaskItem>>

    @Query("SELECT * FROM task_item_table WHERE listID = :listID ORDER BY name")
    fun getTasksForListByAlphabeticalOrder(listID: ULID): Flow<List<TaskItem>>

    @Query("SELECT * FROM task_item_table WHERE listID = :listID ORDER BY priority")
    fun getTasksForListByPriority(listID: ULID): Flow<List<TaskItem>>

    @Query("SELECT * FROM task_item_table WHERE listID = :listID")
    fun getTasksForList(listID: ULID): Flow<List<TaskItem>>

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
    fun deleteTasksInList(listID: ULID)

    @Query("SELECT * FROM task_lists_table JOIN task_item_table ON task_lists_table.id = task_item_table.listID")
    fun getGroupedTaskItems(): Flow<Map<TaskList, List<TaskItem>>>

    @Query("UPDATE task_item_table SET reminderTime = null WHERE id = :taskID")
    fun eraseReminderTimeInTaskItemByID(taskID: ULID)
}

@Dao
interface TaskListDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTaskList(taskList: TaskList): Long

    @Update
    suspend fun updateTaskList(taskList: TaskList)

    @Delete
    suspend fun deleteTaskList(taskList: TaskList)

    @Query("SELECT * FROM task_lists_table LIMIT 1")
    fun getFirstTaskList(): Flow<TaskList>

    @Query("SELECT * FROM task_lists_table")
    fun getTaskLists(): Flow<List<TaskList>>

    @Query("SELECT * FROM task_lists_table WHERE id = :listID")
    fun getTaskListByID(listID: ULID): Flow<TaskList>

    @Query("UPDATE task_lists_table SET quantityOfToDoTasks = quantityOfToDoTasks + :plusToDoTasks, quantityOfCompletedTasks = quantityOfCompletedTasks + :plusCompletedTasks WHERE id = :listID")
    suspend fun updateTaskListByID(listID: ULID, plusToDoTasks: Int, plusCompletedTasks: Int)

    @Query("UPDATE task_lists_table SET quantityOfToDoTasks = quantityOfToDoTasks + :plusToDoTasks, quantityOfCompletedTasks = quantityOfCompletedTasks + :plusCompletedTasks WHERE id = (SELECT MIN(id) FROM task_lists_table)")
    suspend fun updateFirstTaskList(plusToDoTasks: Int, plusCompletedTasks: Int)
}