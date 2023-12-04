package com.example.taskorganiserapp

import androidx.annotation.WorkerThread
import kotlinx.coroutines.flow.Flow

class TasksRepository(private val taskLists: TaskListDAO, private val tasks: TaskItemDAO, private val subtasks: SubtaskItemDAO) {
    val allSubtaskItems: Flow<List<SubtaskItem>> = subtasks.getAllSubtasks()
    val allTasksItems: Flow<List<TaskItem>> = tasks.getAllTasks()
    val allTaskLists: Flow<List<TaskList>> = taskLists.getTaskLists()

    @WorkerThread
    suspend fun insertSubtaskItem(subtask: SubtaskItem): Long {
        return subtasks.insertSubtaskItem(subtask)
    }

    @WorkerThread
    suspend fun updateSubtaskItem(subtask: SubtaskItem) {
        subtasks.updateSubtaskItem(subtask)
    }

    @WorkerThread
    suspend fun deleteSubtaskItem(subtask: SubtaskItem) {
        subtasks.deleteSubtaskItem(subtask)
    }

    @WorkerThread
    fun getSubtasksForTask(task: TaskItem): Flow<List<SubtaskItem>> {
        return subtasks.getSubtasksForTask(task.id)
    }

    @WorkerThread
    suspend fun insertTaskItem(task: TaskItem): Long {
        return tasks.insertTaskItem(task)
    }

    @WorkerThread
    suspend fun updateTaskItem(task: TaskItem) {
        tasks.updateTaskItem(task)
    }

    @WorkerThread
    suspend fun deleteTaskItem(task: TaskItem) {
        tasks.deleteTaskItem(task)
    }

    @WorkerThread
    fun getTasksForList(taskList: TaskList): Flow<List<TaskItem>> {
        return tasks.getTasksForList(taskList.id)
    }

    @WorkerThread
    suspend fun insertTaskList(taskList: TaskList): Long {
        return taskLists.insertTaskList(taskList)
    }

    @WorkerThread
    suspend fun updateTaskList(taskList: TaskList) {
        taskLists.updateTaskList(taskList)
    }

    @WorkerThread
    suspend fun deleteTaskList(taskList: TaskList) {
        taskLists.deleteTaskList(taskList)
    }
}