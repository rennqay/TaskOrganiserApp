package com.example.taskorganiserapp

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import java.util.UUID

class SubtaskViewModel: ViewModel() {
    var subtaskItems = MutableLiveData<MutableList<SubtaskItem>>()

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

    fun updateSubtaskItem(id: UUID, newName: String) {
        val subtaskList = subtaskItems.value
        val subtask = subtaskList!!.find { it.id == id }!!
        subtask.name = newName
        subtaskItems.postValue(subtaskList)
    }

    fun setCompleted(subtask: SubtaskItem) {
        val list = subtaskItems.value
        val buffer = list!!.find { it.id == subtask.id }!!
        buffer.completed = true
        subtaskItems.postValue(list)
    }

    fun setUncompleted(subtask: SubtaskItem) {
        val list = subtaskItems.value
        val buffer = list!!.find { it.id == subtask.id }!!
        buffer.completed = false
        subtaskItems.postValue(list)
    }

    fun setCreatorMode(boolean: Boolean) {
        val list = subtaskItems.value
            for (subtaskItem in list!!) {
                subtaskItem.creatorMode = boolean
            }
    }
}