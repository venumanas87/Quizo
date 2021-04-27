package com.quizo.activity

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Intent
import android.os.Bundle
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.messaging.FirebaseMessaging
import com.jaeger.library.StatusBarUtil
import com.quizo.R
import com.quizo.utils.NotificationBroadcast

class AboutActivity:AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.about)
        StatusBarUtil.setTransparent(this)
        FirebaseMessaging.getInstance().subscribeToTopic("general")
        val rl:RelativeLayout = findViewById(R.id.rl2)

    }
}