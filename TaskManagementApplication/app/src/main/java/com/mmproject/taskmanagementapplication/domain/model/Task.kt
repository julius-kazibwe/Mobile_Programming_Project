package com.mmproject.taskmanagementapplication.domain.model

data class Task(
    var taskName : String? = null,
    var taskDescription : String? = null,
    var taskDueDate : String? = null,
    var reminderDate : String? = null,
    var taskIsFinished : Boolean? = null,
    var taskID : String? = null,
    var subtasks: List<Subtask>? = null
)

data class Subtask(
    var subtaskName: String? = null,
    var subtaskIsFinished: Boolean? = null,
    var isEditing: Boolean? = null
)