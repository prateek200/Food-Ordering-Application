package com.example.foodapp.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.foodapp.R
import model.Itemdata

class Fooditemsapadter (val context: Context, val itemlist:ArrayList<Itemdata>):RecyclerView.Adapter<Fooditemsapadter.Fooditemsviewholder>(){

    class Fooditemsviewholder(view: View): RecyclerView.ViewHolder(view){

        var txtitemid:TextView=view.findViewById(R.id.txtitemid)
        var txtitemname:TextView=view.findViewById(R.id.txtitemname)
        var txtitemprice:TextView=view.findViewById(R.id.txtitemprice)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Fooditemsviewholder {

        var view=
            LayoutInflater.from(parent.context).inflate(R.layout.fooditemssinglerow,parent,false)

        return Fooditemsviewholder(
            view
        )

    }

    override fun getItemCount(): Int {

        return itemlist.size

    }

    override fun onBindViewHolder(holder: Fooditemsviewholder, position: Int) {

        holder.txtitemid.text=(position+1).toString()
        holder.txtitemname.text=itemlist[position].itemname
        holder.txtitemprice.text="Rs. "+itemlist[position].costforone

    }
}