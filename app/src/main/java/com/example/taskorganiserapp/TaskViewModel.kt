package com.example.taskorganiserapp

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

class TaskViewModel(private val repository: TasksRepository): ViewModel() {
    var taskItems: MediatorLiveData<List<TaskItem>> = MediatorLiveData<List<TaskItem>>()
    var selectedToDoTaskItems: LiveData<List<TaskItem>>
    var selectedCompletedTaskItems: LiveData<List<TaskItem>>
    var lastInsertedID: Long = 0

    init {
        selectedToDoTaskItems = repository.getTasksForList(TaskList(name = "Wszystkie", quantityOfToDoTasks = 0, quantityOfCompletedTasks = 0, isEditable = false)).asLiveData().map { tasks -> tasks.filter { task -> !task.isCompleted }}
        selectedCompletedTaskItems = repository.getTasksForList(TaskList(name = "Wszystkie", quantityOfToDoTasks = 0, quantityOfCompletedTasks = 0, isEditable = false)).asLiveData().map { tasks -> tasks.filter { task -> task.isCompleted }}
        taskItems.addSource(selectedToDoTaskItems) {
            taskItems.value = it
        }
    }

    fun setTasksFromTabPosition(tabPosition: Int) {
        if(tabPosition == 0) {
            taskItems.removeSource(selectedCompletedTaskItems)
            taskItems.addSource(selectedToDoTaskItems) {
                taskItems.value = it
            }
        }
        else {
            taskItems.removeSource(selectedToDoTaskItems)
            taskItems.addSource(selectedCompletedTaskItems) {
                taskItems.value = it
            }
        }
    }

    fun setTasksFromTaskList(taskList: TaskList, tabPosition: Int) {
        if(tabPosition == 0)
            taskItems.removeSource(selectedToDoTaskItems)

        if(tabPosition == 1)
            taskItems.removeSource(selectedCompletedTaskItems)
        selectedToDoTaskItems = repository.getTasksForList(taskList).asLiveData().map { tasks -> tasks.filter { task -> !task.isCompleted }}
        selectedCompletedTaskItems = repository.getTasksForList(taskList).asLiveData().map { tasks -> tasks.filter { task -> task.isCompleted }}

        if(tabPosition == 0)
            taskItems.addSource(selectedToDoTaskItems) {
                taskItems.value = it
            }
        else
            taskItems.addSource(selectedCompletedTaskItems) {
                taskItems.value = it
            }
    }

    fun findTasksByName(name: String) {
        taskItems.removeSource(selectedToDoTaskItems)
        taskItems.removeSource(selectedCompletedTaskItems)
        selectedToDoTaskItems = repository.getTasksByName(name).asLiveData().map { tasks -> tasks.filter { task -> !task.isCompleted }}
        selectedCompletedTaskItems = repository.getTasksByName(name).asLiveData().map { tasks -> tasks.filter { task -> task.isCompleted }}

        taskItems.addSource(selectedToDoTaskItems) {
            taskItems.value = it
        }
        taskItems.addSource(selectedCompletedTaskItems) {
            taskItems.value = it
        }
    }

    suspend fun getQuantityOfSelectedTasks(): String = withContext(Dispatchers.IO) {
        val completedTasksDeferred = async { selectedCompletedTaskItems.value }
        val toDoTasksDeferred = async { selectedToDoTaskItems.value }

        val completedTasks = completedTasksDeferred.await() ?: emptyList()
        val toDoTasks = toDoTasksDeferred.await() ?: emptyList()

        val totalCountOfTasks = completedTasks.size + toDoTasks.size
        return@withContext "(${completedTasks.size}✔/$totalCountOfTasks)"
    }

    fun addTaskItem(newTaskItem: TaskItem) {
        runBlocking { lastInsertedID = repository.insertTaskItem(newTaskItem) }
    }

    fun updateTaskItem(newTaskItem: TaskItem) = viewModelScope.launch {
        repository.updateTaskItem(newTaskItem)
    }

    fun deleteTaskItem(taskItem: TaskItem) = viewModelScope.launch {
        repository.deleteTaskItem(taskItem)
    }

    fun setState(taskItem: TaskItem, state: Boolean) = viewModelScope.launch {
        if(state)
            taskItem.setCompletionTime()
        else
            taskItem.completionTime = null

        taskItem.isCompleted = state
        repository.updateTaskItem(taskItem)
    }

    class TaskModelFactory(private val repository: TasksRepository) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(TaskViewModel::class.java))
                return TaskViewModel(repository) as T

            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}