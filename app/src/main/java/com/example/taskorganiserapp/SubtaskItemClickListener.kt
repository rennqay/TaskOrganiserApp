package com.example.taskorganiserapp

interface SubtaskItemClickListener {
    fun deleteSubtaskItem(subtask: SubtaskItem)
    fun setCompleteSubtaskItem(subtask: SubtaskItem)
    fun setIncompleteSubtaskItem(subtask: SubtaskItem)
}