package com.mmproject.taskmanagementapplication.presentation.screens.addedittask

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.DatePickerDialog
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.icu.util.Calendar
import android.os.Build
import android.provider.Settings
import android.widget.DatePicker
import androidx.annotation.RequiresApi
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Edit
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.NotificationManagerCompat
import androidx.navigation.NavController
import com.mmproject.taskmanagementapplication.R
import com.mmproject.taskmanagementapplication.domain.model.Subtask
import com.mmproject.taskmanagementapplication.presentation.Notification
import com.mmproject.taskmanagementapplication.presentation.utils.TopBar
import com.mmproject.taskmanagementapplication.ui.theme.DarkGray
import com.mmproject.taskmanagementapplication.ui.theme.MainBackground
import com.mmproject.taskmanagementapplication.ui.theme.interBold
import dev.jeziellago.compose.markdowntext.MarkdownText
import java.time.Duration
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@RequiresApi(Build.VERSION_CODES.N)
@Composable
fun AddEditTaskScreen(
    navController: NavController,
    addEditTaskViewModel: AddEditTaskViewModel,
    taskID: String,
    onEditTask: MutableState<Boolean> = mutableStateOf(true),
    scaffoldState: ScaffoldState = rememberScaffoldState(),
) {


    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(MainBackground),
        scaffoldState = scaffoldState,
        backgroundColor = MainBackground,
        topBar = { TopBar(
            navigationController = navController,
            topBarTitle =
            if(taskID != "") {
                if(onEditTask.value) "Edit Task"
                else "Task Details"
            }
            else "Add task"
        )},
        floatingActionButton = {
            FloatingActionButton(
                backgroundColor = DarkGray,
                onClick = {
                if(onEditTask.value) {
                    addEditTaskViewModel.saveTask()
                    addEditTaskViewModel.resetState()
                    onEditTask.value = false

                    navController.popBackStack()
                }else{
                    onEditTask.value = true
                }
            }) {
                if(onEditTask.value) Icon(Icons.Filled.Check, null, tint = Color.White)
                else Icon(Icons.Filled.Edit, null, tint = Color.White)
            }
        }
    ) {

        val addEditTaskUiState = addEditTaskViewModel.addEditTaskUiState

        fun handleAddSubtask(subtaskName: String, subtaskIsFinished: Boolean, isEditing: Boolean) {
            val newSubtask = Subtask(subtaskName, subtaskIsFinished, isEditing)
            addEditTaskViewModel.addSubtask(newSubtask)
        }

        AddEditTaskContent(
            taskName = addEditTaskUiState.taskName,
            taskDescription = addEditTaskUiState.taskDescription,
            taskDueDate = addEditTaskUiState.taskDueDate,
            viewAsMarkdown = addEditTaskUiState.viewAsMarkdown,
            onTaskNameChange = addEditTaskViewModel::updateName,
            onTaskDescChange = addEditTaskViewModel::updateDescription,
            onTaskDueDateChange = addEditTaskViewModel::updateDueDate,
            onEditTask = onEditTask.value,
            onViewChange = addEditTaskViewModel::updateDescriptionView,
            onAddSubtask = ::handleAddSubtask,
            subtasks = addEditTaskUiState.subtasks,
            addEditTaskViewModel = addEditTaskViewModel
        )
    }
}

@SuppressLint("UnrememberedMutableState")
@RequiresApi(Build.VERSION_CODES.N)
@Composable
fun AddEditTaskContent(
    taskName: String,
    taskDescription: String,
    taskDueDate: String,
    viewAsMarkdown: Boolean,
    onTaskNameChange: (String) -> Unit,
    onTaskDescChange: (String) -> Unit,
    onTaskDueDateChange: (String) -> Unit,
    onEditTask: Boolean,
    onViewChange: () -> Unit,
    subtasks: List<Subtask>,
    onAddSubtask: (String, Boolean, Boolean) -> Unit,
    addEditTaskViewModel: AddEditTaskViewModel,

) {

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(PaddingValues(top = 20.dp, start = 10.dp, end = 10.dp))
            .verticalScroll(rememberScrollState())
    ) {
        val textFieldColors = TextFieldDefaults.outlinedTextFieldColors(
            focusedBorderColor = Color.Transparent,
            unfocusedBorderColor = Color.Transparent,
            cursorColor = DarkGray,
            disabledBorderColor = Color.Transparent,
            disabledTextColor = DarkGray,
        )

        TaskStatus(
            subtasks
        )

        InputTaskName(
            taskName = taskName,
            onTaskNameChange = onTaskNameChange,
            textFieldColors = textFieldColors,
            onEditTask = onEditTask
        )
        InputTaskDueDate(
            taskDueDate = taskDueDate,
            onTaskDueDateChange = onTaskDueDateChange,
            onEditTask = onEditTask
        )

        if(viewAsMarkdown){
            MarkdownText(
                modifier = Modifier.padding(16.dp),
                markdown = taskDescription,
                fontSize = 16.sp,
                color = DarkGray,
                maxLines = 20,
                style = MaterialTheme.typography.overline,
            )
        }
        else{
            InputTaskDescription(
                taskDescription = taskDescription,
                onTaskDescChange = onTaskDescChange,
                textFieldColors = textFieldColors,
                onEditTask = onEditTask
            )
        }

        SubtasksRow(onAddSubtask = onAddSubtask, onEditTask = onEditTask)

        subtasks.forEachIndexed { index, subtask ->
            SubtaskCard(
                subtask = subtask,
                index = index,
                textFieldColors = textFieldColors,
                onSubtaskNameChange = { newName -> addEditTaskViewModel.updateSubtaskName(subtask, newName) },
                onSubtaskCheckboxChange = { isChecked -> addEditTaskViewModel.updateSubtaskCheckbox(subtask, isChecked) },
                onDeleteSubtask = { indexToDelete -> addEditTaskViewModel.deleteSubtask(indexToDelete) },
                onEditSubtask = {isEditing -> addEditTaskViewModel.onEditSubtask(subtask, isEditing)},
                onEditTask = onEditTask
            )
        }
    }

}
@Composable
fun InputTaskName(
    taskName : String,
    onTaskNameChange: (String) -> Unit,
    textFieldColors: TextFieldColors,
    onEditTask: Boolean
){
    OutlinedTextField(
        value = taskName,
        modifier = Modifier.fillMaxWidth().defaultMinSize(minHeight = 70.dp),
        onValueChange = onTaskNameChange,
        placeholder = {
            Text(
                text = "Title",
                style = MaterialTheme.typography.h6,
                fontSize = 24.sp,
                fontFamily = interBold
            )
        },
        textStyle = MaterialTheme.typography.h6.copy(
            fontWeight = FontWeight.Bold,
            fontSize = 24.sp,
            fontFamily = interBold
        ),
        maxLines = 1,
        colors = textFieldColors,
        enabled = onEditTask,
    )
}

@Composable
fun InputTaskDescription(
    taskDescription: String,
    onTaskDescChange: (String) -> Unit,
    textFieldColors: TextFieldColors,
    onEditTask: Boolean
){
    OutlinedTextField(
        value = taskDescription.replace("\\n", "\n"),
        onValueChange = onTaskDescChange,
        placeholder = { Text("Enter task description here.") },
        modifier = Modifier
            .height(150.dp)
            .fillMaxWidth(),
        colors = textFieldColors,
        enabled = onEditTask
    )
}


@RequiresApi(Build.VERSION_CODES.N)
@Composable
fun InputTaskDueDate(
    taskDueDate: String,
    onTaskDueDateChange: (String) -> Unit,
    onEditTask: Boolean
) {
    val context = LocalContext.current
    val selectedDate = remember { mutableStateOf("") }

    val year : Int
    val month : Int
    val day : Int
    val hour: Int
    val minute: Int

    val calendar = Calendar.getInstance()
    year = calendar.get(Calendar.YEAR)
    month = calendar.get(Calendar.MONTH)
    day = calendar.get(Calendar.DAY_OF_MONTH)
    hour = calendar.get(Calendar.HOUR_OF_DAY)
    minute = calendar.get(Calendar.MINUTE)


    val datePickerDialog = DatePickerDialog(
        context,
        { _: DatePicker, year : Int, month : Int, dayOfMonth : Int ->

            var y = year.toString()
            var m = (month + 1).toString().padStart(2, '0')
            var d = dayOfMonth.toString().padStart(2, '0')

            selectedDate.value = "$y-$m-$d" // Store selected date

        }, year, month, day
    )

    val timePickerDialog = TimePickerDialog(
        context,
        { _, hourOfDay: Int, minute: Int ->
            var h = hourOfDay.toString().padStart(2, '0')
            var min = minute.toString().padStart(2, '0')

            val selectedTime = "$h:$min"
            val updatedDueDate = "${selectedDate.value} at $selectedTime" // Combine date and time
            onTaskDueDateChange(updatedDueDate)
            setNotificationForReminder(context, updatedDueDate)
        },
        hour,
        minute,
        true
    )

    Row(modifier = Modifier
        .fillMaxWidth()
        .height(50.dp)
        .padding(PaddingValues(start = 1.dp)),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween) {

        Text(text = if(taskDueDate != "No due date") "Due on $taskDueDate" else "No due date",
            color = Color.DarkGray,
            modifier = Modifier
                .padding(PaddingValues(start = 15.dp)))

        Image(
            modifier = Modifier
                .alpha(0.7f)
                .padding(PaddingValues(end = 15.dp))
                .clickable(
                    onClick = {
                        if (onEditTask) {
                            if (taskDueDate == "No due date") {
                                datePickerDialog.show()
                                datePickerDialog.setOnDismissListener {
                                    timePickerDialog.show()
                                }
                            } else onTaskDueDateChange("No due date")
                        }
                    }),
            painter = painterResource(
                id = if (taskDueDate != "No due date" && onEditTask) R.drawable.cancel
                     else R.drawable.calendar
            ),
            contentDescription = null
        )
    }
}

@Composable
fun ViewAsMarkDown(
    viewAsMarkdown: Boolean,
    onViewChange: () -> Unit
){
    Row(modifier = Modifier.width(200.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Checkbox(
            checked = viewAsMarkdown,
            onCheckedChange = {
                onViewChange.invoke()
            },
            colors = CheckboxDefaults.colors(Color.Black)
        )

        Text(text = "View as markdown")
    }
}
@Composable
fun SubtasksRow(onAddSubtask: (String, Boolean, Boolean) -> Unit, onEditTask: Boolean) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 20.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = "Subtasks",
            modifier = Modifier.padding(start = 16.dp),
            style = MaterialTheme.typography.h6.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp
            )
        )

        IconButton(
            onClick = { onAddSubtask("",false,true) },
            modifier = Modifier.padding(end = 16.dp),
            enabled = onEditTask
        ) {
            Icon(Icons.Filled.AddCircle, null, tint = Color.Black)
        }

    }
}
@RequiresApi(Build.VERSION_CODES.O)
fun setNotificationForReminder(context: Context, dueDateTime: String) {
    // Parse dueDateTime
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd 'at' HH:mm")
    val dateTime = LocalDateTime.parse(dueDateTime, formatter)

    // Get current time
    val currentTime = LocalDateTime.now()

    // Calculate time difference
    val diff = Duration.between(currentTime, dateTime).toMillis()

    // Set notification if the due time is in the future
    if (diff > 0) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, Notification::class.java)
        intent.putExtra("title", "Your task reminder")
        intent.putExtra("description", "Remember to complete your task!")

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )

        alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + diff, pendingIntent)
    }
}
