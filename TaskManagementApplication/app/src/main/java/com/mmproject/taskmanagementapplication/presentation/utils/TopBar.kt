package com.mmproject.taskmanagementapplication.presentation.utils

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.mmproject.taskmanagementapplication.R
import com.mmproject.taskmanagementapplication.ui.theme.MainBackground
import com.mmproject.taskmanagementapplication.ui.theme.interBold
import com.mmproject.taskmanagementapplication.ui.theme.interRegular

@SuppressLint("UnrememberedMutableState")
@Composable
fun TopBar(
    navigationController: NavController,
    openDrawer:() -> Unit,
    topBarTitle : String,
) {

    Row(modifier = Modifier
        .fillMaxWidth()
        .background(
            when (navigationController.currentDestination?.route) {
                "home_screen" -> {
                    MainBackground
                }
                else -> {
                    Color.White
                }
            }
        )
        .padding(20.dp, 20.dp, 20.dp, 10.dp)
        .height(20.dp),
        horizontalArrangement = Arrangement.SpaceBetween) {

        Image(
            modifier = Modifier.clickable(onClick = {
                openDrawer.invoke()
            }),
            painter = painterResource(id = R.drawable.menu_icon),
            contentDescription = null)

        Text(
            text = topBarTitle,
            fontWeight = FontWeight.Bold,
            fontFamily = interBold,
            fontSize = 14.sp)

        Image(
            modifier = Modifier
                .clickable(onClick = {
                    if(navigationController.currentDestination?.route == "home_screen") {
                        navigationController.navigate("calendar_screen")
                    } else {
                        navigationController.navigate("home_screen")
                    }
                }),
            painter = if(topBarTitle == "HOME") painterResource(id = R.drawable.calendar) else painterResource(id = R.drawable.home),
            contentDescription = null)
    }
}

@Composable
fun TopBar(
    navigationController: NavController,
    topBarTitle : String,
) {

    Row(modifier = Modifier
        .fillMaxWidth()
        .background(MainBackground)
        .padding(20.dp, 20.dp, 20.dp, 10.dp)
        .height(20.dp)) {

        IconButton(
            onClick = { navigationController.popBackStack() },
        ) {
            Icon(Icons.Filled.ArrowBack, null, tint = Color.Black)
        }

        Spacer(modifier = Modifier.width(20.dp))

        Text(
            text = topBarTitle,
            fontWeight = FontWeight.Bold,
            fontFamily = interRegular,
            fontSize = 14.sp)
    }
}