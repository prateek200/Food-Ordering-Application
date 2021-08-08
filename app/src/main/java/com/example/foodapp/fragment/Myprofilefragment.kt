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
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.foodapp.R
import org.json.JSONObject
import util.ConnectionManager

/**
 * A simple [Fragment] subclass.
 */
class Myprofilefragment : Fragment() {

    lateinit var txtname:TextView
    lateinit var txtnumber:TextView
    lateinit var txtemail:TextView
    lateinit var txtaddress:TextView
    lateinit var files: SharedPreferences
    lateinit var progressLayout: RelativeLayout


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        var view=inflater.inflate(R.layout.fragment_myprofilefragment, container, false)

        txtname=view.findViewById(R.id.txtname)
        txtnumber=view.findViewById(R.id.txtnumber)
        txtemail=view.findViewById(R.id.txtemail)
        txtaddress=view.findViewById(R.id.txtaddress)

        progressLayout=view.findViewById(R.id.progressLayout)

        progressLayout.visibility=View.VISIBLE

        files= this.getActivity()?.getSharedPreferences(getString(R.string.file), Context.MODE_PRIVATE)!!


        var num=files.getString("mobile_number","-1")
        var pass=files.getString("password","-1")

        if(num!="-1" && pass!="-1"){

            var jsonobj= JSONObject()
            jsonobj.put("mobile_number",num)
            jsonobj.put("password",pass)


            if (ConnectionManager().checkConnectivity(activity as Context)) {


                var queue = Volley.newRequestQueue(activity as Context)

                var url = "http://13.235.250.119/v2/login/fetch_result/"

                var jsonrequest = object : JsonObjectRequest(
                    Request.Method.POST, url,
                    jsonobj,
                    Response.Listener {

                        progressLayout.visibility = View.GONE

                        var data = it.getJSONObject("data")

                        var success = data.getBoolean("success")

                        if (success) {

                            var info = data.getJSONObject("data")

                            txtname.text = info.getString("name")
                            txtnumber.text = info.getString("mobile_number")
                            txtemail.text = info.getString("email")
                            txtaddress.text = info.getString("address")

                        } else {
                            Toast.makeText(
                                activity as Context, data.getString("errorMessage"),
                                Toast.LENGTH_LONG
                            ).show()
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



        }

        return view
    }

}
