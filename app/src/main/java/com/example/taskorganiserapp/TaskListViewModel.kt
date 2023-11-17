package com.example.taskorganiserapp

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import java.util.UUID

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
}