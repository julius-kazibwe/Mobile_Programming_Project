package com.mmproject.taskmanagementapplication.presentation.utils

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.AlertDialog
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.mmproject.taskmanagementapplication.ui.theme.*
import com.mmproject.taskmanagementapplication.presentation.screens.signin.GoogleAuthUiClient
import com.google.android.gms.auth.api.identity.Identity
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch

@Composable
fun SignOutButton(navController: NavController) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    val googleAuthUiClient by remember {
        mutableStateOf(
            GoogleAuthUiClient(
                context = context,
                oneTapClient = Identity.getSignInClient(context)
            )
        )
    }
    var showDialog by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Bottom,
        horizontalAlignment = Alignment.CenterHorizontally) {

        Box(modifier = Modifier
            .fillMaxWidth()
            .height(60.dp)
            .background(DarkGray)
            .clickable(onClick = {
                if (Firebase.auth.currentUser != null) {
                    showDialog = true
                }
            }),
            contentAlignment = Alignment.Center
        ) {

            Text(text = "Sign Out", fontFamily = interMedium, fontSize = 15.sp , color = Color.White)
        }
    }

    if(showDialog){
        AlertDialog(
            onDismissRequest = {  },
            title = {
                Text(
                    text = "Sign out confirmation",
                    fontFamily = interBold,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    text = "Are you sure you want to sign out?",
                    fontFamily = interRegular,
                    fontSize = 12.sp
                )
            },
            confirmButton = {
                Text(
                    modifier = Modifier
                        .padding(10.dp)
                        .clickable(onClick = {
                            showDialog = false
                            coroutineScope.launch {
                                googleAuthUiClient.signOut()
                            }

                            navController.navigate("signin_screen")
                        }),
                    text = "Sign Out",
                    fontWeight = FontWeight.Medium,
                    fontFamily = interMedium,
                    fontSize = 12.sp,
                )
            },
            dismissButton = {
                Text(
                    modifier = Modifier
                        .padding(10.dp)
                        .clickable(onClick = {
                            showDialog = false
                        }),
                    text = "Cancel",
                    fontWeight = FontWeight.Normal,
                    fontFamily = interRegular,
                    color = Color(0XFF909090),
                    fontSize = 12.sp,)
            },
        )
    }
}