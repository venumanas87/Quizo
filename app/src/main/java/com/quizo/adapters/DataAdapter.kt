package com.quizo.adapters

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import com.quizo.R
import com.quizo.adapters.DataAdapter.MyViewHolder
import com.quizo.activity.CovidFullActivity
import com.quizo.objects.CoData

class DataAdapter(private val dataList: List<CoData>) : RecyclerView.Adapter<MyViewHolder>() {

    class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var curr: TextView = view.findViewById(R.id.inyour2)
        var dea: TextView = view.findViewById(R.id.dea2)
        var rec: TextView = view.findViewById(R.id.rec2)
        var conf: TextView = view.findViewById(R.id.conf2)
        var srcard:MaterialCardView = view.findViewById(R.id.stateC)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context)
                .inflate(R.layout.card_re, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val data = dataList[position]
        holder.conf.text = data.confirmed
        holder.curr.text = data.currently
        holder.dea.text = data.deaths
        holder.rec.text = data.recovered
        if (data.from.equals("m")){
        holder.srcard.setOnClickListener { view ->
            val i = Intent(view.context, CovidFullActivity::class.java)
            i.putExtra("cont",data.currently)
            i.putExtra("dec",data.deaths)
            view.context.startActivity(i)
        }
        }else{
            holder.srcard.isEnabled = false
        }
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

}