package com.example.foodapp.adapter

import android.content.Context
import android.content.Intent
import android.os.SystemClock
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.foodapp.Activity.RestaurantActivity
import com.example.foodapp.R
import com.google.gson.Gson
import com.squareup.picasso.Picasso
import database.Restaurantentity

class Favouriteadapter(val context: Context, var restaurantlist: List<Restaurantentity>) :
RecyclerView.Adapter<Favouriteadapter.Favviewholder>(){

    var lastClickTime:Long= 0
    class Favviewholder(view: View): RecyclerView.ViewHolder(view){

        var imgrestaurant: ImageView =view.findViewById(R.id.imgrestaurant)
        var txtname: TextView =view.findViewById(R.id.txtname)
        var txtcostforone: TextView =view.findViewById(R.id.txtcostforone)
        var txtrating: TextView =view.findViewById(R.id.txtrating)
        var cardhome: CardView =view.findViewById(R.id.cardhome)
        var imgheart: ImageView =view.findViewById(R.id.imgheart)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Favviewholder {

        var view= LayoutInflater.from(parent.context).inflate(R.layout.homesinglerow,parent,false)

        return Favviewholder(view)

    }

    override fun getItemCount(): Int {

        return restaurantlist.size
    }

    override fun onBindViewHolder(holder: Favviewholder, position: Int) {

        var restaurant=restaurantlist[position]

        holder.txtname.text=restaurant.name
        holder.txtrating.text=restaurant.rating
        holder.txtcostforone.text="₹ "+restaurant.cost_for_one+"/person"
        Picasso.get().load(restaurant.image_url).error(R.drawable.logo1).into(holder.imgrestaurant)

        holder.imgheart.setImageResource(R.drawable.favourite)

        var restentity= Restaurantentity(restaurant.id,restaurant.name,
            restaurant.rating,restaurant.cost_for_one,restaurant.image_url)

        holder.cardhome.setOnClickListener {

            if (SystemClock.elapsedRealtime() - lastClickTime < 1000){
                return@setOnClickListener;
            }

            lastClickTime = SystemClock.elapsedRealtime().toLong()

            var intent= Intent(context,
                RestaurantActivity::class.java)

            intent.putExtra("restaurantid",restaurant.id)
            intent.putExtra("retaurantname",restaurant.name)
            intent.putExtra("retaurant", Gson().toJson(restaurant))

            context.startActivity(intent)

        }

        holder.imgheart.setOnClickListener {

            if(Homeadapter.Restasyntask(
                    context,
                    restentity,
                    3
                ).execute().get()) {

                Homeadapter.Restasyntask(
                    context,
                    restentity,
                    2
                ).execute().get()
                holder.imgheart.setImageResource(R.drawable.favouriteborder)

            }else{

                Homeadapter.Restasyntask(
                    context,
                    restentity,
                    1
                ).execute().get()
                holder.imgheart.setImageResource(R.drawable.favourite)

            }
        }

    }

}