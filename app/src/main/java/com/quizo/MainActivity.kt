package com.quizo

import android.app.Activity
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityOptionsCompat
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import com.jaeger.library.StatusBarUtil
import com.quizo.OffActivity

class MainActivity : AppCompatActivity() {
    var view: View? = null
    fun checkNetworkConnection() {
        Snackbar.make(view!!, "Please Connect to internet", BaseTransientBottomBar.LENGTH_INDEFINITE)
                .setAction("Offline mode") {
                    val i = Intent(applicationContext, OffActivity::class.java)
                    i.putExtra("Offline", "true")
                    startActivity(i)
                }
                .show()
    }

    val isNetworkConnectionAvailable: Unit
        get() {
            val cm = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val activeNetwork = cm.activeNetworkInfo
            val isConnected = activeNetwork != null &&
                    activeNetwork.isConnected
            if (isConnected) {
                Only()
                Log.d("Network", "Connected")
            } else {
                checkNetworkConnection()
                Log.d("Network", "Not Connected")
            }
        }

    fun Only() {
        val downTimer = object : CountDownTimer(1000, 1000) {
            override fun onTick(l: Long) {}
            override fun onFinish() {
                if (intent.extras != null &&  intent.extras?.getString("title") != null) {
                    val i = Intent(this@MainActivity, SharedeleClass::class.java)
                    i.putExtra("activity", "first")
                    i.putExtra("title", intent.extras?.getString("title"))
                    i.putExtra("desc", intent.extras?.getString("desc"))
                    i.putExtra("auth", intent.extras?.getString("auth"))
                    i.putExtra("date", intent.extras?.getString("date"))
                    i.putExtra("url",intent.extras?.getString("url"))
                    this@MainActivity.startActivity(i)
                    finish()
                }else {
                    val inte = Intent(this@MainActivity, SecondActivity::class.java)
                    this@MainActivity.startActivity(inte)
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
                    finish()
                }
            }
        }.start()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        StatusBarUtil.setTransparent(this)
        setContentView(R.layout.activity_main)
        val channeN = "quizo"
        view = findViewById(R.id.rl)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(channeN, channeN, importance)
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
            isNetworkConnectionAvailable
        } else {
            isNetworkConnectionAvailable
        }
    }
}