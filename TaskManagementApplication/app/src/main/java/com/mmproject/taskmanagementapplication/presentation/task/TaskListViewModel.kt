package com.mmproject.taskmanagementapplication.presentation.task

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mmproject.taskmanagementapplication.data.TaskDataSource
import com.mmproject.taskmanagementapplication.domain.model.Task
import kotlinx.coroutines.launch

class TaskListViewModel : ViewModel() {

    private val tasks = mutableStateListOf(
        Task(taskName = "",
            taskDescription = "",
            taskID = "",
            taskDueDate = "",
            taskIsFinished = false)
    )

    init {
        updateTaskList()
    }

    private fun updateTaskList() {
        TaskDataSource.getTasks(tasks)
    }

    val deletedTasks = mutableStateListOf<String>()

    fun deleteTask(taskId : String){
        deletedTasks.add(taskId)
        TaskDataSource.deleteTask(taskId)
    }

    fun finishTask(taskId : String, isFinished : Boolean) = viewModelScope.launch {
        TaskDataSource.finishTask(taskId, isFinished)
    }

    fun getUnfinishedTasks() : List<Task> {
        val unfinishedTasks = mutableListOf<Task>()

        for(task in tasks)
            if(task.taskIsFinished == false)
                unfinishedTasks.add(task)

        return unfinishedTasks
    }

    fun getFinishedTasks(): List<Task> {
        val finishedTasks = mutableListOf<Task>()

        for (task in tasks) {
            val allSubtasksCompleted = task.subtasks?.all { it.subtaskIsFinished == true } ?: false
            if (task.taskIsFinished == true || allSubtasksCompleted && task.subtasks?.isNotEmpty() == true) {
                task.taskIsFinished = true
                task.subtasks?.forEach { subtask ->
                    subtask.subtaskIsFinished = true
                }
                finishedTasks.add(task)
            }
        }

        return finishedTasks
    }

    fun getCurrentDateTask(date : String) : List<Task> {
        val currentDateTask = mutableListOf<Task>()

        for(task in tasks)
            if(task.taskDueDate == date && task.taskIsFinished == false)
                currentDateTask.add(task)

        return currentDateTask
    }

    fun checkIfDateHasEvent(date : String) : Int {
        return getCurrentDateTask(date).size
    }

}