package com.quizo.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.RelativeLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityOptionsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.facebook.ads.AdView
import com.google.firebase.auth.FirebaseAuth
import com.jaeger.library.StatusBarUtil
import com.quizo.*
import com.quizo.adapters.ArticleAdapter
import com.quizo.adapters.NewsAdapter
import com.quizo.objects.NewsData
import com.quizo.utils.CustomStoryClick
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import java.io.IOException
import java.util.*
import kotlin.coroutines.CoroutineContext


@Suppress("SameParameterValue")
class ArticleActivity:AppCompatActivity(),CoroutineScope {
    var job:Job = Job()
    override val coroutineContext: CoroutineContext = Dispatchers.Main + job
    var firebaseAuth = FirebaseAuth.getInstance()
    val currentUser = firebaseAuth.currentUser
    /////////////////////////////////
    private var recyclerView1:RecyclerView? = null
    private var articleAdapter1: ArticleAdapter? = null
    private var data1: NewsData? = null
    private var newsDataList1: MutableList<NewsData>? = null

    private var recyclerView2:RecyclerView? = null
    var newsAdapter2: NewsAdapter? = null
    var data2: NewsData? = null
    var newsDataList2: MutableList<NewsData>? = null
    ////////////////////////////////
     var loading:RelativeLayout? = null
    private var adView: AdView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.articles)
        StatusBarUtil.setTransparent(this)
        loading = findViewById(R.id.loading)
        val profimg:CircleImageView  = findViewById(R.id.profimg)
        Picasso.get().load(currentUser!!.photoUrl).into(profimg)
        initRecycler()
    }

    private fun initRecycler() {
        recyclerView1 = findViewById(R.id.recycler_view1)
        newsDataList1 = ArrayList()
        loading?.visibility = View.VISIBLE
        articleAdapter1 = ArticleAdapter(newsDataList1!!, CustomStoryClick { v, position, img ->
            val newsData = newsDataList1!![position]
            val i = Intent(v.context, SharedeleClass::class.java)
            i.putExtra("activity", newsData.activi)
            i.putExtra("title", newsData.head)
            i.putExtra("desc", newsData.cont)
            i.putExtra("auth", newsData.auth)
            i.putExtra("date", newsData.date)
            i.putExtra("url", newsData.url)
            val options = ActivityOptionsCompat.makeSceneTransitionAnimation((v.context as Activity), img, "blob")
            startActivity(i, options.toBundle())
        })
        val layoutManager1: RecyclerView.LayoutManager = LinearLayoutManager(applicationContext, RecyclerView.HORIZONTAL, false)
        recyclerView1?.layoutManager = layoutManager1
        recyclerView1?.adapter = articleAdapter1

        //////////////////////////////////////////////////


        recyclerView2 = findViewById(R.id.recycler_view2)
        newsDataList2 = ArrayList()
        newsAdapter2 = NewsAdapter(newsDataList2!!, CustomStoryClick { v, position, img ->
            val newsData = newsDataList2!![position]
            val i = Intent(v.context, SharedeleClass::class.java)
            i.putExtra("activity", newsData.activi)
            i.putExtra("title", newsData.head)
            i.putExtra("desc", newsData.cont)
            i.putExtra("auth", newsData.auth)
            i.putExtra("date", newsData.date)
            i.putExtra("url", newsData.url)
            val options = ActivityOptionsCompat.makeSceneTransitionAnimation((v.context as Activity), img, "blob")
            startActivity(i, options.toBundle())
        })
        val layoutManager2: RecyclerView.LayoutManager = LinearLayoutManager(applicationContext, RecyclerView.HORIZONTAL, false)
        recyclerView2?.layoutManager = layoutManager2
        recyclerView2?.adapter = newsAdapter2




        job = launch(Dispatchers.IO) {
            ripMuskan()
            ripForbes()
        }
         }

    private fun ripForbes() {
        val doc:Document
        try {
            doc = Jsoup.connect("https://www.forbes.com/innovation/").get()
            val mainEle = doc.select("div.chansec-stream__items")
            val card = mainEle.select("article.stream-item")
            val csize = card.size
            for (i in 0 until csize){
              val link = card.eq(i).select("a").first().attr("href")
                ripForbesU(link)
                Log.d(TAG, "ripForbes: $link")
            }
        }catch (e:IOException){
           e.printStackTrace()
            job = launch(Dispatchers.IO){
                ripForbes()
            }
        }
    }

    private fun ripForbesU(link: String?) {
        val doc:Document
        try {
           doc = Jsoup.connect(link).get()
            val mainEle = doc.select("div.body-container")
            val title = mainEle.select("h1.fs-headline").text()
            val  time = mainEle.select("div.content-data").first().select("time").text()
            var imgurl = mainEle.select("div.article-body-container").select("img").attr("src")
            if(imgurl.isEmpty()){
                imgurl = "https://quizooo.000webhostapp.com/bg.jpg"
            }
            val auth = mainEle.select("span.fs-author-name").select("a.contrib-link--name").text()
            val bodyP = mainEle.select("p")
            val psize = bodyP.size
            val desc = StringBuilder()
            for (i in 0 until psize){
                desc.append(bodyP.eq(i).text() + "\n\n")
            }
            ad2(title,imgurl,desc.toString(),auth,time)
            Log.d(TAG, "ripForbesU: $title $time $imgurl $auth $desc")
            runOnUiThread { newsAdapter2?.notifyDataSetChanged() }
        }catch (e:IOException){
          e.printStackTrace()
        }
    }

    private fun ripMuskan() {
        val doc:Document
        try {
            doc = Jsoup.connect("https://muskaannn21.blogspot.com").get()
            val mainEle = doc.select("div.main")
            val articleBox = mainEle.select("div.post")
            val size = articleBox.size
            for (i in 0 until  size) {
                val link = articleBox.eq(i).select("h3.post-title").select("a").attr("href")
                ripMuskanU(link)
                Log.d(TAG, "ripMuskan: $link ")
            }

        }catch (e:IOException){
            e.printStackTrace()
            job = launch(Dispatchers.IO){
                ripMuskan()
            }
        }
    }

    private fun ripMuskanU(link: String?) {
        val doc:Document
        try {
            doc = Jsoup.connect(link).get()
            val mainBox = doc.select("article").first()
            val title = mainBox.select("div.post").select("h3.post-title").text()
            val date = mainBox.select("div.post").select("span.post-timestamp").text()
            var imgurl = mainBox.select("div.post").select("div.post-body").select("img").attr("src")
            if (imgurl==null || imgurl==" " || imgurl.isEmpty()){
                imgurl = "https://quizooo.000webhostapp.com/bg.jpg"
            }
            val desc = mainBox.select("div.post").select("div.post-body").select("div.p1")
            val dsiz = desc.size
            val fulldesc = StringBuilder()
            for (i in 0 until dsiz){
                fulldesc.append(desc.eq(i).text()+"\n\n")
            }
            ad1(title,imgurl,fulldesc.toString(),"Muskaan Singh",date)
            Log.d(TAG, "ripMuskanU: $title $date $imgurl $desc")
            runOnUiThread { articleAdapter1?.notifyDataSetChanged()
                loading?.visibility = View.GONE}
        }catch (e:IOException){
            e.printStackTrace()
        }

    }

  /*  private fun ad1(ti: String?, imgurl: String?, cont: String?, auth: String?, tida: String?) {
        data1 = NewsData(ti, "desc", tida, auth, imgurl, cont, "first", 0)
        newsDataList1!!.add(data1!!)
    }*/
    private fun ad2(ti: String?, imgurl: String?, cont: String?, auth: String?, tida: String?) {
        val size = newsDataList2?.size
        var count:Int = 0
        if (size!=null){
            for (i in 0 until size){
                if (newsDataList2!![i].head == ti){
                    count++
                }
            }
        }
        if (count==0){
            data2 = NewsData(ti, "desc", tida, auth, imgurl, cont, "first", 0)
            newsDataList2!!.add(data2!!)
        }

    }

    private fun ad1(ti: String?, imgurl: String?, cont: String?, auth: String?, tida: String?) {
        val size = newsDataList1?.size
        var count:Int = 0
        if (size!=null){
            for (i in 0 until size){
                if (newsDataList1!![i].head == ti){
                    count++
                }
            }
        }
        if (count==0){
            data1 = NewsData(ti, "desc", tida, auth, imgurl, cont, "first", 0)
            newsDataList1!!.add(data1!!)
        }

    }


    override fun onDestroy() {
        if (adView != null) {
            adView!!.destroy()
        }
        super.onDestroy()
    }


    companion object {
        const val TAG = "Article"
    }

}