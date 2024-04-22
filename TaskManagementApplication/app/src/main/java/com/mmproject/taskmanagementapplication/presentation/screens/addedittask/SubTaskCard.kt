package com.mmproject.taskmanagementapplication.presentation.screens.addedittask

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Checkbox
import androidx.compose.material.CheckboxDefaults
import androidx.compose.material.Icon
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.TextFieldColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.mmproject.taskmanagementapplication.R
import com.mmproject.taskmanagementapplication.domain.model.Subtask
import me.saket.swipe.SwipeAction
import me.saket.swipe.SwipeableActionsBox


@Composable
fun SubtaskCard(
    subtask: Subtask,
    onSubtaskNameChange: (String) -> Unit,
    onDeleteSubtask: (Int) -> Unit,
    onSubtaskCheckboxChange: (Boolean) -> Unit,
    onEditSubtask: (Boolean) -> Unit,
    index: Int,
    textFieldColors: TextFieldColors,
    onEditTask: Boolean
) {

    val editSubtask = SwipeAction(
        onSwipe = {
            onEditSubtask(true)
        },
        icon = {
            Icon(
                modifier = Modifier.padding(16.dp),
                painter = painterResource(id = R.drawable.pencil_square),
                contentDescription = null)
        },
        background = Color(0XFFFFF9C5)
    )

    val deleteSubtask = SwipeAction(
        onSwipe = {
            onDeleteSubtask(index)

        },
        icon = {
            Icon(
                modifier = Modifier.padding(16.dp),
                painter = painterResource(id = R.drawable.trash3),
                contentDescription = null)
        },
        background = Color(0XFFFF9595)
    )
    SwipeableActionsBox(
        modifier = Modifier
            .shadow(10.dp, RoundedCornerShape(10.dp))
            .clip(RoundedCornerShape(10.dp))
            .background(Color.Transparent) ,
        swipeThreshold = 100.dp,
        startActions = listOf(editSubtask),
        endActions = listOf(deleteSubtask)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(75.dp)
                .background(Color.White)
                .padding(horizontal = 20.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = subtask.subtaskIsFinished ?: false,
                onCheckedChange = { isChecked ->
                    onSubtaskCheckboxChange(isChecked)
                },
                colors = CheckboxDefaults.colors(Color.Black),
                enabled = subtask.isEditing!! && onEditTask
            )

            Column(
                modifier = Modifier.padding(start = 16.dp),
                horizontalAlignment = Alignment.Start
            ) {
                OutlinedTextField(
                    value = subtask.subtaskName ?: "",
                    onValueChange = onSubtaskNameChange,
                    placeholder = { Text("Subtask Title") },
                    modifier = Modifier
                        .fillMaxWidth().defaultMinSize(minHeight = 70.dp)
                        .clickable { onEditSubtask(true) } ,
                    maxLines = 1,
                    colors = textFieldColors,
                    enabled = subtask.isEditing!! && onEditTask
                )
            }

        }

    }
}
