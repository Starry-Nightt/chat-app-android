package com.example.chat_appication.firebase

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.chat_appication.R
import com.example.chat_appication.activities.ChatRoomActivity
import com.example.chat_appication.model.User
import com.example.chat_appication.model.UserData
import com.example.chat_appication.shared.Constants
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.google.firebase.messaging.remoteMessage

private const val TAG = "FCM"

class MessagingService: FirebaseMessagingService() {
    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d(TAG, "Token: $token")
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        val userId = remoteMessage.data[Constants.KEY_USER_ID] as String
        val username = remoteMessage.data[Constants.KEY_NAME] as String
        val token = remoteMessage.data.get(Constants.KEY_TOKEN) as String

        val userData = UserData(id = userId, username = username, token = token)

        val notificationId = Math.random().toInt()

        val channelId = "chat_message"

        val intent = Intent(this, ChatRoomActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        intent.putExtra(Constants.KEY_USER, userData)

        val pendingIntent = PendingIntent.getActivity(this, 0 , intent, PendingIntent.FLAG_IMMUTABLE)
        val builder  = NotificationCompat.Builder(this, channelId)
        builder.setSmallIcon(R.drawable.ic_chat_bubble)
        builder.setContentTitle(username)
        builder.setContentText(remoteMessage.data[Constants.KEY_MESSAGE])
        builder.setPriority(NotificationCompat.PRIORITY_DEFAULT)
        builder.setContentIntent(pendingIntent)
        builder.setAutoCancel(true)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelName = " Chat message"
            val channelDescription = "This is notification used for chat message notifications"
            val important = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(channelId, channelName, important)
            channel.description = channelDescription
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }

        val notificationCompat = NotificationManagerCompat.from(this)
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        notificationCompat.notify(notificationId, builder.build())
    }
}