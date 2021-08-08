package com.example.foodapp.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.foodapp.R
import model.Previousorders

class Orderhistoryadapter(val context:Context,var list:ArrayList<Previousorders>)
    :RecyclerView.Adapter<Orderhistoryadapter.Orderviewholder>(){

    class Orderviewholder(view: View):RecyclerView.ViewHolder(view){

        var recyclerfooditems:RecyclerView=view.findViewById(R.id.recyclerfooditems)
        var txtname:TextView=view.findViewById(R.id.txtname)
        var txtdate:TextView=view.findViewById(R.id.txtdate)
        var txttime:TextView=view.findViewById(R.id.txttime)
        var txttotalcost:TextView=view.findViewById(R.id.txttotalcost)
        lateinit var layoutmanager:RecyclerView.LayoutManager
        lateinit var adapter: Fooditemsapadter
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Orderviewholder {

        var view=LayoutInflater.from(parent.context).inflate(
            R.layout.orderhistorysinglerow,parent,false)

        return Orderviewholder(
            view
        )

    }

    override fun getItemCount(): Int {

        return list.size
    }

    override fun onBindViewHolder(holder: Orderviewholder, position: Int) {

        holder.layoutmanager=LinearLayoutManager(context)
        holder.adapter= Fooditemsapadter(
            context,
            list[position].fooditems
        )

        holder.recyclerfooditems.layoutManager=holder.layoutmanager
        holder.recyclerfooditems.adapter=holder.adapter

        holder.txtname.text=list[position].name
        holder.txtdate.text=list[position].placed.subSequence(0,8)
        holder.txttime.text=list[position].placed.subSequence(8,list[position].placed.length)
        holder.txttotalcost.text="Rs. "+list[position].totalcost

    }
}