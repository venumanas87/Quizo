package com.quizo.utils

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.RingtoneManager
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.quizo.R
import com.quizo.activity.MainActivity
import com.quizo.activity.SharedeleClass
import okhttp3.internal.notify
import java.net.HttpURLConnection
import java.net.URL

@SuppressLint("MissingFirebaseInstanceTokenRefresh")
class MyFirebaseMessagingService : FirebaseMessagingService() {
    var bitmap: Bitmap? = null
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        var notificationTitle: String? = null
        var notificationBody: String? = null
        var imageUrl:String? = null

        // Check if message contains a notification payload
        if (remoteMessage.notification != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.data)
            val name = FirebaseAuth.getInstance().currentUser?.displayName.toString()
            notificationTitle = remoteMessage.notification!!.title.toString()
            notificationBody = remoteMessage.notification!!.body
            imageUrl = remoteMessage.notification!!.imageUrl.toString()

        }

        // If you want to fire a local notification (that notification on the top of the phone screen)
        // you should fire it from here
        sendLocalNotification(notificationTitle, notificationBody,imageUrl,12)
    }

     fun sendLocalNotification(notificationTitle: String?, notificationBody: String?,imgurl:String?,id:Int) {
        val intent = Intent(applicationContext, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent = PendingIntent.getActivity(
                this, 0, intent,
                PendingIntent.FLAG_ONE_SHOT
        )
        val notificationBuilder: Notification.Builder? = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            Notification.Builder(this,Constants.daily_updates_message_channel)
                    .setAutoCancel(true) //Automatically delete the notification
                    .setSmallIcon(R.drawable.ic_quizo_noti)
                    .setLargeIcon(getBitmapfromUrl(imgurl))
                    .setContentIntent(pendingIntent)
                    .setContentTitle(notificationTitle)
                    .setStyle(Notification.BigTextStyle().bigText(notificationBody))
        } else {
            Notification.Builder(this)
                    .setAutoCancel(true) //Automatically delete the notification
                    .setSmallIcon(R.drawable.ic_quizo_noti)
                    .setContentIntent(pendingIntent)
                    .setContentTitle(notificationTitle)
                    .setContentText(notificationBody)
        }
        val notificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager?
        notificationManager!!.notify(id, notificationBuilder?.build())
    }


    fun getBitmapfromUrl(imageUrl: String?): Bitmap? {
        return try {
            val url = URL(imageUrl)
            val connection = url.openConnection() as HttpURLConnection
            connection.doInput = true
            connection.connect()
            val input = connection.inputStream
            BitmapFactory.decodeStream(input)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    companion object {
        private const val TAG = "FirebaseMessageService"
    }
}