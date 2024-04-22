package com.mmproject.taskmanagementapplication.presentation

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.mmproject.taskmanagementapplication.R

const val notificationID = 121
const val channelID = "channel1"
const val description = ""
const val title = ""


class Notification : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {

        // Build the notification using NotificationCompat.Builder

        val notification = NotificationCompat.Builder(context, channelID)
            .setSmallIcon(R.drawable.check_circle_fill)
            .setContentTitle(intent.getStringExtra(title))
            .setContentText(intent.getStringExtra(description))
            .build()

        // Get the NotificationManager service
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Show the notification using the manager
        manager.notify(notificationID, notification)
    }
}