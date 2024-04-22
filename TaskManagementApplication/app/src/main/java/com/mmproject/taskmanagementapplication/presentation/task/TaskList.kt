package com.mmproject.taskmanagementapplication.presentation.task

import android.annotation.SuppressLint
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.mmproject.taskmanagementapplication.R
import com.mmproject.taskmanagementapplication.domain.model.Task
import com.mmproject.taskmanagementapplication.presentation.navigations.NavScreen
import com.mmproject.taskmanagementapplication.presentation.screens.addedittask.TaskStatus
import com.mmproject.taskmanagementapplication.presentation.screens.addedittask.AddEditTaskViewModel
import com.mmproject.taskmanagementapplication.ui.theme.DarkGray
import com.mmproject.taskmanagementapplication.ui.theme.interBold
import com.mmproject.taskmanagementapplication.ui.theme.interLight
import com.mmproject.taskmanagementapplication.ui.theme.interMedium
import me.saket.swipe.SwipeAction
import me.saket.swipe.SwipeableActionsBox
import org.intellij.lang.annotations.JdkConstants.HorizontalAlignment

@SuppressLint("UnrememberedMutableState")
@Composable
fun TaskList(
    currDateTasks : String = "",
    displayFinishedTask : Boolean = false,
    navController: NavController,
    taskListViewModel: TaskListViewModel = viewModel(modelClass = TaskListViewModel::class.java),
){

        LazyColumn(
            contentPadding = PaddingValues(top = 20.dp, bottom = 20.dp),
            verticalArrangement = Arrangement.spacedBy(15.dp)){

            itemsIndexed(
                items =
                if(currDateTasks != "") taskListViewModel.getCurrentDateTask(currDateTasks)
                else if(displayFinishedTask) taskListViewModel.getFinishedTasks()
                else taskListViewModel.getUnfinishedTasks(),
                itemContent = {_, item->
                    AnimatedVisibility(
                        visible = !taskListViewModel.deletedTasks.contains(item.taskID),
                        enter = slideInHorizontally() + fadeIn(),
                        exit = slideOutHorizontally() + fadeOut()
                    ) {
                        if(item.taskID != ""){
                            TaskCard(
                                task = item,
                                navController = navController,
                                onDeleteTask = taskListViewModel::deleteTask,
                                onFinishTask = taskListViewModel::finishTask
                            )
                        }
                    }
                }
            )
        }

}

@Composable
fun TaskCard(
    task : Task,
    navController: NavController,
    onDeleteTask: (String) -> Unit,
    onFinishTask: (String, Boolean) -> Unit
){

    val params = ("?taskId=${task.taskID}")

    val editTask = SwipeAction(
        onSwipe = {
            navController.navigate(NavScreen.AddEditTaskScreen.route + params + "&onEditTask=true")
        },
        icon = {
            Icon(
                modifier = Modifier.padding(16.dp),
                painter = painterResource(id = R.drawable.pencil_square),
                contentDescription = null)
        },
        background = Color(0XFFFFF9C5)
    )

    val deleteTask = SwipeAction(
        onSwipe = {
            task.taskID?.let { onDeleteTask(it) }
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
            .background(Color.Transparent)
            .clickable(onClick = {
                navController.navigate(NavScreen.AddEditTaskScreen.route + params + "&onEditTask=false")
            }),
        swipeThreshold = 100.dp,
        startActions = listOf(editTask),
        endActions = listOf(deleteTask)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
        ) {
            val taskId = task.taskID
            val isFinished = task.taskIsFinished

            if (isFinished != null) {
                Checkbox(
                    checked = isFinished,
                    onCheckedChange = {
                        if (taskId != null)
                            onFinishTask(taskId, it)
                    },
                    colors = CheckboxDefaults.colors(Color.Black)
                )
            }

            task.taskName?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.h6,
                    fontSize = 24.sp,
                    fontFamily = interBold,
                    fontWeight = FontWeight.Medium,
                    color = DarkGray,
                    textDecoration =
                    if(task.taskIsFinished == true) TextDecoration.LineThrough
                    else TextDecoration.None ,
                    modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                )
            }

            task.subtasks?.let {
                TaskStatus(
                    it
                )

            }

            task.taskDueDate?.let {
                Text(
                    text = if (it != "No due date") "Due on $it" else "No due date",
                    fontFamily = interLight,
                    fontWeight = FontWeight.Light,
                    fontSize = 12.sp,
                    color = DarkGray,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            }
        }

    }
}


fun <T> SnapshotStateList<T>.updateList(newList: List<T>){
    clear()
    addAll(newList)
}