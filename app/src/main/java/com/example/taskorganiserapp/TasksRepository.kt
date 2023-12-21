package com.example.taskorganiserapp

import androidx.annotation.WorkerThread
import kotlinx.coroutines.flow.Flow

class TasksRepository(private val taskLists: TaskListDAO, private val tasks: TaskItemDAO) {
    val allTasksItems: Flow<List<TaskItem>> = tasks.getAllTasks()
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
        return tasks.getTasksForList(taskList.id)
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
}