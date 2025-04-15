package com.example.late_plate.viewModel

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.late_plate.R

class AlarmNotificationHelper(
    private val context: Context
) {
    fun showTimerFinishedNotification(key: RecipeTimerKey) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE)
                as NotificationManager

        val channelId = "timer_channel"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Timer Notifications",
                NotificationManager.IMPORTANCE_HIGH // Ensures sound and alert
            ).apply {
                description = "Shows timer completion alerts"
                enableVibration(true)
                setShowBadge(true)
            }
            notificationManager.createNotificationChannel(channel) // Ensure the channel is created
        }

        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.late_plat_logo)
            .setContentTitle("${key.recipeName} - Step ${key.stepIndex + 1} Complete!")
            .setContentText("Timer for step ${key.stepIndex + 1} has finished")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(key.hashCode(), notification) // Use a valid integer ID
    }
}
