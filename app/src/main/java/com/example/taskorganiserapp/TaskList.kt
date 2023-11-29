package com.example.taskorganiserapp

import java.util.UUID

data class TaskList(
    var name: String,
    var tasks: List<TaskItem>,
    var isEditable: Boolean,
    var id: UUID = UUID.randomUUID()
) {

}
