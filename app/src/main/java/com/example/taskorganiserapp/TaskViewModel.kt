package com.example.taskorganiserapp

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import java.time.LocalDate
import java.time.LocalTime
import java.util.UUID

class TaskViewModel: ViewModel() {
    var taskItems = MutableLiveData<MutableList<TaskItem>>()

    init {
        taskItems.value = mutableListOf()
    }

    fun setTaskList(taskList: TaskList) {
        taskItems.postValue(taskList.tasks.toMutableList())
    }

    fun addTaskItem(newTask: TaskItem) {
        val taskList = taskItems.value
        taskList!!.add(newTask)
        taskItems.postValue(taskList)
    }

    fun updateTaskItem(id: UUID, newName: String, newNote: String?, newTime: LocalTime?, newDate: LocalDate?, newPriority: Int, newSubtasks: List<SubtaskItem>?) {
        val taskList = taskItems.value
        val task = taskList!!.find { it.id == id } !!
        task.name = newName
        task.note = newNote
        task.time = newTime
        task.date = newDate
        task.priority = newPriority
        task.subtasks = newSubtasks
        taskItems.postValue(taskList)
    }

    fun setCompleted(task: TaskItem)
    {
        val list = taskItems.value
        val task = list!!.find { it.id == task.id }!!
        task.completed = true
        taskItems.postValue(list)
    }

    fun setUncompleted(task: TaskItem)
    {
        val list = taskItems.value
        val task = list!!.find { it.id == task.id }!!
        task.completed = false
        taskItems.postValue(list)
    }
}