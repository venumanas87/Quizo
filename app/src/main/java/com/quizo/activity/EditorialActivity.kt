package com.quizo.activity

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
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
import org.jsoup.select.Elements
import java.io.IOException
import java.lang.StringBuilder
import java.util.*
import kotlin.coroutines.CoroutineContext

class EditorialActivity:AppCompatActivity(),CoroutineScope {
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
    var loading:RelativeLayout? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.editorials)
        window.statusBarColor = Color.parseColor("#A068FF")
        loading = findViewById(R.id.loading)
        val profU: Uri? = currentUser!!.photoUrl
        langBottomSheet = findViewById(R.id.lang_rl)
        categBottomSheet = findViewById(R.id.cat_rl)
        googleSignInClient = GoogleSignIn.getClient(this, GoogleSignInOptions.DEFAULT_SIGN_IN)
        prof = findViewById(R.id.profimg)
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
        var langBottomSheetBehavior = BottomSheetBehavior.from(langBottomSheet!!)
        var langCard = findViewById<MaterialCardView>(R.id.lan_card)
        var blck:RelativeLayout = findViewById(R.id.blck)
        engChip = findViewById(R.id.eng)
        hinChip = findViewById(R.id.hin)
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



        initEnglish()

    }




    fun initEnglish(){
        val label1:TextView = findViewById(R.id.h1)
        val label2:TextView = findViewById(R.id.h2)
        val img1:CircleImageView = findViewById(R.id.l1)
        val img2:CircleImageView = findViewById(R.id.l2)
        this.unil = 0
        loading?.visibility = View.VISIBLE
        ////////////////////////////First ///////////////

        Picasso.get().load("https://www.hindustantimes.com/favicon.ico").into(img1)




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
        Picasso.get().load("https://pbs.twimg.com/profile_images/1042399192793006080/YYeMRNNJ_400x400.jpg").fit().into(img2)



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
        label1.text = "Hindustan Times"
        label2.text = "The Telegraph"
        newsDataList1?.clear()
        newsDataList2?.clear()
        newsAdapter1?.notifyDataSetChanged()
        newsAdapter2?.notifyDataSetChanged()
        job = launch(Dispatchers.IO){
         HinU()
         EditTele()
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
        Picasso.get().load("https://sscatoz.com/wp-content/uploads/2018/05/images-1.png").into(img1)

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
        label1.text = "Jansatta"
        label2.text = "Dainik Jagran"
        newsDataList1?.clear()
        newsDataList2?.clear()
        newsAdapter1?.notifyDataSetChanged()
        newsAdapter2?.notifyDataSetChanged()

        job = launch(Dispatchers.IO){
               jagU()
            janU()
        }

    }




    //////////////////////////////////////////////////////////EXTRACTION INSHORTS///////////////////////////////////////////







    /////////////////////////////////////////////////////////////////////JAGRAN EDITORIAL ////////////////////////////////////////
    private fun jagU(){
        val doc:Document

        Log.d("TAG", "jagU: Jagran started")
        try{
            doc = Jsoup.connect("https://www.jagran.com/editorial/apnibaat-news-hindi.html").get()
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
            Log.d("TAG", "jagE: $imgurl $titl $date $fulldesc ")
            runOnUiThread { newsAdapter2?.notifyDataSetChanged()
            loading?.visibility= View.GONE}
        }catch (e:IOException){
            e.printStackTrace()
        }
    }
/////////////////////////////////////////////////////////////HINDUSTAN TIMES////////////////////////////////////////////////////
private fun HinU(){
    var doc: Document?
    runOnUiThread { loading?.visibility = View.VISIBLE }
    try {
        doc = Jsoup.connect("https://www.hindustantimes.com/editorials/").get()
        var cont = doc.select("div.clearfix")
        var urlI: Elements = cont.select("div.media-heading").select("a")
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

    /////////////////////////////          TELEGRAPH //////////////////////////////////////////////////

    private fun EditTele() {

        var doc: Document? = null
        try {
            doc = Jsoup.connect("https://www.telegraphindia.com/topic/editorial").get()
            val maindiv = doc.select("div.storyBox")
            val p = maindiv.select("p")
            val links = p.select("a")
            val size = links.size
            for (i in 0 until size) {
                val link = links.eq(i).attr("abs:href")
                Log.d("TEle", "doInBackground: TELEGRAPH $link")
                job = launch(Dispatchers.IO){ ExTele(link)}
            }
            runOnUiThread {  }
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
            val p = main.select("div.padiingDetails").first().select("p")
            val psiz = p.size
            val builder = StringBuilder()
            for (i in 0 until psiz){
                builder.append(p.eq(i).text() + "\n\n")
            }
            ad2(title, imgurl, builder.toString(), author, date)
            runOnUiThread { loading?.visibility = View.GONE
                newsAdapter2!!.notifyDataSetChanged() }
        } catch (e: IOException) {
            e.printStackTrace()
        }



    }


    //////////////////////////////////////////////////////////JANSATTA //////////////////////////////////////////////
    private fun janU(){
        val doc:Document
        runOnUiThread { loading?.visibility = View.VISIBLE }

        runOnUiThread { newsDataList1?.clear()
            newsAdapter1?.notifyDataSetChanged()}
        try {
            Log.d("TAG", "janU: STARTED JANU")
            doc = Jsoup.connect("https://www.jansatta.com/editorial/").get()
            val main= doc.select("div.listinholder")
            val box= main.select("div.newslistbx")
            val size = box.size
            Log.d("TAG", "janU: $size")
            for (i in 0 until size){
               val link =  box.eq(i).select("div.frame").first().select("a").attr("href")
                janE(link)
                Log.d("TAG", "janU: $link")
            }
            runOnUiThread { }
        }catch (e:IOException){
            e.printStackTrace()
        }

    }

    private fun janE(link: String?) {
        val doc:Document
        try {
            doc = Jsoup.connect(link).get()
            val main = doc.select("article.storybox")
            val title = main.select("h1[itemprop='headline']").text()
            val imgurl = "https://quizooo.000webhostapp.com/bg.jpg"
            val date = main.select("article.dateholder").select("div.date").first().select("span[itemprop='dateModified']").text()
            val desc = main.select("div.rightsec").select("p")
            val descfull = StringBuilder()
            val psiz = desc.size-1
            for (i in 0 until psiz){
                descfull.append(desc.eq(i).text() + "\n\n")
            }
            ad1(title,imgurl,descfull.toString(),"JANSATTA",date)
            runOnUiThread {     newsAdapter1!!.notifyDataSetChanged() }
            Log.d("TAG", "janE: $title $imgurl $date $descfull")
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