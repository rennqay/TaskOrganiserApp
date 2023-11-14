package com.example.taskorganiserapp

import java.time.LocalDate
import java.time.LocalTime
import java.util.UUID

data class TaskItem(
    var name: String,
    var note: String?,
    var time: LocalTime?,
    var date: LocalDate?,
    var priority: Int,
    var completed: Boolean,
    var id: UUID = UUID.randomUUID()
    ) {

    fun setStateImage(): Int = if(completed) R.drawable.checked_button else R.drawable.unchecked_button
}