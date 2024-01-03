package com.example.taskorganiserapp

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.util.UUID

class SubtaskViewModel(): ViewModel() {
    var subtaskItems = MutableLiveData<MutableList<SubtaskItem>?>()

    init {
        subtaskItems.value = mutableListOf()
    }

    fun addSubtaskItem(newSubtask: SubtaskItem) {
        val subtaskList = subtaskItems.value
        subtaskList!!.add(newSubtask)
        subtaskItems.postValue(subtaskList)
    }

    fun deleteSubtaskItem(subtask: SubtaskItem) {
        val list = subtaskItems.value
        val buffer = list!!.find { it.id == subtask.id }!!
        list.remove(buffer)
        subtaskItems.postValue(list)
    }

//    fun updateSubtaskItem(id: Int, newName: String) {
//        val subtaskList = subtaskItems.value
//        val subtask = subtaskList!!.find { it.id == id }!!
//        subtask.name = newName
//        subtaskItems.postValue(subtaskList)
//    }

    fun setState(subtask: SubtaskItem, state: Boolean) {
        val list = subtaskItems.value
        val buffer = list!!.find { it.id == subtask.id }!!
        buffer.completed = state
        subtaskItems.postValue(list)
    }
}