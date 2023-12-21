package com.example.taskorganiserapp

import androidx.room.Entity
import androidx.room.Fts4
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "subtask_item_table")
data class SubtaskItem(
    @PrimaryKey(autoGenerate = true) var id: Long = 0,
    var name: String,
    var completed: Boolean
) {
    constructor() : this(0, "", false)

    companion object {
        var creatorMode: Boolean = true
    }
    fun setStateImage(): Int = if(completed) R.drawable.checked_button else R.drawable.unchecked_button
}

