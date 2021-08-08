package com.example.foodapp.Activity

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.view.MenuItem
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.foodapp.adapter.Fooditemsapadter
import com.example.foodapp.R
import com.google.gson.Gson
import model.Itemdata
import org.json.JSONArray
import org.json.JSONObject
import util.ConnectionManager

class CartActivity : AppCompatActivity() {

    lateinit var recycleritems:RecyclerView
    lateinit var layoutmanager:RecyclerView.LayoutManager
    lateinit var adapter: Fooditemsapadter
    lateinit var btnplaceorder:Button
    lateinit var txtorderedreaturant:TextView
    lateinit var files: SharedPreferences
    lateinit var toolbar:Toolbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cart)

        recycleritems=findViewById(R.id.recycleritems)
        layoutmanager=LinearLayoutManager(this)
        btnplaceorder=findViewById(R.id.btnplaceorder)
        txtorderedreaturant=findViewById(R.id.txtorderedreaturant)
        files=getSharedPreferences(getString(R.string.file), Context.MODE_PRIVATE)
        toolbar=findViewById(R.id.toolbar)

        toolbarsetup()

        var totalcost=0

        var jsonarraylist=intent.getStringArrayListExtra("jsonarraylist")
        var restaurantid=intent.getStringExtra("restaurantid")
        var restaurantname=intent.getStringExtra("retaurantname")
        var userid=files.getString("userid","-1")

        txtorderedreaturant.text="Ordering From: $restaurantname"

        var fooditemlist=ArrayList<Itemdata>()

        var food=JSONArray()

        for(i in 0 until jsonarraylist.size){

            var fooditem=Gson().fromJson(jsonarraylist[i],Itemdata::class.java)
            totalcost+=fooditem.costforone.toInt()
            fooditemlist.add(fooditem)

            var fooditemid=JSONObject()
            fooditemid.put("food_item_id",fooditem.itemid)
            food.put(fooditemid)

        }

        btnplaceorder.text="Place Order( Rs. $totalcost )"
        adapter= Fooditemsapadter(this, fooditemlist)

        recycleritems.layoutManager=layoutmanager
        recycleritems.adapter=adapter


        var jsonobject=JSONObject()

        jsonobject.put("user_id",userid)
        jsonobject.put("restaurant_id",restaurantid)
        jsonobject.put("total_cost",totalcost.toString())
        jsonobject.put("food",food)


        btnplaceorder.setOnClickListener {

            println("json object is $jsonobject")

            if (ConnectionManager().checkConnectivity(this)) {

                var queue= Volley.newRequestQueue(this)

            var url="http://13.235.250.119/v2/place_order/fetch_result/"

            var jsonrequest=object: JsonObjectRequest(
                Request.Method.POST,url,
                jsonobject,
                Response.Listener {

                    println("output is $it")
                   var data=it.getJSONObject("data")

                    var success=data.getBoolean("success")
                    println("success id $success")

                    if(success){

                        var intent=Intent(this,
                            SuccessfulActivity::class.java)
                        startActivity(intent)

                    }else{

                        Toast.makeText(this,data.getString("errorMessage"),
                            Toast.LENGTH_LONG).show()

                    }

                },
                Response.ErrorListener {

                 Toast.makeText(this,
                     "Some Volley error occured",Toast.LENGTH_LONG).show()
                }

            ){
                override fun getHeaders(): MutableMap<String, String> {

                    val headers = HashMap<String, String>()
                    headers["Content-type"] = "Application/json"
                    headers["token"] = "c63912f4cfba72"
                    return headers
                }

            }

            queue.add(jsonrequest)

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
        }


    }

    fun toolbarsetup(){

        setSupportActionBar(toolbar)
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        if(item.itemId==android.R.id.home)
            onBackPressed()

        return super.onOptionsItemSelected(item)
    }


}
