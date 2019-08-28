package com.wakemeuphere.internal

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.provider.Settings.System.getString
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat.getSystemService
import java.security.AccessController.getContext
import com.wakemeuphere.R

class AlarmNotification {
    var contentTitle: String = ""
    var contentText: String = ""
    var CANAL_BRASIL: Int = 999
    var channelId: String = "reminder_1"
    var channelName: String = "Lembrança"

    constructor(){
        this.contentTitle = "TESTE"
        this.contentText = "ESSE É O TEXTO DO TESTE"
    }

    fun createNotification(context: Context): Notification{
        var channelId = this.createNotificationChannel(context)
        var builder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.ic_arrow_down_24dp)
            .setContentTitle(this.contentTitle)
            .setContentText(this.contentText)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT).build()

        return builder
    }

    fun showNotification(context: Context, notification: Notification){
        with(NotificationManagerCompat.from(context)) {
            // notificationId is a unique int for each notification that you must define
            notify(CANAL_BRASIL, notification)
        }

    }

    private fun createNotificationChannel(context: Context): String {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val name = context.getString(R.string.CANAL_BRASIL)
            val descriptionText = context.getString(R.string.CANAL_BRASIL_DESCRIPTION)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(this.channelId, name, importance).apply {
                description = descriptionText
            }
            // Register the channel with the system
            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
            return this.channelId
        } else
            return ""
    }

}