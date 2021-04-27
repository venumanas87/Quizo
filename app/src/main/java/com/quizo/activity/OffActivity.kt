package com.quizo.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityOptionsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.adcolony.sdk.AdColony
import com.adcolony.sdk.AdColonyAdSize
import com.adcolony.sdk.AdColonyAdView
import com.adcolony.sdk.AdColonyAdViewListener
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.bottomnavigation.LabelVisibilityMode
import com.google.android.material.button.MaterialButton
import com.quizo.*
import com.quizo.adapters.NewsAdapter
import com.quizo.localdatabase.DboffHelper
import com.quizo.objects.NewsData
import com.quizo.utils.CustomStoryClick
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.util.*
import kotlin.coroutines.CoroutineContext

class OffActivity : AppCompatActivity(),CoroutineScope {
    var job:Job = Job()
  override var coroutineContext:CoroutineContext = Dispatchers.Main + job
    var recyclerView: RecyclerView? = null
    var recyclerView1: RecyclerView? = null
    var btn: MaterialButton? = null
    var newsAdapter: NewsAdapter? = null
    var newsAdapter1: NewsAdapter? = null
    var tv: TextView? = null
    var newss:TextView?= null
    var stry:TextView? = null
    var newsDataList: MutableList<NewsData>? = null
    var newsDataList1: MutableList<NewsData>? = null
    var bottomNavigationView: BottomNavigationView? = null
    var data: NewsData? = null
    var data1: NewsData? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AdColony.configure(this, APP_ID, ZONE_IDS)
        val listener: AdColonyAdViewListener = object : AdColonyAdViewListener() {
            override fun onRequestFilled(ad: AdColonyAdView) {
            }
        }
        AdColony.requestAdView(ZONE_IDS, listener, AdColonyAdSize.BANNER)
        setContentView(R.layout.activity_off)
        recyclerView = findViewById(R.id.recycler_view)
        recyclerView1 = findViewById(R.id.recycler_view1)
        btn = findViewById(R.id.but)
        tv = findViewById(R.id.emp)
        newss = findViewById(R.id.nwsar)
        stry = findViewById(R.id.stry)
        bottomNavigationView = findViewById(R.id.btmnav)
        bottomNavigationView?.setLabelVisibilityMode(LabelVisibilityMode.LABEL_VISIBILITY_UNLABELED)

        bottomNavigationView?.setOnNavigationItemSelectedListener(BottomNavigationView.OnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.page_2 -> {
                    startActivity(Intent(applicationContext, NewsActivity::class.java))
                    overridePendingTransition(0, 0)
                    return@OnNavigationItemSelectedListener true
                }
                R.id.page_1 -> {
                    startActivity(Intent(applicationContext, LoggedIn::class.java))
                    overridePendingTransition(0, 0)
                    return@OnNavigationItemSelectedListener true
                }

                R.id.page_4 -> {
                    startActivity(Intent(applicationContext, ProfileActivity::class.java))
                    overridePendingTransition(0, 0)
                }
            }
            false
        })
        newsDataList = ArrayList()
        newsAdapter = NewsAdapter(newsDataList!!, CustomStoryClick { v, position, img ->
            val news = newsDataList?.get(position)
            val i = Intent(v.context, SharedeleClass::class.java)
            i.putExtra("activity", news?.activi)
            i.putExtra("title", news?.head)
            i.putExtra("desc", news?.cont)
            i.putExtra("auth", news?.auth)
            i.putExtra("date", news?.date)
            i.putExtra("url", news?.url)
            val option = ActivityOptionsCompat.makeSceneTransitionAnimation((v.context as Activity), img, "blob")
            startActivity(i, option.toBundle())
        })


        newsDataList1 = ArrayList()
        newsAdapter1 = NewsAdapter(newsDataList1!!, CustomStoryClick { v, position, img ->
            val news = newsDataList1?.get(position)
            val i = Intent(v.context, SharedeleClass::class.java)
            i.putExtra("activity", news?.activi)
            i.putExtra("title", news?.head)
            i.putExtra("desc", news?.cont)
            i.putExtra("auth", news?.auth)
            i.putExtra("date", news?.date)
            i.putExtra("url", news?.url)
            val option = ActivityOptionsCompat.makeSceneTransitionAnimation((v.context as Activity), img, "blob")
            startActivity(i, option.toBundle())
        })

        val toolbar:MaterialToolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        val extras = intent.extras
        var mode:String? = null
        if (extras!=null){
            mode = extras.getString("Offline")
        }
        if (mode == "true"){
            bottomNavigationView!!.visibility = View.GONE
        }
        btn?.setOnClickListener(View.OnClickListener {
            runOnUiThread {
                val helper = DboffHelper(applicationContext)
                val db = helper.writableDatabase
                db.delete("NEWS", null, null)
                db.delete("STORY", null, null)
                newsDataList?.clear()
                newsDataList1?.clear()
                newsAdapter!!.notifyDataSetChanged()
                newsAdapter1!!.notifyDataSetChanged()
                ch()
            }
        })
        val layoutManager: RecyclerView.LayoutManager = LinearLayoutManager(applicationContext, RecyclerView.HORIZONTAL, false)
        recyclerView?.setLayoutManager(layoutManager)
        recyclerView?.setAdapter(newsAdapter)

        val layoutManager1: RecyclerView.LayoutManager = LinearLayoutManager(applicationContext, RecyclerView.HORIZONTAL, false)
        recyclerView1?.setLayoutManager(layoutManager1)
        recyclerView1?.setAdapter(newsAdapter1)


        job = launch(Dispatchers.IO){update()
            update1()
              ch()
        }
    }

    private fun ch() {
        if (newsDataList1!!.size == 0 && newsDataList!!.size == 0) {
           runOnUiThread {   tv!!.visibility = View.VISIBLE
            btn!!.visibility = View.GONE}
        }else{
            runOnUiThread {   tv!!.visibility = View.GONE
            btn!!.visibility = View.VISIBLE}
        }

        if(newsDataList!!.size == 0){
            runOnUiThread {  newss?.visibility = View.GONE }
        }
        if (newsDataList1!!.size == 0){
       runOnUiThread {  stry?.visibility = View.GONE }
        }
    }

    private fun update(){
        val helper = DboffHelper(applicationContext)
        val database = helper.writableDatabase
        val cursor = database.rawQuery("SELECT DISTINCT TITLE,DESCRIPTION,AUTHOR,DATE,IMAGEURL FROM NEWS", arrayOf())
        if (cursor != null && cursor.moveToFirst()) {
            do {
                val title = cursor.getString(0)
                val description = cursor.getString(1)
                val author = cursor.getString(2)
                val date = cursor.getString(3)
                val url = cursor.getString(4)
                data = NewsData(title, "desc", date, author, url, description, "second", 0)
                newsDataList?.add(data!!)
            } while (cursor.moveToNext())
        }
        runOnUiThread {
            newsAdapter!!.notifyDataSetChanged()
        }


    }

    private fun update1(){
        val helper = DboffHelper(applicationContext)
        val database = helper.writableDatabase
        val cursor = database.rawQuery("SELECT DISTINCT TITLE,DESCRIPTION,AUTHOR,DATE,IMAGEURL FROM STORY", arrayOf())
        if (cursor != null && cursor.moveToFirst()) {
            do {
                val title = cursor.getString(0)
                val description = cursor.getString(1)
                val author = cursor.getString(2)
                val date = cursor.getString(3)
                val url = cursor.getString(4)
                data1 = NewsData(title, "desc", date, author, url, description, "second", 1)
                newsDataList1?.add(data1!!)
            } while (cursor.moveToNext())
        }
        runOnUiThread {
            newsAdapter1!!.notifyDataSetChanged()
        }


    }


    override fun onBackPressed() {
        super.onBackPressed()
        finish()
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        val extras = intent.extras
        var mode:String? = null
        if (extras!=null){
            mode = extras.getString("Offline")
        }
        if (mode == "true"){
           finishAffinity()
        }
    }

    companion object {
        private const val APP_ID = "app40514ed2b6b44d8ab8"
        private const val ZONE_IDS = "vzf0e127cae4794498ab"
    }
}