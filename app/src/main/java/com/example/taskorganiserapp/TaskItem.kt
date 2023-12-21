package com.example.taskorganiserapp

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Fts4
import androidx.room.Ignore
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.UUID

@Entity(tableName = "task_item_table")
data class TaskItem(
    @PrimaryKey(autoGenerate = true) var id: Long = 0,
    var listID: Long,
    var name: String,
    var note: String?,
    var time: LocalTime?,
    var date: LocalDate?,
    var priority: Int,
    var completed: Boolean,
    var subtasks: List<SubtaskItem>?
    ) {

    fun setStateImage(): Int = if(completed) R.drawable.checked_button else R.drawable.unchecked_button
}