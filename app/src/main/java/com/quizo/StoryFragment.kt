package com.quizo

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.app.ActivityOptionsCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.tab1.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import java.io.IOException
import java.util.ArrayList
import kotlin.coroutines.CoroutineContext

class StoryFragment : Fragment(),CoroutineScope {
    var newsAdapter: NewsAdapter? = null
    var data: NewsData? = null
    var newsDataList: MutableList<NewsData>? = null
var job:Job = Job()
   override val coroutineContext:CoroutineContext = Dispatchers.Main + job



    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
           val v = inflater.inflate(R.layout.tab1,container,false)
        newsDataList = ArrayList()
        newsAdapter = NewsAdapter(newsDataList as ArrayList<NewsData>, CustomStoryClick { v, position, img ->
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
        val layoutManager: RecyclerView.LayoutManager = GridLayoutManager(v.context,2)
        v.recycler_view.layoutManager = layoutManager
        v.recycler_view.adapter = newsAdapter
        v.recycler_view.addItemDecoration(ItemSpace(v.context,R.dimen.twenty))

        job = launch(Dispatchers.IO){
            torExu()
        }

        return v



    }

    private fun torExu(){
        activity?.runOnUiThread {newsDataList!!.clear()
            newsAdapter!!.notifyDataSetChanged()
            }
        var doc: Document
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


        }catch (e: IOException){}
    }

    private fun torD(url: String?) {
        var doc: Document
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
            ad(title,imgurl,content.toString(),"By - $author",date)
            activity?.runOnUiThread {     newsAdapter!!.notifyDataSetChanged()
                }
        }catch (e: IOException){

        }
    }


    fun ad(ti: String?, imgurl: String?, cont: String?, auth: String?, tida: String?) {
        data = NewsData(ti, "desc", tida, auth, imgurl, cont, "first", 1)
        newsDataList!!.add(data!!)
    }



}

