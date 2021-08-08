package com.example.foodapp.fragment

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.provider.Settings
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.foodapp.adapter.Orderhistoryadapter
import com.example.foodapp.R
import model.Itemdata
import model.Previousorders
import org.json.JSONObject
import util.ConnectionManager

class OrderHistoryFragment : Fragment() {

    lateinit var recyclerorder:RecyclerView
    lateinit var layoutManager: RecyclerView.LayoutManager
    lateinit var apdater: Orderhistoryadapter
    lateinit var files: SharedPreferences
    lateinit var progressLayout: RelativeLayout

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        var view=inflater.inflate(R.layout.fragment_order_history, container, false)


        recyclerorder=view.findViewById(R.id.recyclerorder)
        files= this.activity?.getSharedPreferences(getString(R.string.file), Context.MODE_PRIVATE)!!
        progressLayout=view.findViewById(R.id.progressLayout)

        progressLayout.visibility=View.VISIBLE

        var userid=files.getString("userid","-1")

        layoutManager =LinearLayoutManager(activity as Context)

        var list= ArrayList<Previousorders>()



        if (ConnectionManager().checkConnectivity(activity as Context)) {


            var queue = Volley.newRequestQueue(activity as Context)

            var url = "http://13.235.250.119/v2/orders/fetch_result/" + userid

            var jsonrequest = object : JsonObjectRequest(
                Request.Method.GET, url,
                null,
                Response.Listener {

                    progressLayout.visibility = View.GONE

                    var data = it.getJSONObject("data")


                    var success = data.getBoolean("success")

                    if (success) {

                        var infoarray = data.getJSONArray("data")

                        for (j in 0 until infoarray.length()) {

                            var info = infoarray.get(j) as JSONObject

                            var name = info.getString("restaurant_name")
                            var totalcost = info.getString("total_cost")
                            var placed = info.getString("order_placed_at")

                            var fooditems = ArrayList<Itemdata>()

                            var fooditemsjson = info.getJSONArray("food_items")

                            for (i in 0 until fooditemsjson.length()) {

                                var fooditemobject = fooditemsjson.get(i) as JSONObject

                                fooditems.add(
                                    Itemdata(
                                        fooditemobject.getString("food_item_id"),
                                        fooditemobject.getString("name"),
                                        fooditemobject.getString("cost")
                                    )
                                )

                            }


                            list.add(Previousorders(name, totalcost, placed, fooditems))


                            apdater =
                                Orderhistoryadapter(
                                    activity as Context,
                                    list
                                )

                            recyclerorder.layoutManager = layoutManager
                            recyclerorder.adapter = apdater
                        }
                    } else {

                        Toast.makeText(
                            activity as Context, data.getString("errorMessage"),
                            Toast.LENGTH_LONG).show()
                    }

                },
                Response.ErrorListener {

                    Toast.makeText(
                        activity as Context, "some volley error occured",
                        Toast.LENGTH_LONG
                    ).show()

                }


            ) {
                override fun getHeaders(): MutableMap<String, String> {

                    val headers = HashMap<String, String>()
                    headers["Content-type"] = "Application/json"
                    headers["token"] = "c63912f4cfba72"
                    return headers
                }

            }

            queue.add(jsonrequest)
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



        return view
    }

}
