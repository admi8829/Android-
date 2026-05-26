package com.example.ui

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat

/**
 * Modern Jetpack Compose & Kotlin implementation for handling Firebase Cloud Messaging (FCM) 
 * dynamic runtime notification requests and notification channel initialization.
 */

/**
 * Initializes the default Notification Channel required for Android Oreo (API 26) and above.
 * Call this inside your MainActivity or Application subclass.
 */
fun createNotificationChannel(context: Context) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val channelId = "smartx_notifications_channel"
        val channelName = "Smart X Academy Announcements"
        val descriptionText = "Receives daily study updates, dynamic quiz announcements, and test preparation insights."
        val importance = NotificationManager.IMPORTANCE_HIGH
        
        val channel = NotificationChannel(channelId, channelName, importance).apply {
            description = descriptionText
            enableLights(true)
            enableVibration(true)
        }
        
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }
}

/**
 * Composable function to safely handle requesting POST_NOTIFICATIONS permissions on Android 13+ (API 33).
 * Auto-triggers when loaded or can be bound to action listeners.
 */
@Composable
fun NotificationPermissionHandler(
    onPermissionGranted: () -> Unit = {},
    onPermissionDenied: () -> Unit = {}
) {
    val context = LocalContext.current
    
    // Create the channel automatically
    LaunchedEffect(Unit) {
        createNotificationChannel(context)
    }

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        val permission = Manifest.permission.POST_NOTIFICATIONS
        
        var hasPermission by remember {
            mutableStateOf(
                ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED
            )
        }
        
        val launcher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestPermission()
        ) { isGranted ->
            hasPermission = isGranted
            if (isGranted) {
                onPermissionGranted()
            } else {
                onPermissionDenied()
            }
        }
        
        LaunchedEffect(Unit) {
            if (!hasPermission) {
                launcher.launch(permission)
            } else {
                onPermissionGranted()
            }
        }
    } else {
        // Automatically granted on pre-Android 13
        LaunchedEffect(Unit) {
            onPermissionGranted()
        }
    }
}
