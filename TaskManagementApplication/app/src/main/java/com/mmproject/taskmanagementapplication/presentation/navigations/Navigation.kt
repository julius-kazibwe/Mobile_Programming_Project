package com.mmproject.taskmanagementapplication.presentation.navigations

import android.annotation.SuppressLint
import android.app.Activity.RESULT_OK
import android.content.Context
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.google.android.gms.auth.api.identity.Identity
import androidx.lifecycle.lifecycleScope
import com.mmproject.taskmanagementapplication.presentation.screens.addedittask.AddEditTaskScreen
import com.mmproject.taskmanagementapplication.presentation.screens.addedittask.AddEditTaskViewModel
import com.mmproject.taskmanagementapplication.presentation.screens.calendar.CalendarScreen
import com.mmproject.taskmanagementapplication.presentation.screens.calendar.CalendarViewModel
import com.mmproject.taskmanagementapplication.presentation.screens.home.HomeScreen
import com.mmproject.taskmanagementapplication.presentation.screens.home.HomeViewModel
import com.mmproject.taskmanagementapplication.presentation.screens.splash.SplashScreen
import com.mmproject.taskmanagementapplication.presentation.screens.signin.SignInScreen
import com.mmproject.taskmanagementapplication.presentation.screens.signin.SignInViewModel
import com.mmproject.taskmanagementapplication.presentation.screens.signin.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch


@SuppressLint("UnrememberedMutableState", "SuspiciousIndentation")
@RequiresApi(Build.VERSION_CODES.N)
@Composable
fun Navigation(
    navigationController : NavHostController = rememberNavController(),
    addEditTaskViewModel: AddEditTaskViewModel,
    homeViewModel: HomeViewModel,
    calendarViewModel: CalendarViewModel,
    coroutineScope: CoroutineScope = rememberCoroutineScope(),
) {
    val context = LocalContext.current

    val googleAuthUiClient by remember {
        mutableStateOf(
            GoogleAuthUiClient(
                context = context,
                oneTapClient = Identity.getSignInClient(context)
            )
        )
    }

    NavHost(navController = navigationController, startDestination = NavScreen.SplashScreen.route){
        composable(route = NavScreen.SplashScreen.route){
            SplashScreen(navigationController)
        }
        composable(route = NavScreen.SignInScreen.route){
            
                val viewModel = viewModel<SignInViewModel>()
                val state by viewModel.state.collectAsState()

                LaunchedEffect(key1 = Unit) {
                    if(googleAuthUiClient.getSignedInUser() != null) {
                        navigationController.navigate("home_screen")
                    }
                }

                val launcher = rememberLauncherForActivityResult(
                    contract = ActivityResultContracts.StartIntentSenderForResult(),
                    onResult = { result ->
                        if(result.resultCode == RESULT_OK) {
                            coroutineScope.launch {
                                val signInResult = googleAuthUiClient.signInWithIntent(
                                    intent = result.data ?: return@launch
                                )
                                viewModel.onSignInResult(signInResult)
                            }
                        }
                    }
                )

                LaunchedEffect(key1 = state.isSignInSuccessful) {
                    if(state.isSignInSuccessful) {
                        Toast.makeText(
                            context,
                            "Sign in successful",
                            Toast.LENGTH_LONG
                        ).show()

                        navigationController.navigate("home_screen")
                        viewModel.resetState()
                    }
                }

                SignInScreen(
                    state = state,
                    onSignInClick = {
                        coroutineScope.launch {
                            val signInIntentSender = googleAuthUiClient.signIn()
                            launcher.launch(
                                IntentSenderRequest.Builder(
                                    signInIntentSender ?: return@launch
                                ).build()
                            )
                        }
                    }
                )

        }
        composable(route = NavScreen.HomeScreen.route){
            val scaffoldState : ScaffoldState = rememberScaffoldState()
            HomeScreen(
                navController = navigationController,
                openDrawer = {coroutineScope.launch { scaffoldState.drawerState.open() } },
                scaffoldState = scaffoldState,
                homeViewModel = homeViewModel,
            )
        }
        composable(
            route = NavScreen.AddEditTaskScreen.route + "?taskId={taskId}&onEditTask={onEditTask}",
            arguments = listOf(
                navArgument("taskId") {
                    type = NavType.StringType
                    defaultValue = ""
                },
                navArgument("onEditTask") {
                    defaultValue = true
                    type = NavType.BoolType
                }
            )
        ){ backStackEntry ->

            val taskId = backStackEntry.arguments?.getString("taskId").toString()
            val onEditTask = backStackEntry.arguments?.getBoolean("onEditTask") == true

            if(taskId != "") addEditTaskViewModel.getTask(taskId)
            else addEditTaskViewModel.resetState()

            AddEditTaskScreen(
                navController = navigationController,
                addEditTaskViewModel = addEditTaskViewModel,
                taskID = taskId,
                onEditTask = mutableStateOf(onEditTask),
            )
        }

        composable(route = NavScreen.CalendarScreen.route){
            val scaffoldState : ScaffoldState = rememberScaffoldState()
            CalendarScreen(
                navController = navigationController,
                openDrawer = {coroutineScope.launch { scaffoldState.drawerState.open() } },
                scaffoldState = scaffoldState,
                calendarViewModel = calendarViewModel,
            )
        }
    }
}