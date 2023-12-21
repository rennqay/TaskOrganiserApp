package com.example.taskorganiserapp

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Fts4
import androidx.room.Ignore
import androidx.room.PrimaryKey
import java.util.UUID


@Entity(tableName = "task_lists_table")

data class TaskList (
    @PrimaryKey(autoGenerate = true) var id: Long = 0,
    var name: String,
    var isEditable: Boolean,
) {}