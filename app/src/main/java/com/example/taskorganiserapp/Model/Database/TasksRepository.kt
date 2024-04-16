package com.example.taskorganiserapp.Model.Database

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import androidx.room.Query
import com.example.taskorganiserapp.Model.Entities.TaskItem
import com.example.taskorganiserapp.Model.Entities.TaskList
import com.example.taskorganiserapp.Model.Services.SharedPreferencesManager
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import ulid.ULID

class TasksRepository(private val taskLists: TaskListDAO, private val tasks: TaskItemDAO, private val preferences: SharedPreferencesManager) {
    val allTaskLists: Flow<List<TaskList>> = taskLists.getTaskLists()

    suspend fun insertTaskItem(task: TaskItem): Long {
        return tasks.insertTaskItem(task)
    }

    suspend fun updateTaskItem(task: TaskItem) {
        tasks.updateTaskItem(task)
    }

    suspend fun deleteTaskItem(task: TaskItem) {
        tasks.deleteTaskItem(task)
    }

    fun getTasksForList(taskList: TaskList): Flow<List<TaskItem>> {
        if(taskList.name == "Wszystkie") {
            return when (preferences.getSortType()) {
                0 -> tasks.getAllTasks()
                1 -> tasks.getAllTasksByDateTime()
                2 -> tasks.getAllTasksByAlphabeticalOrder()
                3 -> tasks.getAllTasksByPriority()
                else -> tasks.getAllTasks()
            }
        }
        else {
            return when (preferences.getSortType()) {
                0 -> tasks.getTasksForList(taskList.id)
                1 -> tasks.getTasksForListByDateTime(taskList.id)
                2 -> tasks.getTasksForListByAlphabeticalOrder(taskList.id)
                3 -> tasks.getTasksForListByPriority(taskList.id)
                else -> tasks.getTasksForList(taskList.id)
            }
        }
    }

    fun updateTaskItemStatusByID(id: ULID, status: Boolean) {
        tasks.updateTaskItemStatusByID(id, status)
        Log.i("updateTaskStatus", "updated")
    }

    fun getTasksByName(name: String): Flow<List<TaskItem>> {
        return tasks.getTasksByName(name)
    }

    fun deleteTasksInList(taskList: TaskList) {
        tasks.deleteTasksInList(taskList.id)
    }

    suspend fun insertTaskList(taskList: TaskList): Long {
        return taskLists.insertTaskList(taskList)
    }

    suspend fun updateTaskList(taskList: TaskList) {
        taskLists.updateTaskList(taskList)
    }

    suspend fun deleteTaskList(taskList: TaskList) {
        taskLists.deleteTaskList(taskList)
    }

    fun getFirstTaskList(): Flow<TaskList> {
        return taskLists.getFirstTaskList()
    }

    suspend fun updateTaskListByID(listID: ULID, plusToDoTasks: Int, plusCompletedTasks: Int) {
        taskLists.updateTaskListByID(listID, plusToDoTasks, plusCompletedTasks)
        taskLists.updateFirstTaskList(plusToDoTasks, plusCompletedTasks)
    }
}