package com.example.foodapp.fragment

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.SystemClock
import android.provider.Settings
import android.view.*
import android.widget.*
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.foodapp.adapter.Homeadapter
import com.example.foodapp.R
import model.Restaurant
import org.json.JSONObject
import util.ConnectionManager
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class HomeFragment : Fragment() {

    lateinit var recyclerhome:RecyclerView
    lateinit var layoutmanager:RecyclerView.LayoutManager
    lateinit var recycleradapter: Homeadapter
    lateinit var etsearch:EditText
    lateinit var btnsearch:Button
    var lastClickTime:Long= 0
    var restaurantlist=ArrayList<Restaurant>()
    var restaurantlistsave=ArrayList<Restaurant>()

    lateinit var progressLayout:RelativeLayout

    var ratingComparator = Comparator<Restaurant>{Restaurant1, Restaurant2 ->

        if (Restaurant1.rating.compareTo(Restaurant2.rating, true) == 0) {
            // sort according to name if rating is same
            Restaurant1.name.compareTo(Restaurant2.name, true)
        } else {
            Restaurant2.rating.compareTo(Restaurant1.rating, true)
        }
    }
    var costComparatorhightolow = Comparator<Restaurant>{Restaurant1, Restaurant2 ->

        if (Restaurant1.cost_for_one.compareTo(Restaurant2.cost_for_one, true) == 0) {
            // sort according to name if rating is same
            Restaurant1.name.compareTo(Restaurant2.name, true)
        } else {
            Restaurant2.cost_for_one.compareTo(Restaurant1.cost_for_one, true)
        }
    }

    var costComparatorlowtohigh = Comparator<Restaurant>{Restaurant1, Restaurant2 ->

        if (Restaurant1.cost_for_one.compareTo(Restaurant2.cost_for_one, true) == 0) {
            // sort according to name if rating is same
            Restaurant1.name.compareTo(Restaurant2.name, true)
        } else {
            Restaurant1.cost_for_one.compareTo(Restaurant2.cost_for_one, true)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        var view=inflater.inflate(R.layout.fragment_home, container, false)

        recyclerhome=view.findViewById(R.id.recyclerhome)
        layoutmanager=LinearLayoutManager(activity)
        etsearch=view.findViewById(R.id.etsearch)
        btnsearch=view.findViewById(R.id.btnsearch)
        progressLayout=view.findViewById(R.id.progressLayout)

        progressLayout.visibility=View.VISIBLE

        setHasOptionsMenu(true)

        if (ConnectionManager().checkConnectivity(activity as Context)) {

        var queue=Volley.newRequestQueue(activity as Context)

        var url="http://13.235.250.119/v2/restaurants/fetch_result/"

        var jsonobjectrequest=object :JsonObjectRequest(Request.Method.GET,url,null,

            Response.Listener {

                progressLayout.visibility=View.GONE

                var datas=it.getJSONObject("data")
                var success=datas.getBoolean("success")

                if(success){

                    var data=datas.getJSONArray("data")

                    for( i in 0 until data.length()){

                        var dataobj:JSONObject= data.get(i) as JSONObject

                        var restaurant=Restaurant(dataobj.getString("id"),dataobj.getString("name")
                            ,dataobj.getString("rating"),dataobj.getString("cost_for_one"),
                            dataobj.getString("image_url"))

                        restaurantlist.add(restaurant)
                        restaurantlistsave.add(restaurant)

                        recycleradapter= Homeadapter(
                            activity as Context,
                            restaurantlist
                        )

                        recyclerhome.layoutManager=layoutmanager
                        recyclerhome.adapter=recycleradapter

                    }


                }else{


                    Toast.makeText(activity as Context,
                        "some error occured",Toast.LENGTH_LONG).show()

                }


            },Response.ErrorListener {

                Toast.makeText(activity as Context,
                    "some volley error occured",Toast.LENGTH_LONG).show()

            }

            ){

            override fun getHeaders(): MutableMap<String, String> {

                val headers = HashMap<String, String>()
                headers["Content-type"] = "Application/json"
                headers["token"] = "c63912f4cfba72"
                return headers
            }

        }

        queue.add(jsonobjectrequest)

        }else{

            val dialog = AlertDialog.Builder(activity as Context)
            dialog.setTitle("Error")
            dialog.setMessage("Internet Connection is not Found")
            dialog.setPositiveButton("Open Settings") { text, listener ->
                val settingsIntent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
                startActivity(settingsIntent)

            }

            dialog.setNegativeButton("Exit") { text, listener ->
                ActivityCompat.finishAffinity(activity!!)
            }
            dialog.create()
            dialog.show()

        }


        btnsearch.setOnClickListener {

            var seachpart=etsearch.text.toString().toUpperCase()

            if(seachpart.length==0){

                restaurantlist.clear()

                for(i in 0 until restaurantlistsave.size)
                    restaurantlist.add(restaurantlistsave[i])

                recycleradapter.notifyDataSetChanged()
                return@setOnClickListener
            }

            restaurantlist.clear()

            for( i in 0 until restaurantlistsave.size){


                var nameofrest=restaurantlistsave[i].name

                var j=0
                var n=seachpart.length
                var m=nameofrest.length

                if(n>m)
                    continue

                while(j<=m-n){

                    var name=nameofrest.subSequence(j,j+n).toString().toUpperCase()

                    if(name==seachpart){
                        restaurantlist.add(restaurantlistsave[i])
                        break
                    }

                    j++

                }

            }
            recycleradapter.notifyDataSetChanged()
            return@setOnClickListener
        }

        return  view

    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {

        inflater?.inflate(R.menu.menuhome, menu)

        }


    override fun onOptionsItemSelected(item: MenuItem?): Boolean {

        val id = item?.itemId

        if (id == R.id.sort && SystemClock.elapsedRealtime() - lastClickTime > 1000){

            lastClickTime= SystemClock.elapsedRealtime()

            val dialog = androidx.appcompat.app.AlertDialog.Builder(activity as Context)

            dialog.setTitle("Sort By?")

            var arr= arrayOf("Cost(Low to High)", "Cost(High to Low)", "Rating")

            dialog.setItems(arr){text,which->

                when(which) {

                    0 -> {

                        Collections.sort(restaurantlist, costComparatorlowtohigh)
                        recycleradapter.notifyDataSetChanged()

                    }

                    1->{

                        Collections.sort(restaurantlist, costComparatorhightolow)
                        recycleradapter.notifyDataSetChanged()

                    }

                    2->{
                        Collections.sort(restaurantlist, ratingComparator)
                        recycleradapter.notifyDataSetChanged()
                    }

                }
            }


            dialog.create()
            dialog.show()
        }

        return super.onOptionsItemSelected(item)

    }

    override fun onStart() {
        super.onStart()
    }

}
