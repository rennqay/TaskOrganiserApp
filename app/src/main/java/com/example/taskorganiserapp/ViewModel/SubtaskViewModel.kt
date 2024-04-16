package com.example.taskorganiserapp.ViewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.taskorganiserapp.Model.Entities.SubtaskItem

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

    fun setState(subtask: SubtaskItem, state: Boolean) {
        val list = subtaskItems.value
        val buffer = list!!.find { it.id == subtask.id }!!
        buffer.completed = state
        subtaskItems.postValue(list)
    }
}