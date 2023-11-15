package com.example.taskorganiserapp

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import java.time.LocalDate
import java.time.LocalTime
import java.util.UUID

class TaskListViewModel: ViewModel() {
    var taskLists = MutableLiveData<MutableList<TaskList>>()

    init {
        taskLists.value = mutableListOf()
    }

    fun addTaskList(newTaskList: TaskList) {
        val buffer = taskLists.value
        buffer!!.add(newTaskList)
        taskLists.postValue(buffer)
    }

    fun updateTaskList(id: UUID, newName: String) {
        val buffer = taskLists.value
        val updatedTaskList = buffer!!.find { it.id == id } !!
        updatedTaskList.name = newName
        taskLists.postValue(buffer)
    }
}