package com.example.foodapp.Activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.foodapp.adapter.Homeadapter
import com.example.foodapp.R
import com.example.foodapp.adapter.Restaurantadapter
import com.google.gson.Gson
import database.Restaurantentity
import model.Itemdata
import org.json.JSONObject
import util.ConnectionManager
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class RestaurantActivity : AppCompatActivity() {

    lateinit var recyclerrestaurant:RecyclerView
    lateinit var layoutmanager:RecyclerView.LayoutManager
    lateinit var recycleradapter: Restaurantadapter
    lateinit var resttoolbar:androidx.appcompat.widget.Toolbar
    lateinit var btnproceed:Button
    var itemlist=ArrayList<Itemdata>()
    var lastClickTime:Long= 0
    lateinit var progressLayout: RelativeLayout
    lateinit var imgheart:ImageView

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_restaurant)

        recyclerrestaurant=findViewById(R.id.recyclerrestaurant)
        layoutmanager=LinearLayoutManager(this)
        resttoolbar=findViewById(R.id.resttoolbar)
        btnproceed=findViewById(R.id.btnproceed)
        progressLayout=findViewById(R.id.progressLayout)
        imgheart=findViewById(R.id.imgheart)

        progressLayout.visibility=View.VISIBLE


        var restaurantid=intent.getStringExtra("restaurantid")
        var restaurantname=intent.getStringExtra("retaurantname")
        var rest=Gson().fromJson(intent.getStringExtra("retaurant"),
            Restaurantentity::class.java)

        if(Homeadapter.Restasyntask(this, rest, 3).execute().get()){

            imgheart.setImageResource(R.drawable.favourite)

        }


        setuptoolbar(restaurantname)

        if (ConnectionManager().checkConnectivity(this)) {


        var url="http://13.235.250.119/v2/restaurants/fetch_result/"+restaurantid

        var queue=Volley.newRequestQueue(this@RestaurantActivity)

        var jsonrequestobj=object:JsonObjectRequest(
            Request.Method.GET, url,null,
            Response.Listener{

                progressLayout.visibility=View.GONE

                var data=it.getJSONObject("data")
                var success=data.getBoolean("success")

                if(success){

                    var itemlistjson=data.getJSONArray("data")

                    for(i in 0 until itemlistjson.length()){

                        var itemjson=itemlistjson.get(i) as JSONObject

                        var item=Itemdata(itemjson.getString("id"),
                            itemjson.getString("name"),
                            itemjson.getString("cost_for_one")
                        )

                        itemlist.add(item)

                        recycleradapter =
                            Restaurantadapter(
                                this,
                                itemlist,
                                findViewById(R.id.btnproceed),
                                recyclerrestaurant,
                                restaurantid,
                                restaurantname
                            )

                        recyclerrestaurant.layoutManager=layoutmanager
                        recyclerrestaurant.adapter=recycleradapter


                    }


                }else{

                    Toast.makeText(this, data.getString("errorMessage"),
                        Toast.LENGTH_LONG).show()

                }

            },
            Response.ErrorListener {

                Toast.makeText(this,
                    "some Volley error occured",Toast.LENGTH_LONG).show()
            }
        ){
            override fun getHeaders(): MutableMap<String, String> {

                val headers = HashMap<String, String>()
                headers["Content-type"] = "Application/json"
                headers["token"] = "c63912f4cfba72"
                return headers
            }

        }


        queue.add(jsonrequestobj)

        }else{


            val dialog = android.app.AlertDialog.Builder(this)
            dialog.setTitle("Error")
            dialog.setMessage("Internet Connection is not Found")
            dialog.setPositiveButton("Open Settings") { text, listener ->
                val settingsIntent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
                startActivity(settingsIntent)
                finish()

            }

            dialog.setNegativeButton("Exit") { text, listener ->
                ActivityCompat.finishAffinity(this)
            }
            dialog.create()
            dialog.show()

        }

        imgheart.setOnClickListener {


            if(Homeadapter.Restasyntask(this, rest, 3).execute().get()) {

                imgheart.setImageResource(R.drawable.favouriteborder)
                Homeadapter.Restasyntask(this, rest, 2).execute().get()

            }else{

                imgheart.setImageResource(R.drawable.favourite)
                Homeadapter.Restasyntask(this, rest, 1).execute().get()
            }


        }


    }
    fun setuptoolbar(restaurantname:String){

        setSupportActionBar(resttoolbar)
        supportActionBar?.title=restaurantname
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)


    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        if(item.itemId==android.R.id.home)
            onBackPressed()

        return super.onOptionsItemSelected(item)
    }


}
