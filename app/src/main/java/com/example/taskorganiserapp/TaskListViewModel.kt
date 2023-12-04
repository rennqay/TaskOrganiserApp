package com.example.taskorganiserapp

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class TaskListViewModel(private val repository: TasksRepository): ViewModel() {
    var listOfTaskLists: LiveData<List<TaskList>> = repository.allTaskLists.asLiveData()
    var lastInsertedID: Long = 0

    init {
//        listOfTaskLists.value = mutableListOf()
//        listOfTaskLists.value!!.add(TaskList("Wszystkie", mutableListOf(), false))
//        listOfTaskLists.value!!.add(TaskList("Usunięte", mutableListOf(), false))
//        listOfTaskLists.value!!.add(TaskList("Ukończone", mutableListOf(), false))
    }

//    fun addTaskList(newTaskList: TaskList) {
//        val buffer = listOfTaskLists.value
//        buffer!!.add(newTaskList)
//        listOfTaskLists.postValue(buffer)
//    }
//
//    fun deleteTaskList(taskList: TaskList) {
//        val buffer = listOfTaskLists.value
//        buffer!!.remove(taskList)
//        listOfTaskLists.postValue(buffer)
//    }
//
//    fun updateTaskList(taskList: TaskList) {
//        val buffer = listOfTaskLists.value
//        val editedTaskList = buffer!!.find { it.id == taskList.id } !!
//        editedTaskList.name = taskList.name
//        listOfTaskLists.postValue(buffer)
//    }

    fun addTaskList(newTaskList: TaskList) = viewModelScope.launch {
        lastInsertedID = repository.insertTaskList(newTaskList)
    }

    fun updateTaskList(newTaskList: TaskList) = viewModelScope.launch {
        repository.updateTaskList(newTaskList)
    }

    fun deleteTaskList(taskList: TaskList) = viewModelScope.launch {
        repository.deleteTaskList(taskList)
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