package com.example.taskorganiserapp

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import java.util.UUID

class SubtaskViewModel(): ViewModel() {
    var subtaskItems = MutableLiveData<MutableList<SubtaskItem>>()

    init {
        subtaskItems.value = mutableListOf()
    }

    fun addSubtaskItem(newSubtask: SubtaskItem) {
        val subtaskList = subtaskItems.value
        subtaskList!!.add(newSubtask)
        subtaskItems.postValue(subtaskList!!)
    }

    fun deleteSubtaskItem(subtask: SubtaskItem) {
        val list = subtaskItems.value
        val buffer = list!!.find { it.id == subtask.id }!!
        list.remove(buffer)
        subtaskItems.postValue(list!!)
    }

    fun updateSubtaskItem(id: Long, newName: String) {
        val subtaskList = subtaskItems.value
        val subtask = subtaskList!!.find { it.id == id }!!
        subtask.name = newName
        subtaskItems.postValue(subtaskList!!)
    }

    fun setCompleted(subtask: SubtaskItem) {
        val list = subtaskItems.value
        val buffer = list!!.find { it.id == subtask.id }!!
        buffer.completed = true
        subtaskItems.postValue(list!!)
    }

    fun setUncompleted(subtask: SubtaskItem) {
        val list = subtaskItems.value
        val buffer = list!!.find { it.id == subtask.id }!!
        buffer.completed = false
        subtaskItems.postValue(list!!)
    }

    fun setTaskIDForEachSubtask(id: Long) {
        val list = subtaskItems.value
        for (subtaskItem in list!!) {
            subtaskItem.taskID = id
        }
    }
}