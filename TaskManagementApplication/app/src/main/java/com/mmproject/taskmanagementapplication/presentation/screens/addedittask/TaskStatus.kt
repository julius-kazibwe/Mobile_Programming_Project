package com.mmproject.taskmanagementapplication.presentation.screens.addedittask

import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.mmproject.taskmanagementapplication.domain.model.Subtask

@Composable
fun TaskStatus(
    subtasks: List<Subtask>,
) {
    // Calculate number of completed subtasks
    val completedSubtasks = subtasks.count { it.subtaskIsFinished == true }

    // Calculate task status
    val taskStatus = when {
        completedSubtasks == 0 -> "Pending"
        completedSubtasks < subtasks.size -> "In Progress"
        else -> "Completed"
    }

    // Calculate progress percentage
    val progress = if (completedSubtasks > 0) {
        (completedSubtasks.toFloat() / subtasks.size.toFloat()) * 100
    } else {
        0f
    }

    // Display status label button
    Button(onClick = { /* Handle status button click if needed */ },
        colors = ButtonDefaults.buttonColors(
            backgroundColor = Color.Black,
            contentColor = Color.White
        )

    ) {
        Text(text = taskStatus)
    }

    // Display progress bar if task is in progress
    if (taskStatus == "In Progress") {
        LinearProgressIndicator(progress = progress / 100,
            color = Color.Blue,
            modifier = Modifier
                .height(8.dp)
                .clip(RoundedCornerShape(16.dp)),
            backgroundColor = Color.LightGray)
    }

    // Display completion score
    Text(text = "$completedSubtasks/${subtasks.size} subtasks completed")
}

