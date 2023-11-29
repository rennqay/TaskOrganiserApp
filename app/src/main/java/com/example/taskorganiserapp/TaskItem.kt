package com.example.taskorganiserapp

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate
import java.time.LocalTime
import java.util.UUID

@Entity(tableName = "task_item_table")
data class TaskItem(
    @ColumnInfo(name = "name") var name: String,
    @ColumnInfo(name = "note") var note: String?,
    @ColumnInfo(name = "time") var time: LocalTime?,
    @ColumnInfo(name = "date") var date: LocalDate?,
    @ColumnInfo(name = "priority") var priority: Int,
    @ColumnInfo(name = "completed") var completed: Boolean,
    @ColumnInfo(name = "subtasks") var subtasks: List<SubtaskItem>?,
    @PrimaryKey(autoGenerate = true) var id: UUID = UUID.randomUUID()
    ) {

    fun setStateImage(): Int = if(completed) R.drawable.checked_button else R.drawable.unchecked_button
}