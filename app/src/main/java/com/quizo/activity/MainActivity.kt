package com.quizo.activity

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import com.jaeger.library.StatusBarUtil
import com.quizo.R
import com.quizo.utils.Constants
import com.quizo.utils.NotificationBroadcast

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
        view = findViewById(R.id.rl)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(Constants.daily_updates_message_channel, Constants.daily_updates_message_channel , importance)
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
            isNetworkConnectionAvailable
        } else {
            isNetworkConnectionAvailable
        }

        val sharedPreferences = getSharedPreferences("quizo_prefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        val firtsRun:Boolean = sharedPreferences.getBoolean("first_time",true)
        if(firtsRun){
            editor.putBoolean("first_time",false)
            editor.apply()
            editor.commit()
            println("venu already registered not for noti")
            val inte = Intent(this, NotificationBroadcast::class.java)
            val pendInte = PendingIntent.getBroadcast(this,0,inte,0)
            val alarmManager: AlarmManager = getSystemService(ALARM_SERVICE) as AlarmManager
            val timebtn = System.currentTimeMillis()
            val twelveHoursinMillis = 432*100*1_000L
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,timebtn+twelveHoursinMillis,twelveHoursinMillis,pendInte)
        }else{
            println("venu already registered for noti")
             }


    }
}