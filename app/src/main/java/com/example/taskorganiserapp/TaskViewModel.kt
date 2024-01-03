package com.example.taskorganiserapp

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.time.LocalDate
import java.time.LocalTime

class TaskViewModel(private val repository: TasksRepository): ViewModel() {
    var taskItems: MediatorLiveData<List<TaskItem>> = MediatorLiveData<List<TaskItem>>()
    private var selectedTaskItems: LiveData<List<TaskItem>>? = null
    var lastInsertedID: Long = 0

    init {
        selectedTaskItems = repository.getAllTasks().asLiveData()
        taskItems.addSource(selectedTaskItems!!) {
            taskItems.value = it
        }
    }

    fun setAllTasks() {
        if(selectedTaskItems != null)
            taskItems.removeSource(selectedTaskItems!!)

        selectedTaskItems = repository.getAllTasks().asLiveData()
        taskItems.addSource(selectedTaskItems!!) {
            taskItems.value = it
        }
    }

    fun setTasksFromTaskList(taskList: TaskList) {
        if(selectedTaskItems != null)
            taskItems.removeSource(selectedTaskItems!!)

        selectedTaskItems = repository.getTasksForList(taskList).asLiveData()
        taskItems.addSource(selectedTaskItems!!) {
            taskItems.value = it
        }
    }

    fun findTasksByName(name: String) {
        if(selectedTaskItems != null)
            taskItems.removeSource(selectedTaskItems!!)

        selectedTaskItems = repository.getTasksByName(name).asLiveData()
        taskItems.addSource(selectedTaskItems!!) {
            taskItems.value = it
        }
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
        taskItem.completed = state
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