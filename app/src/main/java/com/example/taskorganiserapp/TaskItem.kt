package com.example.taskorganiserapp

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter

const val NORMAL = 1
const val HIGH = 2
const val VERY_HIGH = 3

@Entity(tableName = "task_item_table")
data class TaskItem(
    @PrimaryKey(autoGenerate = true) var id: Long = 0,
    var listID: Long,
    var name: String,
    var note: String?,
    var time: LocalTime?,
    var date: LocalDate?,
    var reminderTime: LocalDateTime?,
    var completionTime: LocalDateTime?,
    var priority: Int,
    var isCompleted: Boolean,
    var isDelayed: Boolean,
    var subtasks: List<SubtaskItem>?
    ) {

    fun setStateImage(): Int {
        return if(isCompleted)
            R.drawable.checked_button
        else
            R.drawable.unchecked_button
    }

    fun setCompletionTime() {
        completionTime = LocalDateTime.now()
    }

    fun convertCompletionTimeToString(): String? {
        return completionTime?.format(DateTimeFormatter.ofPattern("E d MMM - HH:mm"))
    }

    fun convertPriorityToString(): String {
        return when(priority) {
            NORMAL -> "Normal"
            HIGH -> "High"
            VERY_HIGH -> "Very High"
            else -> "Normal"
        }
    }
}