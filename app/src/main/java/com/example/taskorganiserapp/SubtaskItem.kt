package com.example.taskorganiserapp

import java.util.UUID

data class SubtaskItem(
    var name: String,
    var completed: Boolean,
    var id: UUID = UUID.randomUUID()
) {
    companion object {
        var creatorMode: Boolean = true
    }
    fun setStateImage(): Int = if(completed) R.drawable.checked_button else R.drawable.unchecked_button
}

