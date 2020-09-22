package com.quizo

import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.app.ActivityOptionsCompat
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.quizo.LoggedIn.Companion.startActivity
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.news_card.*

class NewsAdapter(private val lists: List<NewsData>, var listener: CustomStoryClick) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == NewsData.NEWS) {
            val itemView = LayoutInflater.from(parent.context)
                    .inflate(R.layout.news_card, parent, false)
            val mv = MyView(itemView)
            itemView.setOnClickListener { listener.onItemClick(itemView, mv.layoutPosition,mv.iv) }
            mv
        } else {
            val v = LayoutInflater.from(parent.context).inflate(R.layout.story_card, parent, false)
            val vh = MyStoryView(v)
            v.setOnClickListener { listener.onItemClick(v, vh.layoutPosition,vh.iv) }
            vh
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val newsData = lists[position]
        if (newsData.type == 0) {
            (holder as MyView).head.text = newsData.head
            Picasso.get().load(newsData.url).fit().into(holder.iv)

        } else {
            (holder as MyStoryView).head.text = newsData.head
            Picasso.get().load(newsData.url).fit().into(holder.iv)
            holder.head.setOnClickListener { view ->
                val i = Intent(view.context, StoryView::class.java)
                i.putExtra("activity", newsData.activi)
                i.putExtra("title", newsData.head)
                i.putExtra("desc", newsData.cont)
                i.putExtra("auth", newsData.auth)
                i.putExtra("date", newsData.date)
                i.putExtra("url", newsData.url)
                val options = ActivityOptionsCompat.makeSceneTransitionAnimation((view.context as Activity), holder.iv, "shared")
                view.context.startActivity(i, options.toBundle())
            }
            holder.iv.setOnClickListener { view ->
                val i = Intent(view.context, StoryView::class.java)
                i.putExtra("activity", newsData.activi)
                i.putExtra("title", newsData.head)
                i.putExtra("desc", newsData.cont)
                i.putExtra("auth", newsData.auth)
                i.putExtra("date", newsData.date)
                i.putExtra("url", newsData.url)
                val options = ActivityOptionsCompat.makeSceneTransitionAnimation((view.context as Activity), holder.iv, "shared")
                view.context.startActivity(i, options.toBundle())
            }
        }
    }

    override fun getItemCount(): Int {
        return lists.size
    }

    inner class MyView(view: View) : RecyclerView.ViewHolder(view) {
        var head: TextView = view.findViewById(R.id.headline)
        var desc: TextView? = null
        var date: TextView? = null
        var auth: TextView? = null
        var iv: ImageView = view.findViewById(R.id.img)

    }

    inner class MyStoryView(view: View) : RecyclerView.ViewHolder(view) {
        var head: TextView = view.findViewById(R.id.title)
        var iv: ImageView = view.findViewById(R.id.image)

    }

    override fun getItemViewType(position: Int): Int {
        when (lists[position].type) {
            1 -> return NewsData.STORY
            0 -> return NewsData.NEWS
        }
        return position
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

}