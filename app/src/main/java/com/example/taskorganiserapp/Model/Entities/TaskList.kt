package com.example.taskorganiserapp.Model.Entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import ulid.ULID

@Entity(tableName = "task_lists_table")
data class TaskList (
    @PrimaryKey var id: ULID = ULID.nextULID(),
    var quantityOfToDoTasks: Int,
    var quantityOfCompletedTasks: Int,
    var name: String,
    var isEditable: Boolean,
) {}