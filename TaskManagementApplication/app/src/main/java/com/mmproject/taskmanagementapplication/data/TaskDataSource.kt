package com.mmproject.taskmanagementapplication.data

import android.annotation.SuppressLint
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.mmproject.taskmanagementapplication.domain.model.Task
import com.mmproject.taskmanagementapplication.presentation.task.updateList
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.mmproject.taskmanagementapplication.domain.model.Subtask

@SuppressLint("StaticFieldLeak")
object TaskDataSource {

    private const val USER_COLLECTION = "users"
    private const val TASK_COLLECTION = "tasks"

    fun getTasks(tasks: SnapshotStateList<Task>)  {
        FirebaseFirestore
            .getInstance()
            .collection(USER_COLLECTION)
            .document(Firebase.auth.currentUser?.uid.toString())
            .collection(TASK_COLLECTION)
            .addSnapshotListener { snapshot, e ->
            if(snapshot != null){
                tasks.updateList(snapshot.toObjects(Task::class.java))
            }
        }
    }

    fun getTask(
        taskId: String,
        onError : (Throwable?) -> Unit,
        onSuccess : (Task?) -> Unit
    ) {
        FirebaseFirestore
            .getInstance()
            .collection(USER_COLLECTION)
            .document(Firebase.auth.currentUser?.uid.toString())
            .collection(TASK_COLLECTION)
            .document(taskId)
            .get()
            .addOnSuccessListener {
                onSuccess.invoke(it?.toObject(Task::class.java))
            }
            .addOnFailureListener {result ->
                onError.invoke(result.cause)
            }
    }

    fun saveTask(
        taskName: String,
        taskDesc: String,
        taskDueDate: String,
        reminderDate: String,
        taskIsFinished: Boolean,
        taskId: String,
        subtasks: List<Subtask>,
    ){
        val task = Task (
            taskName = taskName,
            taskDescription = taskDesc,
            taskDueDate = taskDueDate,
            reminderDate = reminderDate,
            taskIsFinished = taskIsFinished,
            taskID = if(taskId == "") generateTaskID() else taskId,
            subtasks = subtasks,
        )

        task.taskID?.let {
            FirebaseFirestore
                .getInstance()
                .collection(USER_COLLECTION)
                .document(Firebase.auth.currentUser?.uid.toString())
                .collection(TASK_COLLECTION)
                .document(it)
                .set(task)
        }
    }

    fun deleteTask(taskID : String) {
        FirebaseFirestore
            .getInstance()
            .collection(USER_COLLECTION)
            .document(Firebase.auth.currentUser?.uid.toString())
            .collection(TASK_COLLECTION)
            .document(taskID)
            .delete()
    }

    fun finishTask(taskId : String, isFinished : Boolean) {
        FirebaseFirestore
            .getInstance()
            .collection(USER_COLLECTION)
            .document(Firebase.auth.currentUser?.uid.toString())
            .collection(TASK_COLLECTION)
            .document(taskId)
            .update("taskIsFinished", isFinished)
    }

    private fun generateTaskID(): String {
        val ref = Firebase.firestore
            .collection(USER_COLLECTION)
            .document(Firebase.auth.currentUser?.uid.toString())
            .collection(TASK_COLLECTION)
            .document()

        return ref.id
    }
}