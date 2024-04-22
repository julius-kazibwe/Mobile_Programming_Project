package com.mmproject.taskmanagementapplication.presentation.screens.calendar

import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.mmproject.taskmanagementapplication.R
import com.mmproject.taskmanagementapplication.presentation.navigations.NavScreen
import com.mmproject.taskmanagementapplication.presentation.task.TaskList
import com.mmproject.taskmanagementapplication.presentation.task.TaskListViewModel
import com.mmproject.taskmanagementapplication.presentation.utils.NoTaskDisplay
import com.mmproject.taskmanagementapplication.presentation.utils.SideDrawer
import com.mmproject.taskmanagementapplication.presentation.utils.TopBar
import com.mmproject.taskmanagementapplication.ui.theme.DarkGray
import com.mmproject.taskmanagementapplication.ui.theme.MainBackground
import com.mmproject.taskmanagementapplication.ui.theme.interBold
import com.kizitonwose.calendar.core.daysOfWeek

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun CalendarScreen(
    navController: NavController,
    openDrawer: () -> Unit,
    scaffoldState: ScaffoldState,
    calendarViewModel : CalendarViewModel,
    taskListViewModel: TaskListViewModel = viewModel(modelClass = TaskListViewModel::class.java),
) {

    LaunchedEffect(key1 = Unit){
        calendarViewModel.resetToCurrentDate()
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        scaffoldState = scaffoldState,
        topBar = { TopBar(navigationController = navController, openDrawer = openDrawer, topBarTitle = "CALENDAR") },
        floatingActionButton = {
            FloatingActionButton(
                backgroundColor = DarkGray,
                onClick = {
                    navController.navigate(NavScreen.AddEditTaskScreen.route)
                }) {
                Icon(Icons.Filled.Add, null, tint = Color.White)
            }
        },
        drawerContent = { SideDrawer(navController = navController) }
    ) {

        val calendarUiState = calendarViewModel.calendarUiState

        Column(modifier = Modifier
            .fillMaxSize()
            .background(MainBackground)
            .padding(PaddingValues(top = 75.dp))
        ) {
            Calendar(
                updateCurrMonthViewed = calendarViewModel::updateCurrentMonthViewed,
                updateCurrDateViewed = calendarViewModel::updateCurrentDateViewed,
                getDateEventCount = taskListViewModel::checkIfDateHasEvent,
                checkIfDateSelected = calendarViewModel::isDateSelected,
                resetCurrDate = calendarViewModel::resetToCurrentDate
            )

            Column(modifier = Modifier.padding(PaddingValues(start = 20.dp, end = 20.dp))) {
                Text(
                    modifier = Modifier.padding(PaddingValues(top = 30.dp, bottom = 5.dp)),
                    text = calendarUiState.label,
                    fontFamily = interBold,
                    fontSize = 20.sp
                )

                if(taskListViewModel.getCurrentDateTask(calendarUiState.currDateViewed).isNotEmpty())
                    TaskList(
                        navController = navController,
                        currDateTasks = calendarUiState.currDateViewed,
                    )
                else
                    NoTaskDisplay("No Tasks due on this day")
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .height(80.dp)
                .background(Color.White)
                .padding(PaddingValues(start = 5.dp, end = 5.dp)),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.Bottom
        ) {

            Text(
                modifier = Modifier.padding(PaddingValues(start = 15.dp, top = 10.dp)),
                text = calendarUiState.currMonthViewed,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = DarkGray,
                fontFamily = interBold
            )
            
            Spacer(modifier = Modifier
                .height(20.dp)
                .fillMaxWidth())

            DaysOfWeekTitle(daysOfWeek())

            Spacer(modifier = Modifier
                .height(5.dp)
                .fillMaxWidth())
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun Calendar(
    updateCurrMonthViewed : (String) -> Unit,
    updateCurrDateViewed: (String) -> Unit,
    getDateEventCount : (String) -> Int,
    checkIfDateSelected : (String) -> Boolean,
    resetCurrDate : () -> Unit
) {
    var mvisible by remember { mutableStateOf(false) }
    var wvisible by remember { mutableStateOf(true) }
    var weekvisible by remember { mutableStateOf(false) }
    var monthvisible by remember { mutableStateOf(true) }

    val density = LocalDensity.current

    Column(
        modifier = Modifier
            .shadow(10.dp, RoundedCornerShape(0.dp, 0.dp, 20.dp, 20.dp), clip = true)
            .clip(RoundedCornerShape(0.dp, 0.dp, 20.dp, 20.dp))
            .background(Color.White)
            .padding(PaddingValues(start = 5.dp, end = 5.dp)),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        AnimatedVisibility(
            visible = mvisible,
            enter = slideInVertically { with(density) { -40.dp.roundToPx() } } +
                    expandVertically( expandFrom = Alignment.Top) +
                    fadeIn( initialAlpha = 0.3f ),
            exit = fadeOut() + slideOutVertically() + shrinkVertically() + fadeOut()
        ) {
            CalenderMonth(
                updateCurrMonthViewed = updateCurrMonthViewed,
                updateCurrDateViewed = updateCurrDateViewed,
                dateEventCount = getDateEventCount,
                checkIfDateSelected = checkIfDateSelected)
        }

        AnimatedVisibility(
            visible = wvisible,
            enter = slideInVertically { with(density) { -40.dp.roundToPx() } } +
                    expandVertically( expandFrom = Alignment.Top ) +
                    fadeIn( initialAlpha = 0f ),
            exit = fadeOut() + slideOutVertically() + shrinkVertically()
        ) {
            CalendarWeek(
                updateCurrMonthViewed = updateCurrMonthViewed,
                updateCurrDateViewed = updateCurrDateViewed,
                dateEventCount = getDateEventCount,
                checkIfDateSelected = checkIfDateSelected
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            IconButton(
                enabled = monthvisible,
                onClick = {
                    mvisible = !mvisible
                    wvisible = !wvisible
                    weekvisible = !weekvisible
                    monthvisible = !monthvisible
                    resetCurrDate.invoke()
                }) {
                Icon(Icons.Filled.KeyboardArrowDown, null, tint = Color.Black)
            }
            IconButton(
                enabled = weekvisible,
                onClick = {
                    mvisible = !mvisible
                    wvisible = !wvisible
                    monthvisible= !monthvisible
                    weekvisible = !weekvisible
                    resetCurrDate.invoke()
                }) {
                Icon(Icons.Filled.KeyboardArrowUp, null, tint = Color.Black)
            }
        }
    }
}