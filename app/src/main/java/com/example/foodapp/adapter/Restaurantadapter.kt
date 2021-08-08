package com.example.foodapp.adapter

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.SystemClock
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.foodapp.Activity.CartActivity
import com.example.foodapp.R
import com.google.gson.Gson
import model.Itemdata

class Restaurantadapter(val context:Context,val itemlist:ArrayList<Itemdata>,
var button:Button,var recyclerrestaurant:RecyclerView,var restaurantid:String, var restaurantname:String)
    :RecyclerView.Adapter<Restaurantadapter.Restaurantviewholder>() {

    var fooditemlist=ArrayList<Itemdata>()
    var lastClickTime:Long= 0

    class Restaurantviewholder(view:View):RecyclerView.ViewHolder(view){

        var txtitemid:TextView=view.findViewById(R.id.txtitemid)
        var txtitemname:TextView=view.findViewById(R.id.txtitemname)
        var txtitemprice:TextView=view.findViewById(R.id.txtitemprice)
        var btnadd:Button=view.findViewById(R.id.btnadd)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Restaurantviewholder {

        var view=LayoutInflater.from(parent.context).inflate(R.layout.restaurantsinglerow,parent,false)

        return Restaurantviewholder(
            view
        )

    }

    override fun getItemCount(): Int {

        return itemlist.size
    }

    override fun onBindViewHolder(holder: Restaurantviewholder, position: Int) {

        var item=itemlist[position]

        holder.txtitemid.text=(position+1).toString()
        holder.txtitemname.text=item.itemname
        holder.txtitemprice.text="Rs. "+item.costforone

        holder.btnadd.setOnClickListener {

            if(fooditemlist.contains(itemlist[position])==false){

                fooditemlist.add(itemlist[position])
                holder.btnadd.text="Remove"
                holder.btnadd.setBackgroundColor(Color.parseColor("#f5e50a"))

            }else{

                fooditemlist.remove(itemlist[position])
                holder.btnadd.text="Add"
                holder.btnadd.setBackgroundColor(Color.parseColor("#f50905"))
            }

            if(fooditemlist.size==0){

                button.visibility=View.GONE
                recyclerrestaurant.setPadding(0,0,0,0)


            }else{

                button.visibility=View.VISIBLE
               recyclerrestaurant.setPadding(0,0,0,70)
            }
            button.setOnClickListener {

                if (SystemClock.elapsedRealtime() - lastClickTime < 1000){
                    return@setOnClickListener;
                }
                lastClickTime=SystemClock.elapsedRealtime()

                var jsonarraylist=ArrayList<String>()

                for(i in 0 until fooditemlist.size){

                    jsonarraylist.add(Gson().toJson(fooditemlist[i]))
                }

                var intent=Intent(context,
                    CartActivity::class.java)

                intent.putStringArrayListExtra("jsonarraylist",jsonarraylist)
                intent.putExtra("restaurantid",restaurantid)
                intent.putExtra("retaurantname",restaurantname)

                context.startActivity(intent)

            }
        }
    }

}

