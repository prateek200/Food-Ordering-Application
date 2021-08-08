package com.example.foodapp.Activity

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.SystemClock
import android.provider.Settings
import android.widget.Button
import android.widget.EditText
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

class LoginActivity : AppCompatActivity() {

    lateinit var txtregister:TextView
    lateinit var etmobilenum:EditText
    lateinit var etpassword:EditText
    lateinit var btnlogin:Button
    lateinit var files:SharedPreferences
    lateinit var txtforgetpass:TextView
    var lastClickTime:Long= 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        files=getSharedPreferences(getString(R.string.file), Context.MODE_PRIVATE)

        if(files.getBoolean("login",false)==true){

            var intent=Intent(this, User::class.java)

            startActivity(intent)
            finish()
        }


        setContentView(R.layout.activity_main2)

        txtregister=findViewById(R.id.txtregister)
        etmobilenum=findViewById(R.id.etmobilenum)
        etpassword=findViewById(R.id.etpassword)
        btnlogin=findViewById(R.id.btnlogin)
        txtforgetpass=findViewById(R.id.txtforgetpass)


        btnlogin.setOnClickListener {

            if (SystemClock.elapsedRealtime() - lastClickTime < 1000) {
                return@setOnClickListener;
            }
            lastClickTime = SystemClock.elapsedRealtime().toLong()

            var num = etmobilenum.getText().toString()
            var pass = etpassword.getText().toString()

            var jsonobj = JSONObject()
            jsonobj.put("mobile_number", num)
            jsonobj.put("password", pass)


            if (ConnectionManager().checkConnectivity(this)) {


                var queue = Volley.newRequestQueue(this@LoginActivity)

                var url = "http://13.235.250.119/v2/login/fetch_result/"

                var jsonrequest = object : JsonObjectRequest(Request.Method.POST, url,
                    jsonobj,
                    Response.Listener {

                        var data = it.getJSONObject("data")

                        var success = data.getBoolean("success")

                        if (success) {

                            var info = data.getJSONObject("data")

                            var intent = Intent(this, User::class.java)

                            files.edit().putString("mobile_number", num).apply()
                            files.edit().putString("password", pass).apply()
                            files.edit().putString("userid", info.getString("user_id")).apply()
                            files.edit().putBoolean("login", true).apply()
                            files.edit().putString("name", info.getString("name")).apply()


                            startActivity(intent)
                            finish()

                        } else {

                            Toast.makeText(
                                this, data.getString("errorMessage"),
                                Toast.LENGTH_LONG
                            ).show()
                        }

                    },
                    Response.ErrorListener {

                        Toast.makeText(
                            this, "some volley error occured",
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


                val dialog = AlertDialog.Builder(this)
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


        txtregister.setOnClickListener {

            var intent= Intent(this, Register::class.java)
            startActivity(intent)

        }

        txtforgetpass.setOnClickListener {

            var intent= Intent(this,
                ForgetPassword::class.java)
            startActivity(intent)

        }

    }

}
