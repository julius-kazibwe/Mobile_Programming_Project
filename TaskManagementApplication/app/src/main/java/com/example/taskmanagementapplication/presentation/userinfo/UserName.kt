package com.example.taskmanagementapplication.presentation.userinfo

import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.sp
import com.example.taskmanagementapplication.presentation.userinfo.UserViewModel
import com.example.taskmanagementapplication.ui.theme.DarkGray
import com.example.taskmanagementapplication.ui.theme.interBold
import com.example.taskmanagementapplication.ui.theme.interLight
import androidx.lifecycle.viewmodel.compose.viewModel


@Composable
fun UserName(viewModel : UserViewModel = viewModel()){

    Text(text = buildAnnotatedString {
        append("Hello, ")
        withStyle(
            style = SpanStyle(
                fontWeight = FontWeight.Bold,
                fontFamily = interBold,
                fontSize = 26.sp
            )
        ) {
            append("\n${viewModel.userName}!")
        }
    },
        fontWeight = FontWeight.Light,
        fontSize = 20.sp,
        fontFamily = interLight,
        color = DarkGray,
    )
}