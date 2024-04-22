package com.mmproject.taskmanagementapplication.presentation

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.AlertDialog
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts.RequestPermission
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Surface
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mmproject.taskmanagementapplication.presentation.navigations.Navigation
import com.mmproject.taskmanagementapplication.presentation.screens.addedittask.AddEditTaskViewModel
import com.mmproject.taskmanagementapplication.presentation.screens.calendar.CalendarViewModel
import com.mmproject.taskmanagementapplication.presentation.screens.home.HomeViewModel
import com.mmproject.taskmanagementapplication.ui.theme.MainBackground
import com.mmproject.taskmanagementapplication.ui.theme.TaskManagementApplicationTheme
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import java.util.Date

class MainActivity : ComponentActivity() {

    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private val lat = mutableStateOf(0.0)
    private val long = mutableStateOf(0.0)
    private val hasLocationAccess = mutableStateOf(false)

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        createNotificationChannel()

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        getCurrentLocation()

        setContent {

            val addEditTaskViewModel = viewModel(modelClass = AddEditTaskViewModel::class.java)
            val homeViewModel = viewModel(modelClass = HomeViewModel::class.java)
            val calendarViewModel = viewModel(modelClass = CalendarViewModel::class.java)

            if(isOnline(this)) homeViewModel.updateNetworkAccess(true)
            if(hasLocationAccess.value) homeViewModel.updateLocationAccess(true)

            if(isOnline(this) && hasLocationAccess.value){
                homeViewModel.getWeatherData(lat.value, long.value)
                homeViewModel.getUserLocation(lat.value, long.value)
            }

            TaskManagementApplicationTheme {
                Surface(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MainBackground),
                    color = Color.White
                ) {
                    Navigation(
                        addEditTaskViewModel = addEditTaskViewModel,
                        homeViewModel = homeViewModel,
                        calendarViewModel = calendarViewModel,
                    )
                }
            }
        }
    }

    private fun isOnline(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (connectivityManager != null) {
            val capabilities =
                connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
            if (capabilities != null) {
                if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                    return true
                } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                    return true
                } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)) {
                    return true
                }
            }
        }
        return false
    }

    private fun getCurrentLocation() {
        if(checkPermissions()){
            if(isLocationEnabled()){
                if (ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    requestPermission()
                    return
                }
                fusedLocationProviderClient.lastLocation.addOnCompleteListener(this){ task ->
                    val location : Location?  = task.result
                    if(location == null) {
                        Toast.makeText(applicationContext, "Unable to retrieve location", Toast.LENGTH_SHORT).show()
                    }
                    else{
                        lat.value = location.latitude
                        long.value = location.longitude
                        hasLocationAccess.value = true
                    }
                }
            }
//            else{
//                Toast.makeText(applicationContext, "Turn on Location", Toast.LENGTH_SHORT).show()
//                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
//                startActivity(intent)
//            }
        }
        else{
            requestPermission()
        }
    }

    companion object {
        private  const val PERMISSION_REQUEST_ACCESS_LOCATION= 100
    }

    private fun isLocationEnabled() : Boolean {
        val locationManager : LocationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) && locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER)
    }

    private fun requestPermission() {
        ActivityCompat.requestPermissions(
            this, arrayOf( Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION),
            PERMISSION_REQUEST_ACCESS_LOCATION
        )
    }

    private fun checkPermissions() : Boolean {
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            return true
        }
        return false
    }

    private val requestPermissionLauncher = registerForActivityResult(RequestPermission()) { isGranted ->
        if (isGranted) {
            Toast.makeText(applicationContext, "Granted", Toast.LENGTH_SHORT).show()
            getCurrentLocation()
        } else {
            Toast.makeText(applicationContext, "Denied", Toast.LENGTH_SHORT).show()
        }
    }
    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel() {

        val name = "Notify Channel"
        val desc = "A Description of the Channel"
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val channel = NotificationChannel(channelID, name, importance)
        channel.description = desc

        // Get the NotificationManager service and create the channel
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }


    private val onBackPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            // Implement your logic here
            // For example, you can show a dialog to confirm the action
            // or navigate to a different screen
        }
    }
}