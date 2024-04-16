package com.example.taskorganiserapp.ViewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.taskorganiserapp.Model.Entities.TaskList
import com.example.taskorganiserapp.Model.Database.TasksRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class TaskListViewModel(private val repository: TasksRepository): ViewModel() {
    var listOfTaskLists: LiveData<List<TaskList>> = repository.allTaskLists.asLiveData()
    var firstTaskList: LiveData<TaskList> = repository.getFirstTaskList().asLiveData()

    fun addTaskList(newTaskList: TaskList) = viewModelScope.launch {
        repository.insertTaskList(newTaskList)
    }

    fun updateTaskList(newTaskList: TaskList) = viewModelScope.launch {
        repository.updateTaskList(newTaskList)
    }

    fun deleteTaskList(taskList: TaskList) = viewModelScope.launch {
        repository.deleteTaskList(taskList)
        CoroutineScope(Dispatchers.IO).launch {
            repository.deleteTasksInList(taskList)
        }
    }
}

class TaskListModelFactory(private val repository: TasksRepository): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T
    {
        if (modelClass.isAssignableFrom(TaskListViewModel::class.java))
            return TaskListViewModel(repository) as T

        throw IllegalArgumentException("Unknown ViewModel class")
    }
}