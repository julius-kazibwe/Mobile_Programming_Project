package com.example.taskmanagementapplication.data

import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

object UserDataSource {

    fun getUserName() : String {
        return Firebase.auth.currentUser?.displayName.toString()
    }

    fun getUserPicture() : String {
        return Firebase.auth.currentUser?.photoUrl.toString()
    }

}
