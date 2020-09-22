package com.quizo

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.ScrollView
import android.widget.SearchView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityOptionsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieAnimationView
import com.google.android.material.card.MaterialCardView
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.search_view_news.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import java.io.IOException
import java.lang.StringBuilder
import java.util.ArrayList
import kotlin.coroutines.CoroutineContext

class SearchNewsActivity:AppCompatActivity(),CoroutineScope {
     var job:Job = Job()
    override val coroutineContext: CoroutineContext = Dispatchers.Main + job

    ////////////////////////Recycler VIews//////////////////
    var recyclerView1:RecyclerView? = null
    var recyclerView2:RecyclerView? = null
    var newsAdapter1: NewsAdapter? = null
    var data1: NewsData? = null
    var newsDataList1: MutableList<NewsData>? = null
    var newsAdapter2: NewsAdapter? = null
    var data2: NewsData? = null
    var newsDataList2: MutableList<NewsData>? = null

    //////////////////////////////////////////////////
    var srchV:SearchView? = null
    var loading:LottieAnimationView? = null
    var scrollV:ScrollView? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.search_view_news)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            window.statusBarColor = Color.WHITE
        }
        val more1:MaterialCardView = findViewById(R.id.cat_card1)
        val more2:MaterialCardView = findViewById(R.id.cat_card2)
        val back:ImageView = findViewById(R.id.back)
        loading = findViewById(R.id.loading)
        srchV = findViewById(R.id.srchtxt)
        scrollV = findViewById(R.id.scroll)
        scrollV?.visibility = View.GONE
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            srchV?.isFocusedByDefault = true
        }
        srchV?.showKeyboard()
        srchV?.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextChange(newText: String): Boolean {
                return false
            }

            override fun onQueryTextSubmit(query: String): Boolean {
                // task HERE
                loading?.visibility = View.VISIBLE
                job = launch(Dispatchers.IO) {
                    aniSearch(query)
                    jagU(query)
                    job.join()
                    runOnUiThread {
                        loading?.visibility = View.GONE
                        scrollV?.visibility = View.VISIBLE
                        newsAdapter1?.notifyDataSetChanged()
                    recyclerView1?.scheduleLayoutAnimation()
                    newsAdapter2?.notifyDataSetChanged()
                    recyclerView2?.scheduleLayoutAnimation()
                    }
                }
                return false
            }
        })

        more1.setOnClickListener {
            val url = "https://aninews.in/search/"
            val i = Intent(Intent.ACTION_VIEW)
            i.data = Uri.parse(url)
            startActivity(i)
        }
        more2.setOnClickListener {
            val url = "https://www.jagran.com/search/"
            val i = Intent(Intent.ACTION_VIEW)
            i.data = Uri.parse(url)
            startActivity(i)
        }
        back.setOnClickListener {
            finishAfterTransition()
        }

        initRVs()

    }

    private fun initRVs() {
        val img1: CircleImageView = findViewById(R.id.l1)
        val img2: CircleImageView = findViewById(R.id.l2)
        ////////////////////////////First ///////////////

        Picasso.get().load("https://pbs.twimg.com/profile_images/1497864299/ani_mic_logo_400x400.jpg").into(img1)




        recyclerView1 = findViewById(R.id.recycler_view1)
        recyclerView1?.scheduleLayoutAnimation()
        newsDataList1 = ArrayList()
        newsAdapter1 = NewsAdapter(newsDataList1!!, CustomStoryClick { v, position, img ->
            val newsData = newsDataList1!![position]
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
        recyclerView1?.layoutManager = layoutManager1
        recyclerView1?.adapter = newsAdapter1
        //////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////SECOND ////////////////////////////////////////////////
        Picasso.get().load("https://content3.jdmagicbox.com/comp/dhanbad/e4/9999px326.x326.140214015447.k8e4/catalogue/dainik-jagran-dhanbad-printing-press-8dk8d516ee-250.jpg").fit().into(img2)



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
            startActivity(i,options.toBundle())
        })
        val layoutManager2: RecyclerView.LayoutManager = LinearLayoutManager(applicationContext, RecyclerView.HORIZONTAL, false)
        recyclerView2?.layoutManager = layoutManager2
        recyclerView2?.adapter = newsAdapter2

        ////////////////////////////////////////////////////////////////////////////////////////////
    }

    fun View.showKeyboard(){
    val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY)
}



    //////////////////////////////////////////ANi SEARCH ////////////////////////////////////////////////////////

    private fun aniSearch(term:String){
        val doc: Document
        runOnUiThread { newsDataList1?.clear()
        }
        try {
            Log.d("TAG", "htU: Strted")
            doc = Jsoup.connect("https://aninews.in/search/?query=$term").get()
            val mainDiv = doc.select("div.extra-related-block").select("div.card")
            val sizul = mainDiv.size
            Log.d("TAG", "htU:S $sizul ")
            for(i in 0 until sizul){
                val sdiv = mainDiv.eq(i).select("figcaption").select("a.read-more").attr("abs:href")
                job = launch(Dispatchers.IO){aniD(sdiv)}
                Log.d("TAG", "htU: $sizul $sdiv")

            }
        }catch (e:IOException){
            e.printStackTrace()
            runOnUiThread { loading?.visibility = View.GONE
              }
                        }

        }


    private  fun aniD(url: String) {
        val doc:Document
        Log.d("TAG", "aniD: sttttrrttt")
        try {
            doc = Jsoup.connect(url).get()
            val main = doc.select("div.card")
            val imgurl = main.select("header").first().select("div.img-container").select("img").attr("src")
            val titl = main.select("article").select("div.content").first().select("h1[class='title']").text()
            val date = main.select("article").select("div.content").first().select("p[class='time']").select("span[class='time-red']").text()
            val desc = main.select("article").select("div[itemprop='articleBody']").select("p").text()
            ad1(titl,imgurl,desc,"ANI",date)
            job=launch {   runOnUiThread {


                  }
            }
            Log.d("TAG", "aniD: $imgurl $titl $date $desc ")
        }catch (e:IOException){
            e.printStackTrace()

        }
    }


    ///////////////////////////////////////////////////Jagran Search//////////////////////////////////////////////////////////////


    private fun jagU(term:String){
        val doc:Document
        runOnUiThread { newsDataList2?.clear()
        }
        Log.d("TAG", "jagU: Jagran started")
        try{
            doc = Jsoup.connect("https://www.jagran.com/search/$term").get()
            val mul = doc.select("ul[class='topicList']").select("li").select("a")
            val sizeu = mul.size
            Log.d("TAG", "jagU: $sizeu")
            for (i in 0 until sizeu){
                val lnk = mul.eq(i).attr("abs:href")
                job = launch(Dispatchers.IO){jagE(lnk)}
                Log.d("TAG", "jagU: $lnk")

            }

        }catch (e:IOException){
            e.printStackTrace()
        }
    }

    private fun jagE(lnk: String) {
        val doc:Document
        try {
            doc = Jsoup.connect(lnk).get()
            val main = doc.select("article[class='detailBox']")
            val imgurl = main.select("figure").select("img").attr("src").split("//")[1]
            val desc = main.select("div.articleBody").select("p")
            val psiz = desc.size-1
            val fulldesc = StringBuilder()
            val date = main.select("div.dateInfo").select("span").text().split("Publish Date:")[1]
            val titl = doc.select("div.iw_component").select("h1").text()
            for (i in 0 until psiz){
                fulldesc.append(desc.eq(i).text() + "\n\n")
            }
            ad2(titl,"https://"+imgurl,fulldesc.toString(),"JAGRAN",date)
            job=launch {   runOnUiThread {


            }
            }
            Log.d("TAG", "jagE: $imgurl $titl $date $fulldesc ")
        }catch (e:IOException){
            e.printStackTrace()
        }
    }




    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private fun ad1(ti: String?, imgurl: String?, cont: String?, auth: String?, tida: String?) {
        data1 = NewsData(ti, "desc", tida, auth, imgurl, cont, "first", 0)
        newsDataList1!!.add(data1!!)
    }

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



}