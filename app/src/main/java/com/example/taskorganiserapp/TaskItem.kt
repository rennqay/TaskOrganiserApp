package com.example.taskorganiserapp

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Fts4
import androidx.room.Ignore
import androidx.room.PrimaryKey
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
    var time: String?,
    var date: String?,
    var priority: Int,
    var completed: Boolean,
    ) {

    @Ignore var subtasks: List<SubtaskItem>? = null

    fun parseTime(): LocalTime? = if (time == null) null
        else LocalTime.parse(time, DateTimeFormatter.ISO_LOCAL_TIME)

    fun parseDate(): LocalDate? = if (date == null) null
        else LocalDate.parse(date, DateTimeFormatter.ISO_LOCAL_DATE)

    fun setStateImage(): Int = if(completed) R.drawable.checked_button else R.drawable.unchecked_button
}