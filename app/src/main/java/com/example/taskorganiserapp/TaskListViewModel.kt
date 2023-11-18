package com.example.taskorganiserapp

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class TaskListViewModel: ViewModel() {
    var listOfTaskLists = MutableLiveData<MutableList<TaskList>>()

    init {
        listOfTaskLists.value = mutableListOf()
    }

    fun addTaskList(newTaskList: TaskList) {
        val buffer = listOfTaskLists.value
        buffer!!.add(newTaskList)
        listOfTaskLists.postValue(buffer)
    }

    fun deleteTaskList(taskList: TaskList) {
        listOfTaskLists.value!!.remove(taskList)
    }

    fun updateTaskList(taskList: TaskList) {
        val buffer = listOfTaskLists.value
        val editedTaskList = buffer!!.find { it.id == taskList.id } !!
        editedTaskList.name = taskList.name
        listOfTaskLists.postValue(buffer)
    }

}