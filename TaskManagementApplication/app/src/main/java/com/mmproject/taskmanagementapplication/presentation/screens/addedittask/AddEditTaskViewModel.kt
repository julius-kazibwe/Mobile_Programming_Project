package com.mmproject.taskmanagementapplication.presentation.screens.addedittask

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.mmproject.taskmanagementapplication.data.TaskDataSource
import com.mmproject.taskmanagementapplication.domain.model.Subtask

data class AddEditTaskUiState(
    var taskName: String = "",
    val taskDescription: String = "",
    val taskDueDate: String = "No due date",
    val isTaskFinished: Boolean = false,
    val taskId : String = "",
    val isLoading: Boolean = false,
    val isTaskSaved: Boolean = false,
    val onEditTask: Boolean = false,
    val viewAsMarkdown: Boolean = false,
    val subtasks: List<Subtask> = emptyList(),
    val reminderDate: String = "Set reminder"
)

class AddEditTaskViewModel : ViewModel() {

    var addEditTaskUiState by mutableStateOf(AddEditTaskUiState())
        private set

    fun addSubtask(subtask: Subtask) {
        val updatedSubtasks = addEditTaskUiState.subtasks.toMutableList()
        updatedSubtasks.add(subtask)
        addEditTaskUiState = addEditTaskUiState.copy(subtasks = updatedSubtasks)
    }
    fun deleteSubtask(index: Int) {
        val updatedSubtasks = addEditTaskUiState.subtasks.toMutableList()
        if (index in updatedSubtasks.indices) {
            updatedSubtasks.removeAt(index) // Remove the subtask at the specified index
            addEditTaskUiState = addEditTaskUiState.copy(subtasks = updatedSubtasks) // Update the UI state
        }
    }
    fun updateSubtaskCheckbox(subtask: Subtask, isChecked: Boolean) {
        // Update the checkbox status of the subtask
        val updatedSubtasks = addEditTaskUiState.subtasks.map { existingSubtask ->
            if (existingSubtask == subtask) {
                existingSubtask.copy(subtaskIsFinished = isChecked)
            } else {
                existingSubtask
            }
        }
        // Update the UI state with the updated subtasks
        addEditTaskUiState = addEditTaskUiState.copy(subtasks = updatedSubtasks)
    }
    fun onEditSubtask(subtask: Subtask, isEditing: Boolean){
        val updatedSubtasks = addEditTaskUiState.subtasks.map { existingSubtask ->
            if (existingSubtask == subtask) {
                existingSubtask.copy(isEditing = isEditing)
            } else {
                existingSubtask
            }
        }
        // Update the UI state with the updated subtasks
        addEditTaskUiState = addEditTaskUiState.copy(subtasks = updatedSubtasks)
    }

    fun updateSubtaskName(subtask: Subtask, newName: String) {
        val updatedSubtasks = addEditTaskUiState.subtasks.map { existingSubtask ->
            if (existingSubtask == subtask) {
                existingSubtask.copy(subtaskName = newName)
            } else {
                existingSubtask
            }
        }
        addEditTaskUiState = addEditTaskUiState.copy(subtasks = updatedSubtasks)
    }

    fun updateName(newName: String) {
        addEditTaskUiState = addEditTaskUiState.copy(taskName = newName)
    }

    fun updateDescription(newDescription: String) {
        addEditTaskUiState = addEditTaskUiState.copy(taskDescription = newDescription)
    }

    fun updateDueDate(newDate: String) {
        addEditTaskUiState = addEditTaskUiState.copy(taskDueDate = newDate)
    }
    fun updateReminderDate(newReminderDate: String) {
        addEditTaskUiState = addEditTaskUiState.copy(reminderDate = newReminderDate)
    }

    fun updateTaskId(taskId: String) {
        addEditTaskUiState = addEditTaskUiState.copy(taskId = taskId)
    }

    fun updateDescriptionView() {
        addEditTaskUiState = addEditTaskUiState.copy(viewAsMarkdown = !addEditTaskUiState.viewAsMarkdown)
    }

    fun saveTask() {
        TaskDataSource.saveTask(
            addEditTaskUiState.taskName,
            addEditTaskUiState.taskDescription,
            addEditTaskUiState.taskDueDate,
            addEditTaskUiState.reminderDate,
            addEditTaskUiState.isTaskFinished,
            addEditTaskUiState.taskId,
            addEditTaskUiState.subtasks

        )
    }

    fun getTask(taskId : String) {
        TaskDataSource.getTask(
            taskId,
            onError = {}
        ) {
            if (it != null) {
                addEditTaskUiState =
                    it.taskName?.let { it1 -> addEditTaskUiState.copy(taskName = it1) }!!
                addEditTaskUiState =
                    it.taskDescription?.let { it1 -> addEditTaskUiState.copy(taskDescription = it1) }!!
                addEditTaskUiState =
                    it.taskDueDate?.let { it1 -> addEditTaskUiState.copy(taskDueDate = it1) }!!
                addEditTaskUiState =
                    it.taskID?.let { it1 -> addEditTaskUiState.copy(taskId = it1) }!!
                addEditTaskUiState =
                    it.taskIsFinished?.let { it1 -> addEditTaskUiState.copy(isTaskFinished = it1) }!!
                addEditTaskUiState =
                    it.subtasks?.let { it1 -> addEditTaskUiState.copy(subtasks = it1) }!!
                addEditTaskUiState =
                    it.reminderDate?.let { it1 -> addEditTaskUiState.copy(reminderDate = it1) }!!
            }
        }
    }
    fun resetState() {
        addEditTaskUiState = AddEditTaskUiState()
    }

}