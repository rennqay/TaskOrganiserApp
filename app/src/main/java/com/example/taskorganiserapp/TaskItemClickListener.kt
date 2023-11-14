package com.example.taskorganiserapp

interface TaskItemClickListener {
    fun editTaskItem(task: TaskItem)
    fun setCompleteTaskItem(task: TaskItem)
    fun setIncompleteTaskItem(task: TaskItem)
}