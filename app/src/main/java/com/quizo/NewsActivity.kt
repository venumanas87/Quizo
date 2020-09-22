package com.quizo

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.transition.AutoTransition
import android.transition.ChangeBounds
import android.transition.TransitionManager
import android.util.Log
import android.view.View
import android.view.Window
import android.view.animation.Animation
import android.view.animation.LinearInterpolator
import android.view.animation.RotateAnimation
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityOptionsCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout.OnRefreshListener
import com.google.android.gms.ads.InterstitialAd
import com.google.android.gms.ads.MobileAds
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.bottomnavigation.LabelVisibilityMode
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.jaeger.library.StatusBarUtil
import com.quizo.R.layout
import kotlinx.coroutines.*
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.select.Elements
import java.io.IOException
import java.util.*
import kotlin.coroutines.CoroutineContext

class NewsActivity : AppCompatActivity(),CoroutineScope {
    var count = 0
    private var job: Job = Job()
    override val coroutineContext: CoroutineContext = Dispatchers.Main + job
    private lateinit var newsModel: ViewModel

    ///////news
    var newsAdapter: NewsAdapter? = null
    var data: NewsData? = null
    var newsDataList: MutableList<NewsData>? = null

    /////editorials
    var newsAdapter1: NewsAdapter? = null
    var data1: NewsData? = null
    var newsDataList1: MutableList<NewsData>? = null

    /////////stories
    var newsAdapter2: NewsAdapter? = null
    var data2: NewsData? = null
    var newsDataList2: MutableList<NewsData>? = null

    var progressBar1: ProgressBar? = null
    var progressBar2: ProgressBar? = null
    var progressBar3: ProgressBar? = null
    var nat: TextView? = null
    var tv1: TextView? = null
    var re: TextView? = null
    var swipeRefreshLayout: SwipeRefreshLayout? = null
    var bottomNavigationView: BottomNavigationView? = null
    var chipGroup: ChipGroup? = null
    var unil = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        window.requestFeature(Window.FEATURE_ACTIVITY_TRANSITIONS)
        super.onCreate(savedInstanceState)
        newsModel = ViewModelProviders.of(this).get(NewsViewModel::class.java)
        //AdColony.configure(this, APP_ID, ZONE_IDS)

        setContentView(layout.news_layout)
    }
    }/*
        bottomNavigationView = findViewById(R.id.btmnav)
        bottomNavigationView?.setSelectedItemId(R.id.page_2)
        val CoronaCard = findViewById<CardView>(R.id.rl2)
        val expL = findViewById<RelativeLayout>(R.id.hidl)
        val image = findViewById<ImageView>(R.id.more)
        val mInterstitialAd: InterstitialAd
        MobileAds.initialize(this) { }
        mInterstitialAd = InterstitialAd(this)
        mInterstitialAd.adUnitId = "ca-app-pub-2569821792203202/4563281256"
        //ca-app-pub-2569821792203202/4563281256 orig
        //ca-app-pub-3940256099942544/1033173712 test
        val toolbar: MaterialToolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        re = findViewById(R.id.re)
      *//*  if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
        window.statusBarColor = resources.getColor(R.color.colorPrimaryDark,this.theme)
        }else{
            window.statusBarColor = resources.getColor(R.color.colorPrimaryDark)
        }*//*
        progressBar1 = findViewById(R.id.progress_circular)
        progressBar2 = findViewById(R.id.progress_circular1)
        progressBar3 = findViewById(R.id.progress_circular2)
        swipeRefreshLayout = findViewById(R.id.swipe)
        swipeRefreshLayout?.setEnabled(false)
        swipeRefreshLayout?.setOnRefreshListener(OnRefreshListener {
            //Extract().execute()
            swipeRefreshLayout?.setRefreshing(true)
        })
        val national: Chip
        val world: Chip
        val sci: Chip
        val tech: Chip
        val startp: Chip
        val busi: Chip
        val sports: Chip
        val poli: Chip
        val auto: Chip
        val miscl: Chip
        val eng: Chip
        val hin: Chip
        chipGroup = findViewById(R.id.chpgrp)
        tv1 = findViewById(R.id.tv1)
        eng = findViewById(R.id.english)
        eng.isChecked = true
        hin = findViewById(R.id.hindi)
        national = findViewById(R.id.national)
        national.isChecked = true
        sci = findViewById(R.id.science)
        tech = findViewById(R.id.tech)
        startp = findViewById(R.id.startup)
        busi = findViewById(R.id.busi)
        sports = findViewById(R.id.sports)
        poli = findViewById(R.id.politics)
        auto = findViewById(R.id.automob)
        miscl = findViewById(R.id.miscl)
        world = findViewById(R.id.world)
        eng.setOnClickListener {
            eng.isChecked = true
            job = launch(Dispatchers.IO){Extract(0)}
            national.isChecked = true
        }
        hin.setOnClickListener {
            hin.isChecked = true
            job = launch(Dispatchers.IO) { Extract(1) }
            national.isChecked = true
        }
        national.setOnClickListener {
            if(eng.isChecked){
                job = launch(Dispatchers.IO) { Extract(0) }}
            else if(hin.isChecked){job = launch(Dispatchers.IO) { Extract(1) }}
            national.isChecked = true
            if (mInterstitialAd.isLoaded) {
                mInterstitialAd.show()
            } else {
                Log.d("TAG", "The interstitial wasn't loaded yet.")
            }
        }
        world.setOnClickListener {
           job = launch(Dispatchers.IO){ ExtractC("world")}
            world.isChecked = true
            if (mInterstitialAd.isLoaded) {
                mInterstitialAd.show()
            } else {
                Log.d("TAG", "The interstitial wasn't loaded yet.")
            }
        }
        sci.setOnClickListener {
            job = launch(Dispatchers.IO){  ExtractC("science")}
            sci.isChecked = true
        }
        tech.setOnClickListener {
            job = launch(Dispatchers.IO){  ExtractC("technology")}
            tech.isChecked = true
        }
        startp.setOnClickListener {
            job = launch(Dispatchers.IO){ExtractC("startup")}
            startp.isChecked = true
        }
        busi.setOnClickListener {
            job = launch(Dispatchers.IO){ExtractC("business")}
            busi.isChecked = true
        }
        sports.setOnClickListener {
            job = launch(Dispatchers.IO){ExtractC("sports")}
            sports.isChecked = true
        }
        poli.setOnClickListener {
            job = launch(Dispatchers.IO){ExtractC("politics")}
            poli.isChecked = true
        }
        auto.setOnClickListener {
            job = launch(Dispatchers.IO){ExtractC("automobile")}
            auto.isChecked = true
        }
        miscl.setOnClickListener {
            job = launch(Dispatchers.IO){ExtractC("miscellaneous")}
            miscl.isChecked = true
        }
        nat = findViewById(R.id.nat)

         val st:TextView? = findViewById(R.id.tvst)
        st?.setOnClickListener {
           startActivity(Intent(applicationContext,StoryFullActivity::class.java))
        }
        val more:TextView? = findViewById(R.id.moret)
        more?.setOnClickListener {
            startActivity(Intent(applicationContext,StoryFullActivity::class.java))
        }






        /////////////////////////////////////////////////NEWS RECYCLER VIEW/////////////////////////////////////////////////////////////////
        val recyclerView = findViewById<RecyclerView>(R.id.recycler_view1)
        newsDataList = ArrayList()
        newsAdapter = NewsAdapter(newsDataList as ArrayList<NewsData>, CustomStoryClick { v, position,img ->
            val newsData = (newsDataList as ArrayList<NewsData>).get(position)
            val i = Intent(v.context, SharedeleClass::class.java)
            i.putExtra("activity", newsData.activi)
            i.putExtra("title", newsData.head)
            i.putExtra("desc", newsData.cont)
            i.putExtra("auth", newsData.auth)
            i.putExtra("date", newsData.date)
            i.putExtra("url", newsData.url)
            val options = ActivityOptionsCompat.makeSceneTransitionAnimation((v.context as Activity), img, "blob")
            startActivity(i,options.toBundle())

        })
        val layoutManager: RecyclerView.LayoutManager = LinearLayoutManager(applicationContext, RecyclerView.HORIZONTAL, false)
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = newsAdapter
        recyclerView.addOnScrollListener(object: RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                when(newState){
                    RecyclerView.SCROLL_STATE_DRAGGING -> {
                        if (expL.visibility == View.VISIBLE) {
                        TransitionManager.beginDelayedTransition(CoronaCard, ChangeBounds())
                    expL.visibility = View.GONE
                        val rotate = RotateAnimation(180F, 0F, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f)
                    rotate.duration = 50
                            rotate.interpolator = LinearInterpolator()
                            rotate.fillAfter = true
                        image.startAnimation(rotate)
                    }
                    }
                }
            }

        })


        ////////////////////////////////////////////////EDITORIAL RECYCLER VIEW//////////////////////////////////////////////////////
        val recyclerView1 = findViewById<RecyclerView>(R.id.recycler_view)
        newsDataList1 = ArrayList()
        newsAdapter1 = NewsAdapter(newsDataList1 as ArrayList<NewsData>, CustomStoryClick { v, position,img ->
            val newsData = (newsDataList1 as ArrayList<NewsData>).get(position)
            val i = Intent(v.context, SharedeleClass::class.java)
            i.putExtra("activity", newsData.activi)
            i.putExtra("title", newsData.head)
            i.putExtra("desc", newsData.cont)
            i.putExtra("auth", newsData.auth)
            i.putExtra("date", newsData.date)
            i.putExtra("url", newsData.url)
            val options = ActivityOptionsCompat.makeSceneTransitionAnimation((v.context as Activity), img, "blob")
            startActivity(i,options.toBundle())

        })
        val layoutManager1: RecyclerView.LayoutManager = LinearLayoutManager(applicationContext, RecyclerView.HORIZONTAL, false)
        recyclerView1.layoutManager = layoutManager1
        recyclerView1.adapter = newsAdapter1




        ///////////////////////////////////////////////////////STORY RECYCLER VIEW//////////////////////////////////////////////////

        val recyclerView2 = findViewById<RecyclerView>(R.id.recycler_view2)
        newsDataList2 = ArrayList()
        newsAdapter2 = NewsAdapter(newsDataList2 as ArrayList<NewsData>, CustomStoryClick { v, position,img ->
            val newsData = (newsDataList2 as ArrayList<NewsData>).get(position)
            val i = Intent(v.context, SharedeleClass::class.java)
            i.putExtra("activity", newsData.activi)
            i.putExtra("title", newsData.head)
            i.putExtra("desc", newsData.cont)
            i.putExtra("auth", newsData.auth)
            i.putExtra("date", newsData.date)
            i.putExtra("url", newsData.url)
            val options = ActivityOptionsCompat.makeSceneTransitionAnimation((v.context as Activity), img, "blob")
            startActivity(i,options.toBundle())

        })
        val layoutManager2: RecyclerView.LayoutManager = LinearLayoutManager(applicationContext, RecyclerView.HORIZONTAL, false)
        recyclerView2.layoutManager = layoutManager2
        recyclerView2.adapter = newsAdapter2

        //////////////////////////////////END OF RECYCLER VIEWS///////////////////////////////////////////////////////////////////

        bottomNavigationView?.isItemHorizontalTranslationEnabled = false
        bottomNavigationView?.labelVisibilityMode = LabelVisibilityMode.LABEL_VISIBILITY_UNLABELED
        bottomNavigationView?.setOnNavigationItemSelectedListener(BottomNavigationView.OnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.page_1 -> {
                    startActivity(Intent(applicationContext, LoggedIn::class.java))
                    overridePendingTransition(0, 0)
                    finish()
                    return@OnNavigationItemSelectedListener true
                }
                R.id.page_2 -> return@OnNavigationItemSelectedListener true

                R.id.page_4 -> {
                    startActivity(Intent(applicationContext, ProfileActivity::class.java))
                    overridePendingTransition(0, 0)
                }
            }
            false
        })

        expands()
        job = launch(Dispatchers.IO){
            Extract(0)
            Editorials()
            EditTele()
            HinU()
            torExu()
        }

    }

    private fun expands() {
        val CoronaCard = findViewById<CardView>(R.id.rl2)
        val expL = findViewById<RelativeLayout>(R.id.hidl)
        val image = findViewById<ImageView>(R.id.more)
        expL.visibility = View.GONE
        CoronaCard.setOnClickListener {
            if (expL.visibility == View.GONE) {
                TransitionManager.beginDelayedTransition(CoronaCard, AutoTransition())
                expL.visibility = View.VISIBLE
                val rotate = RotateAnimation(0F, 180F, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f)
                rotate.duration = 50
                rotate.interpolator = LinearInterpolator()
                rotate.fillAfter = true
                image.startAnimation(rotate)
            } else {
                TransitionManager.beginDelayedTransition(CoronaCard, ChangeBounds())
                expL.visibility = View.GONE
                val rotate = RotateAnimation(180F, 0F, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f)
                rotate.duration = 50
                rotate.interpolator = LinearInterpolator()
                rotate.fillAfter = true
                image.startAnimation(rotate)
            }
        }
    }


    private fun Extract(lang: Int) {

        count++
        runOnUiThread{
            swipeRefreshLayout!!.isEnabled = false
            re!!.visibility = View.GONE
            newsDataList!!.clear()
            progressBar1!!.visibility = View.VISIBLE
        }
          this.unil = lang
       *//* var url = ""
        url = if (lang == 0) {
            "https://inshorts.com/en/read"
        } else {
            "https://inshorts.com/hi/read"
        }*//*
        var doc: Document? = null
        Log.d("TAG", "Extract: the lang is $lang ")
        try {
            var url: String? = null
            when(lang){
                0 -> url =  "https://inshorts.com/en/read"
                1 -> url = "https://inshorts.com/hi/read"
            }
            doc = Jsoup.connect(url).get()
            val title = doc.select("div.news-card-title")
            val author = doc.select("div.news-card-author-time")
            val autho = author.select("span[class='author']")
            val time = author.select("span[class='time']")
            val date = author.select("span[class='date']")
            val style = title.select("a")
            val span = style.select("span[itemprop]")
            val image = doc.select("div.news-card-image")
            val cont = doc.select("div.news-card-content")
            val contt = cont.select("div[itemprop]")
            val size = span.size
            Log.d("LINKS", "onCreate: " + title.size)
            for (i in 0 until size) {
                val CONT = contt.eq(i).text()
                val tit = span.eq(i).text()
                val ftime = time.eq(i).text()
                val fdate = date.eq(i).text()
                val tida = "$fdate on $ftime"
                val auth = autho.eq(i).text()
                val imgurl = image.eq(i).attr("style").split("'").toTypedArray()[1]
                Log.d("NEXTDATA", "doInBackground: EXTRACT THE FINAL SOURCES$tit   $imgurl $tida")
                ad(tit, imgurl, CONT, auth, tida)

               job = launch { swipeRefreshLayout!!.isRefreshing = false
                    newsAdapter!!.notifyDataSetChanged()
                    progressBar1!!.visibility = View.GONE
                    nat!!.visibility = View.VISIBLE
                    tv1!!.visibility = View.VISIBLE
                    chipGroup!!.visibility = View.VISIBLE
                }



            }
        } catch (e: IOException) {
            runOnUiThread(Runnable {
                re!!.visibility = View.VISIBLE
                swipeRefreshLayout!!.isEnabled = true
                progressBar1!!.visibility = View.GONE
                newsDataList2!!.clear()
                newsDataList1!!.clear()
            })
            if (count<5){
            job = launch(Dispatchers.IO){
                Extract(0)
                newsDataList1!!.clear()
                Editorials()
                EditTele()
                HinU()
                torExu()
            }}
            e.printStackTrace()
        }


    }

    private fun ExtractC(cat:String) {

        runOnUiThread {
            re!!.visibility = View.GONE
            newsDataList!!.clear()
            nat!!.visibility = View.INVISIBLE
            progressBar1!!.visibility = View.VISIBLE
        }

        var doc: Document? = null
        var url:String
       if (unil == 0) {
           url = "https://inshorts.com/en/read/"
        } else {
           url =  "https://inshorts.com/hi/read/"
        }
        Log.d("News", "Acessing the link through asunkTaask  $url $cat")
        try {
            doc = Jsoup.connect(url + cat).get()
            val title = doc.select("div.news-card-title")
            val author = doc.select("div.news-card-author-time")
            val autho = author.select("span[class='author']")
            val time = author.select("span[class='time']")
            val date = author.select("span[class='date']")
            val style = title.select("a")
            val span = style.select("span[itemprop]")
            val image = doc.select("div.news-card-image")
            val cont = doc.select("div.news-card-content")
            val contt = cont.select("div[itemprop]")
            val size = span.size
            Log.d("LINKS", "onCreate: " + title.size)
            for (i in 0 until size) {
                val CONT = contt.eq(i).text()
                val tit = span.eq(i).text()
                val ftime = time.eq(i).text()
                val fdate = date.eq(i).text()
                val tida = "$fdate on $ftime"
                val auth = autho.eq(i).text()
                val imgurl = image.eq(i).attr("style").split("'").toTypedArray()[1]
                Log.d("NEXTDATA", "doInBackground: EXTRACT THE FINAL SOURCES$tit   $imgurl $tida")
                ad(tit, imgurl, CONT, auth, tida)
                job=launch {   newsAdapter!!.notifyDataSetChanged()
                    progressBar1!!.visibility = View.GONE
                    nat!!.visibility = View.VISIBLE }
            }
        } catch (e: IOException) {
            e.printStackTrace()
            progressBar1!!.visibility = View.GONE
        }

    }

    //         ///////////////////////////////////             THE HINDUU /////////////////////////////////////////////
    private fun Editorials() {
                   runOnUiThread {newsDataList1!!.clear()
                       newsAdapter1!!.notifyDataSetChanged()
                       progressBar2!!.visibility = View.VISIBLE }
        var doc: Document? = null
        try {
            doc = Jsoup.connect("https://www.thehindu.com/opinion/editorial/").get()
            val edcard = doc.select("div.ES2-100x4-text1")
            val h2 = edcard.select("h2")
            val links = h2.select("a")
            for (i in 0..1) {
                val link = links.eq(i).attr("href")
                Log.d("EDITORIAL", "doInBackground: $link")
                job = launch(Dispatchers.IO){ ExEdit(link) }
            }

        } catch (e: IOException) {
            e.printStackTrace()
            runOnUiThread { progressBar2!!.visibility = View.GONE }
        }
    }


    private fun ExEdit(url:String) {

            var doc: Document?
            try {
                doc = Jsoup.connect(url).get()
                val div = doc.select("div")
                val p = div.select("p.drop-caps")
                val h2 = doc.select("h2")
                val timestamp = doc.select("span[class='blue-color ksl-time-stamp']")
                val td = timestamp.first().text()
                val desc = p.first().text()
                val Btitle = h2.first().text()
                val urli = "https://exchange4media.gumlet.com/news-photo/93208-hindunew.jpg"
                ad1(Btitle, urli, desc, "Editor,The Hindu", td)
                // Log.d("EXEDIT", "doInBackground: "+ desc+ " : " + td+ Btitle);
            } catch (e: IOException) {
                e.printStackTrace()
            }
            runOnUiThread { newsAdapter1!!.notifyDataSetChanged() }
    }

    /////////////////////////////          TELEGRAPH //////////////////////////////////////////////////

    private fun EditTele() {

            var doc: Document? = null
            try {
                doc = Jsoup.connect("https://www.telegraphindia.com/topic/editorial").get()
                val maindiv = doc.select("div.storyBox")
                val p = maindiv.select("p")
                val links = p.select("a")
                val curd = maindiv.select("h4").first().text()
                var size = 0
                for (d in maindiv.select("h4")) {
                    if (d.text() == curd) size++
                }
                Log.d("TESTING", "doInBackground: $size")
                for (i in 0 until size) {
                    val bas = "https://www.telegraphindia.com"
                    val link = links.eq(i).attr("abs:href")
                    Log.d("TEle", "doInBackground: TELEGRAPH $link")
                    job = launch(Dispatchers.IO){ ExTele(link)}
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }

    }

    private fun ExTele(u:String) {
            val doc: Document
            Log.i("TAG", "Scraping url  $u")
            try {
                doc = Jsoup.connect(u).get()
                val main = doc.select("div.mainStory-normal")
                val title = main.select("h1").first().text()
                val author = main.select("div.author-name").first().text()
                val date = main.select("div.published-infor").first().select("li").first().text().split("Published").toTypedArray()[1]
                val imgurl = main.select("div.Storyimage").first().select("img").first().absUrl("src")
                val builder = StringBuilder()
                builder.append(main.select("div.padiingDetails").first().select("p").text())
                ad1(title, imgurl, builder.toString(), author, date)
                Log.d("EXTELE", "DATA FROM TELEGRAPH $imgurl$builder")
                runOnUiThread { progressBar2!!.visibility = View.GONE }
            } catch (e: IOException) {
                e.printStackTrace()
            }
        runOnUiThread {     newsAdapter1!!.notifyDataSetChanged() }


    }

    ////////////////////////////////////////////////////HINDUSTAN TIMES ///////////////////////////////////////////////////////


    private fun HinU(){
        var doc: Document?
        try {
            doc = Jsoup.connect("https://www.hindustantimes.com/editorials/").get()
            var cont = doc.select("div.clearfix")
            var urlI:Elements = cont.select("div.media-heading").select("a")
            var size = urlI.size
            for(i in 0 until size){
                var url = urlI.eq(i).attr("href")
                Log.d("TAG", "HinU: $size $url")
               job = launch(Dispatchers.IO){ HinC(url) }

            }
        }
        catch (e:IOException){

        }


    }

    private fun HinC(url: String?) {
        var doc: Document?
        try {
            doc = Jsoup.connect(url).get()
            var cont = doc.select("div.storyArea")
            var title = cont.first().select("h1").first().text()
            var date = cont.first().select("span[class='text-dt']").first().text().split("Updated:").toTypedArray()[1]
            var imgurl = cont.first().select("figure").select("img").first().attr("src")
            var ps = cont.first().select("div.storyDetail").first().select("p")
            var psize = ps.size
            var content= StringBuilder()
            for(i in 0 until psize){
                content.append(ps.eq(i).text()+"\n\n")
            }
            ad1(title,imgurl,content.toString(),"HINDUSTAN TIMES",date)
            runOnUiThread {     newsAdapter1!!.notifyDataSetChanged() }
        }
        catch (e:IOException){

        }

    }


    //////////////////////////////////////////////////////EDITORIAL END ////////////////////////////////////////////

    //////////////////////////////////////////////////////STORIES//////////////////////////////////////////////////

    private fun torExu(){
        runOnUiThread {newsDataList2!!.clear()
            newsAdapter2!!.notifyDataSetChanged()
            progressBar3!!.visibility = View.VISIBLE }
        var doc:Document
        try{
            doc = Jsoup.connect("https://www.tor.com/category/all-fiction/").get()
            var main = doc.select("div.archive-section")
            var box = main.select("article[data-nonce]")
            var size = box.size
            Log.d("TAG", "torExu: $size")
            for (i in 0 until size){
                var url = box.eq(i).select("h2[class='entry-title']").select("a").attr("href")
                torD(url)
                Log.d("TAG", "torExu: $url")
            }


        }catch (e:IOException){}
    }

    private fun torD(url: String?) {
        var doc:Document
        try {
            doc = Jsoup.connect(url).get()
            var title =  doc.select("header[class='entry-header']").select("h2[class='entry-title']").text()
            var author =  doc.select("header[class='entry-header']").select("a[class='entry-author']").text()
            var conten = doc.select("div.entry-content").select("p")
            var cs = conten.size
            var content = StringBuilder()
            for(i in 0 until cs){
                content.append(conten.eq(i).text()+"\n\n")
            }
            var date =   doc.select("header[class='entry-header']").select("span[class='entry-date']").text()
            var imgurl =  doc.select("header[class='entry-header']").select("picture").select("img").attr("srcset")
            if(imgurl.isEmpty()){
                imgurl = "https://wallpapercave.com/wp/xo1DOKG.jpg"
            }
            Log.d("TAG", "torD:  $title $imgurl $author $date ")
            ad2(title,imgurl,content.toString(),"By - $author",date)
            runOnUiThread {     newsAdapter2!!.notifyDataSetChanged()
                progressBar3!!.visibility = View.GONE }
        }catch (e:IOException){

        }
    }


    fun ad(ti: String?, imgurl: String?, cont: String?, auth: String?, tida: String?) {
        data = NewsData(ti, "desc", tida, auth, imgurl, cont, "first", 0)
        newsDataList!!.add(data!!)
    }

    fun ad1(ti: String?, imgurl: String?, cont: String?, auth: String?, tida: String?) {
        data1 = NewsData(ti, "desc", tida, auth, imgurl, cont, "first", 0)
        newsDataList1!!.add(data1!!)
    }

    fun ad2(ti: String?, imgurl: String?, cont: String?, auth: String?, tida: String?) {
        data2 = NewsData(ti, "desc", tida, auth, imgurl, cont, "first", 1)
        newsDataList2!!.add(data2!!)
    }

    override fun onPostResume() {
        super.onPostResume()
        bottomNavigationView!!.selectedItemId = R.id.page_2
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
    }

    companion object {
        private const val APP_ID = "app40514ed2b6b44d8ab8"
        private const val ZONE_IDS = "vz4835d55a49e64ca7bc"
    }
}*/
