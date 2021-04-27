package com.quizo.utils

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import android.view.View
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat.getSystemService
import com.quizo.R
import com.quizo.activity.MainActivity
import com.quizo.activity.NewsActivity1
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.internal.notify
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL
import java.util.logging.Handler
import kotlin.random.Random

class NotificationBroadcast: BroadcastReceiver() {
    override fun onReceive(p0: Context?, p1: Intent?) {
       GlobalScope.launch {
           extract(p0!!)
           htU(p0)
       }
    }

    fun extract(context: Context){
        var doc: Document? = null
        try {
            val url = "https://inshorts.com/en/read"

            doc = Jsoup.connect(url).get()
            val title = doc.select("div.news-card-title")
            val author = doc.select("div.news-card-author-time")
            val autho = author.select("span[class='author']")
            val time = author.select("span[class='time']")
            val date = author.select("span[class='date']")
            val style = title.select("a")
            val span = style.select("span[itemprop]")
            val size = span.size
            val image = doc.select("div.news-card-image")
            val cont = doc.select("div.news-card-content")
            val contt = cont.select("div[itemprop]")
            val i = Random.nextInt(0,size)
            val CONT = contt.eq(i).text()
            val tit = span.eq(i).text()
            val imgurl = image.eq(i).attr("style").split("'").toTypedArray()[1]

            sendLocalNotification(context,tit,CONT,imgurl,201)



        }catch(e: IOException) {
            e.printStackTrace()
        }

    }

    private fun htU(context:Context){
        val doc:Document

        try {
            doc = Jsoup.connect("https://aninews.in/category/national/").get()
            val mainDiv = doc.select("div.extra-related-block").select("figcaption").select("a[class='read-more']")
            val sizul = mainDiv.size
            val i = Random.nextInt(0,sizul)
            val sdiv = mainDiv.eq(i).attr("abs:href")
            val doc1 = Jsoup.connect(sdiv).get()
            val main = doc1.select("div.card")
            val imgurl = main.select("header").first().select("div.img-container").select("img").attr("src")
            val titl = main.select("article").select("div.content").first().select("h1[class='title']").text()
            val desc = main.select("article").select("div[itemprop='articleBody']").select("p").text()
            sendLocalNotification(context,titl,desc,imgurl,200)
        }catch (e:IOException){
            e.printStackTrace()
        }
    }


    fun sendLocalNotification(context: Context,notificationTitle: String?, notificationBody: String?,imgurl:String?,id:Int) {
        val intent = Intent(context, NewsActivity1::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent = PendingIntent.getActivity(
                context, 0, intent,
                PendingIntent.FLAG_ONE_SHOT
        )
        val notificationBuilder: Notification.Builder? = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            Notification.Builder(context,Constants.daily_updates_message_channel)
                    .setAutoCancel(true) //Automatically delete the notification
                    .setSmallIcon(R.mipmap.ic_launcher_foreground)
                    .setLargeIcon(getBitmapfromUrl(imgurl))
                    .setContentIntent(pendingIntent)
                    .setContentTitle("Daily News Update")
                    .setContentText(notificationTitle)
                    .setStyle(Notification.BigTextStyle().bigText(notificationBody))
        } else {
            Notification.Builder(context)
                    .setAutoCancel(true) //Automatically delete the notification
                    .setSmallIcon(R.mipmap.ic_launcher_foreground)
                    .setContentIntent(pendingIntent)
                    .setContentTitle(notificationTitle)
                    .setContentText(notificationBody)
        }
        val notificationManager =
                NotificationManagerCompat.from(context)
        notificationManager.notify(id, notificationBuilder?.build()!!)
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

}