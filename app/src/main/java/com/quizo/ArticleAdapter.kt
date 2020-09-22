package com.quizo

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso

class ArticleAdapter(private val list:List<NewsData>,var listener: CustomStoryClick):RecyclerView.Adapter<ArticleAdapter.ArticleViewHolder>() {

    class ArticleViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var title: TextView = view.findViewById(R.id.title)
       var image:ImageView = view.findViewById(R.id.img)
        val prof:ImageView = view.findViewById(R.id.prof)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArticleViewHolder {
        val itemView = LayoutInflater.from(parent.context)
                .inflate(R.layout.article_card, parent, false)
        val mv = ArticleViewHolder(itemView)
        itemView.setOnClickListener { listener.onItemClick(itemView, mv.layoutPosition,mv.image) }
        return mv
    }

    override fun getItemCount(): Int {
        return  list.size
    }

    override fun onBindViewHolder(holder: ArticleViewHolder, position: Int) {
        val list = list[position]
        Picasso.get().load(list.url).fit().into(holder.image)
        Picasso.get().load("https://i.ibb.co/HG2gpk7/IMG-20200619-112755.jpg").into(holder.prof)
        holder.title.text = list.head
    }


}