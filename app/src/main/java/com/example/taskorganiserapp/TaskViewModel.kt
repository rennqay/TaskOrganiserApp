package com.example.taskorganiserapp

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime

class TaskViewModel(private val repository: TasksRepository): ViewModel() {
    var taskItems: LiveData<List<TaskItem>> = repository.allTasksItems.asLiveData()
    var lastInsertedID: Long = 0

    fun setTasksFromTaskList(taskList: TaskList) = viewModelScope.launch {
        taskItems = repository.getTasksForList(taskList).asLiveData()
    }

    fun addTaskItem(newTaskItem: TaskItem) = viewModelScope.launch {
        lastInsertedID = repository.insertTaskItem(newTaskItem)
    }

    fun updateTaskItem(newTaskItem: TaskItem) = viewModelScope.launch {
        repository.updateTaskItem(newTaskItem)
    }

    fun deleteTaskItem(taskItem: TaskItem) = viewModelScope.launch {
        repository.deleteTaskItem(taskItem)
    }

    fun isCompleted(taskItem: TaskItem, state: Boolean) = viewModelScope.launch {
        taskItem.completed = state
        repository.updateTaskItem(taskItem)
    }
}

class TaskItemModelFactory(private val repository: TasksRepository): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T
    {
        if (modelClass.isAssignableFrom(TaskViewModel::class.java))
            return TaskViewModel(repository) as T

        throw IllegalArgumentException("Unknown ViewModel class")
    }
}