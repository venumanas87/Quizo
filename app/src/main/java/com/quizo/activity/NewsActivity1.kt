package com.quizo.activity

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityOptionsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.card.MaterialCardView
import com.google.android.material.chip.Chip
import com.google.firebase.auth.FirebaseAuth
import com.quizo.adapters.NewsAdapter
import com.quizo.objects.NewsData
import com.quizo.R
import com.quizo.utils.CustomStoryClick
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.categ_bottomsheet.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import java.io.IOException
import java.lang.StringBuilder
import java.util.*
import kotlin.coroutines.CoroutineContext

class NewsActivity1:AppCompatActivity(),CoroutineScope {
    var job: Job = Job()
    override val coroutineContext: CoroutineContext = Dispatchers.Main+job
    var firebaseAuth = FirebaseAuth.getInstance()
    val currentUser = firebaseAuth.currentUser
    var googleSignInClient: GoogleSignInClient? = null
    var unil:Int = 0
    ////////////////////////////////News//////////////////////////////
    var newsAdapter1: NewsAdapter? = null
    var data1: NewsData? = null
    var newsDataList1: MutableList<NewsData>? = null


    var newsAdapter2: NewsAdapter? = null
    var data2: NewsData? = null
    var newsDataList2: MutableList<NewsData>? = null
    ////////////////////////////////////////////////////////////////

    var langBottomSheet:RelativeLayout? = null
    var categBottomSheet:RelativeLayout? = null
    var engChip:Chip? = null
    var hinChip:Chip?  = null
    var prof:CircleImageView? = null
    var search:ImageView? = null
    var catCard2:MaterialCardView? = null
    var loading:RelativeLayout? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.news)
        window.statusBarColor = Color.parseColor("#A068FF")
        val profU: Uri? = currentUser!!.photoUrl
        loading = findViewById(R.id.loading)
        langBottomSheet = findViewById(R.id.lang_rl)
        categBottomSheet = findViewById(R.id.cat_rl)
        googleSignInClient = GoogleSignIn.getClient(this, GoogleSignInOptions.DEFAULT_SIGN_IN)
        prof = findViewById(R.id.profimg)
        search = findViewById(R.id.search)
        if(prof != null) {
            Picasso.get().load(profU).fit().into(prof)
        }else{
            Picasso.get().load("https://cdn.business2community.com/wp-content/uploads/2017/08/blank-profile-picture-973460_640.png").fit().into(prof)
        }
        prof?.setOnClickListener {
            val i = Intent(this, ProfileActivity::class.java)
            i.putExtra("loc","from_logged")
            this.startActivity(i)
            overridePendingTransition(R.anim.screen_slide_in_from_right, R.anim.screen_slide_out_to_left)
        }
        search?.setOnClickListener {
            val intt = Intent(this, SearchNewsActivity::class.java)
            intt.putExtra("loc","from_logged")
            this.startActivity(intt)
            overridePendingTransition(R.anim.screen_slide_in_from_right, R.anim.screen_slide_out_to_left)
        }
        val langBottomSheetBehavior = BottomSheetBehavior.from(langBottomSheet!!)
        val catBottomSheetBehavior = BottomSheetBehavior.from(categBottomSheet!!)
        var langCard = findViewById<MaterialCardView>(R.id.lan_card)
        var catCard1 = findViewById<MaterialCardView>(R.id.cat_card1)
         catCard2 = findViewById(R.id.cat_card2)
        var blck:RelativeLayout = findViewById(R.id.blck)
        engChip = findViewById(R.id.eng)
        hinChip = findViewById(R.id.hin)
        catCard1.setOnClickListener {
            catBottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
            val cats = arrayOf("National","World","Science","Technology","Startup","Sports","Business","Politics")
            initCateg(cats,"IS")
        }
        catCard2?.setOnClickListener {
            if(unil==0){
                catBottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
            val cats = arrayOf("National","World","Science","Tech","Sports","Business")
            initCateg(cats,"ANI")
            }
            else if(unil == 1) {

                    Toast.makeText(this, "No Categories Available", Toast.LENGTH_SHORT).show()

            }
        }
        langCard.setOnClickListener {
            langBottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
        }

     langBottomSheetBehavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback(){
         override fun onSlide(bottomSheet: View, slideOffset: Float) {
             blck.visibility = View.VISIBLE
             blck.alpha = (0.6*slideOffset).toFloat()
         }

         override fun onStateChanged(bottomSheet: View, newState: Int) {
             when(newState){
                 BottomSheetBehavior.STATE_COLLAPSED ->{
                     blck.visibility = View.GONE
                 }
                 BottomSheetBehavior.STATE_DRAGGING -> {

                 }
                 BottomSheetBehavior.STATE_EXPANDED -> {
                     blck.setOnClickListener {
                         langBottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
                     }
                     engChip?.setOnClickListener {
                         initEnglish()
                         langBottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
                     }
                     hinChip?.setOnClickListener {
                         initHindi()
                         langBottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
                     }
                 }
                 BottomSheetBehavior.STATE_HALF_EXPANDED -> {

                 }
                 BottomSheetBehavior.STATE_HIDDEN -> {

                 }
                 BottomSheetBehavior.STATE_SETTLING -> {

                 } }
         }
     })

        catBottomSheetBehavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback(){
            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                blck.visibility = View.VISIBLE
                blck.alpha = (0.6*slideOffset).toFloat()
            }

            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when(newState){
                    BottomSheetBehavior.STATE_COLLAPSED ->{
                        blck.visibility = View.GONE
                    }
                    BottomSheetBehavior.STATE_DRAGGING -> {

                    }
                    BottomSheetBehavior.STATE_EXPANDED -> {
                        blck.setOnClickListener {
                            catBottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
                        }
                        rg.setOnClickListener {
                            catBottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
                        }

                    }
                    BottomSheetBehavior.STATE_HALF_EXPANDED -> {

                    }
                    BottomSheetBehavior.STATE_HIDDEN -> {

                    }
                    BottomSheetBehavior.STATE_SETTLING -> {

                    } }
            }
        })


         initEnglish()

    }




    fun initCateg(categs: Array<String>,source:String) {
        val catBottomSheetBehavior = BottomSheetBehavior.from(categBottomSheet!!)
        val rg:RadioGroup= findViewById(R.id.rg)
        rg.removeAllViews()
        val size = categs.size
        for(i in 0 until size){
            val rdbtn = RadioButton(this)
            rdbtn.text = categs[i]
            rdbtn.textSize = 25f
            rdbtn.id = i+1
            rdbtn.gravity = Gravity.CENTER_HORIZONTAL
            rdbtn.setTextColor(Color.parseColor("#ffffff"))
            rdbtn.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT)
            rg.addView(rdbtn)
            val v = View(this)
            v.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,25)
            rg.addView(v)
        }
        rg.setOnCheckedChangeListener { _, i ->
            val radio:RadioButton = findViewById(i)
           when(source){


               "IS" -> {
                   val term = radio.text.toString().toLowerCase(Locale.ROOT)
                   if (term == "national") {
                       job = launch(Dispatchers.IO) {
                           Extract(unil)
                       }
                   } else {
                       job = launch(Dispatchers.IO) {
                           ExtractC(term)
                       } }
                       }
                  "ANI" -> {
                      val term = radio.text.toString().toLowerCase(Locale.ROOT)

                          job = launch(Dispatchers.IO) {
                              aniC(term)
                          }
                  }

                 }
            catBottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        }



    }

    fun initEnglish(){
        val label1:TextView = findViewById(R.id.h1)
        val label2:TextView = findViewById(R.id.h2)
        val img1:CircleImageView = findViewById(R.id.l1)
        val img2:CircleImageView = findViewById(R.id.l2)
         this.unil = 0
        loading?.visibility = View.VISIBLE
        ////////////////////////////First ///////////////

        Picasso.get().load("https://whatisthebusinessmodelof.com/wp-content/uploads/2019/09/Inshorts-Business-Model.png").into(img1)




        val recyclerView1:RecyclerView = findViewById(R.id.recycler_view1)
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
        recyclerView1.layoutManager = layoutManager1
        recyclerView1.adapter = newsAdapter1
        //////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////SECOND ////////////////////////////////////////////////
        Picasso.get().load("https://pbs.twimg.com/profile_images/1497864299/ani_mic_logo_400x400.jpg").fit().into(img2)



        val recyclerView2:RecyclerView = findViewById(R.id.recycler_view2)
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
        recyclerView2.layoutManager = layoutManager2
        recyclerView2.adapter = newsAdapter2

        ////////////////////////////////////////////////////////////////////////////////////////////


        hinChip?.isChecked = false
        engChip?.isChecked = true
        label1.text = "Inshorts"
        label2.text = "ANI"
        job = launch(Dispatchers.IO){
            Extract(0)
            htU()
        }

    }
    fun initHindi(){
        val label1:TextView = findViewById(R.id.h1)
        val label2:TextView = findViewById(R.id.h2)
        val img1:CircleImageView = findViewById(R.id.l1)
        val img2:CircleImageView = findViewById(R.id.l2)
         this.unil = 1
        loading?.visibility = View.VISIBLE
        /////////////////////////////////////////////////// First News /////////////////////////////////////////////
        Picasso.get().load("https://whatisthebusinessmodelof.com/wp-content/uploads/2019/09/Inshorts-Business-Model.png").into(img1)

        val recyclerView1 : RecyclerView = findViewById(R.id.recycler_view1)

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
        recyclerView1.layoutManager = layoutManager1
        recyclerView1.adapter = newsAdapter1

/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////// SECOND NEWS/ /////////////////////////////////////////////
        Picasso.get().load("https://content3.jdmagicbox.com/comp/dhanbad/e4/9999px326.x326.140214015447.k8e4/catalogue/dainik-jagran-dhanbad-printing-press-8dk8d516ee-250.jpg").fit().into(img2)



        val recyclerView2:RecyclerView = findViewById(R.id.recycler_view2)
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
        recyclerView2.layoutManager = layoutManager2
        recyclerView2.adapter = newsAdapter2

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        engChip?.isChecked = false
        hinChip?.isChecked = true
        label1.text = "Inshorts"
        label2.text = "Dainik Jagran"
         newsDataList1?.clear()
         newsDataList2?.clear()
        job = launch(Dispatchers.IO){
            Extract(1)
            jagU()
        }

    }




    //////////////////////////////////////////////////////////EXTRACTION INSHORTS///////////////////////////////////////////


    private fun Extract(lang: Int) {

        runOnUiThread{
            newsDataList1!!.clear()
        }
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
                ad1(tit, imgurl, CONT, auth, tida)

                job = launch {
                    newsAdapter1!!.notifyDataSetChanged()
                }



            }
        } catch (e: IOException) {
            runOnUiThread(kotlinx.coroutines.Runnable {
                newsDataList1!!.clear()
            })
            e.printStackTrace()
        }


    }

    private fun ExtractC(cat:String) {

        runOnUiThread {
            newsDataList1!!.clear()

        }

        var doc: Document? = null
        var url:String
        if (unil == 0) {
            url = "https://inshorts.com/en/read/"
        } else {
            url =  "https://inshorts.com/hi/read/"
        }
        Log.d("News", "Acessing the link through   $url $cat")
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
                ad1(tit, imgurl, CONT, auth, tida)
                job=launch {   runOnUiThread { newsAdapter1!!.notifyDataSetChanged() }
                   }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }

    }




    //////////////////////////////////////    ANI ///////////////////////////////////////////////

   private fun htU(){
       val doc:Document
        try {
            Log.d("TAG", "htU: Strted")
           doc = Jsoup.connect("https://aninews.in/category/national/").get()
            var mainDiv = doc.select("div.extra-related-block").select("figcaption").select("a[class='read-more']")
            var sizul = mainDiv.size
            Log.d("TAG", "htU:S $sizul ")
            for(i in 0 until sizul){
                val sdiv = mainDiv.eq(i).attr("abs:href")
                job = launch(Dispatchers.IO){aniD(sdiv)}
                Log.d("TAG", "htU: $sizul $sdiv")

            }
            runOnUiThread { loading?.visibility = View.GONE }
        }catch (e:IOException){
            e.printStackTrace()
        }
    }

    private fun aniC(cat:String){
        runOnUiThread { loading?.visibility = View.VISIBLE }
        val doc:Document
        val url = "https://aninews.in/category/$cat/"
        try {
            Log.d("TAG", "htU: Strted")
            doc = Jsoup.connect(url).get()
            var mainDiv = doc.select("div.extra-related-block").select("figcaption").select("a[class='read-more']")
            var sizul = mainDiv.size
            Log.d("TAG", "htU:S $sizul ")
            for(i in 0 until sizul){
                val sdiv = mainDiv.eq(i).attr("abs:href")
                job = launch(Dispatchers.IO){aniD(sdiv)}
                Log.d("TAG", "htU: $sizul $sdiv")

            }
            runOnUiThread { loading?.visibility = View.GONE }
        }catch (e:IOException){
            e.printStackTrace()
            runOnUiThread { loading?.visibility = View.GONE }
        }
    }

  private  fun aniD(url: String) {
      runOnUiThread {
          newsDataList2!!.clear()

      }
           val doc:Document
        Log.d("TAG", "aniD: sttttrrttt")
        try {
            doc = Jsoup.connect(url).get()
            val main = doc.select("div.card")
            val imgurl = main.select("header").first().select("div.img-container").select("img").attr("src")
            val titl = main.select("article").select("div.content").first().select("h1[class='title']").text()
            val date = main.select("article").select("div.content").first().select("p[class='time']").select("span[class='time-red']").text()
            val desc = main.select("article").select("div[itemprop='articleBody']").select("p").text()
            ad2(titl,imgurl,desc,"ANI",date)
            job=launch {   runOnUiThread { newsAdapter2!!.notifyDataSetChanged() }
            }
            Log.d("TAG", "aniD: $imgurl $titl $date $desc ")
        }catch (e:IOException){
            e.printStackTrace()
            runOnUiThread { loading?.visibility = View.GONE }
        }
    }

////////////////////////////////////////// Jagran News Hindi ////////////////////////////

private fun jagU(){
    val doc:Document

    Log.d("TAG", "jagU: Jagran started")
    try{
        doc = Jsoup.connect("https://www.jagran.com/news/national-news-hindi.html").get()
        val mul = doc.select("ul[class='topicList']").select("li").select("a")
        val sizeu = mul.size
        Log.d("TAG", "jagU: $sizeu")
        for (i in 0 until sizeu){
            val lnk = mul.eq(i).attr("abs:href")
            job = launch(Dispatchers.IO){jagE(lnk)}
            Log.d("TAG", "jagU: $lnk")

        }
        runOnUiThread {  loading?.visibility = View.GONE }
    }catch (e:IOException){
        e.printStackTrace()
    }
}

  private fun jagE(lnk: String) {
      val doc:Document
      try {
          doc = Jsoup.connect(lnk).get()
          val main = doc.select("article[class='detailBox']")
          var imgur = main.select("figure").select("img").attr("src")
          var imgurl:String
          if(imgur.isEmpty()){
              imgurl = "https://quizooo.000webhostapp.com/bg.jpg"
          }else{
              imgurl = main.select("figure").select("img").attr("src").split("//")[1]
          }
          val desc = main.select("div.articleBody").select("p")
          val psiz = desc.size-1
          val fulldesc = StringBuilder()
          val date = main.select("div.dateInfo").select("span").text()/*.split("Publish Date:")[1]*/
          val titl = doc.select("div.iw_component").select("h1").text()
          for (i in 0 until psiz){
              fulldesc.append(desc.eq(i).text() + "\n\n")
          }
          ad2(titl,"https://"+imgurl,fulldesc.toString(),"JAGRAN",date)
          Log.d("TAG", "jagE: $imgurl $titl $date $fulldesc ")
          runOnUiThread { newsAdapter2?.notifyDataSetChanged()
          }
      }catch (e:IOException){
          e.printStackTrace()
      }
  }

   private fun ad1(ti: String?, imgurl: String?, cont: String?, auth: String?, tida: String?) {
        data1 = NewsData(ti, "desc", tida, auth, imgurl, cont, "first", 0)
        newsDataList1!!.add(data1!!)
    }

    private fun ad2(ti: String?, imgurl: String?, cont: String?, auth: String?, tida: String?) {
        data2 = NewsData(ti, "desc", tida, auth, imgurl, cont, "first", 0)
        newsDataList2!!.add(data2!!)
    }


}