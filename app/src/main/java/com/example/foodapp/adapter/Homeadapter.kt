package com.example.foodapp.adapter

import android.content.Intent
import android.os.AsyncTask
import android.os.SystemClock
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.example.foodapp.Activity.RestaurantActivity
import com.example.foodapp.R
import com.google.gson.Gson
import com.squareup.picasso.Picasso
import database.Restaurantdatabase
import database.Restaurantentity
import model.Restaurant
import android.content.Context as Context

class Homeadapter(val context: Context, var restaurantlist:ArrayList<Restaurant>):RecyclerView.Adapter<Homeadapter.Homeviewholder>() {

    var lastClickTime:Long= 0

    class Homeviewholder(view:View):RecyclerView.ViewHolder(view){

        var imgrestaurant:ImageView=view.findViewById(R.id.imgrestaurant)
        var txtname:TextView=view.findViewById(R.id.txtname)
        var txtcostforone:TextView=view.findViewById(R.id.txtcostforone)
        var txtrating:TextView=view.findViewById(R.id.txtrating)
        var cardhome:CardView=view.findViewById(R.id.cardhome)
        var imgheart:ImageView=view.findViewById(R.id.imgheart)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Homeviewholder {

        var view=LayoutInflater.from(parent.context).inflate(R.layout.homesinglerow,parent,false)

        return Homeviewholder(view)
    }

    override fun getItemCount(): Int {

        return restaurantlist.size
    }


    override fun onBindViewHolder(holder: Homeviewholder, position: Int) {

        var restaurant=restaurantlist[position]

        holder.txtname.text=restaurant.name
        holder.txtrating.text=restaurant.rating
        holder.txtcostforone.text="₹ "+restaurant.cost_for_one+"/person"
        Picasso.get().load(restaurant.image_url).error(R.drawable.logo1).into(holder.imgrestaurant)

        var restentity=Restaurantentity(restaurant.id,restaurant.name,
            restaurant.rating,restaurant.cost_for_one,restaurant.image_url)

        if(Restasyntask(
                context,
                restentity,
                3
            ).execute().get())
            holder.imgheart.setImageResource(R.drawable.favourite)


        holder.cardhome.setOnClickListener {

            if (SystemClock.elapsedRealtime() - lastClickTime < 1000){
                return@setOnClickListener;
            }
            lastClickTime= SystemClock.elapsedRealtime()

            var intent=Intent(context,
                RestaurantActivity::class.java)

            intent.putExtra("restaurantid",restaurant.id)
            intent.putExtra("retaurantname",restaurant.name)
            intent.putExtra("retaurant",Gson().toJson(restentity))
            context.startActivity(intent)

        }

        holder.imgheart.setOnClickListener {


            if(Restasyntask(
                    context,
                    restentity,
                    3
                ).execute().get()) {

                holder.imgheart.setImageResource(R.drawable.favouriteborder)
                Restasyntask(
                    context,
                    restentity,
                    2
                ).execute().get()

            }else{

                holder.imgheart.setImageResource(R.drawable.favourite)
                Restasyntask(
                    context,
                    restentity,
                    1
                ).execute().get()
            }


        }

    }

    class Restasyntask(val context:Context, val restaurant: Restaurantentity, val mode:Int):
        AsyncTask<Void, Void, Boolean>() {

        var db=Room.databaseBuilder(context,
            Restaurantdatabase::class.java,"favrestaurant").build()
        /*

        1-> to insert the restuarnt into database
        2-> to delete the restaurnt from the data base

        */
        override fun doInBackground(vararg params: Void?): Boolean {

            when(mode){

                1->{

                    db.restdao().insertrest(restaurant)
                    db.close()
                    return true
                }

                2->{
                    db.restdao().deleterest(restaurant)
                    db.close()
                    return true
                }

                3->{

                    var rest=db.restdao().findrestbyid(restaurant.id)
                    db.close()
                    return rest!=null
                }


            }

            return false
        }


    }

}