package com.example.taskorganiserapp

import androidx.room.Entity
import androidx.room.Fts4
import androidx.room.PrimaryKey
import java.util.UUID

data class SubtaskItem(
    var id: Int,
    var name: String,
    var completed: Boolean
) {
    constructor() : this(0, "", false)

    companion object {
        var creatorMode: Boolean = false
    }
    fun setStateImage(): Int = if(completed) R.drawable.checked_button else R.drawable.unchecked_button

    fun convertCompletedToString(): String {
        return if(completed) "completed"
        else "uncompleted"
    }
}