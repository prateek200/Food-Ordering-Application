package com.example.foodapp.Activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.SystemClock
import android.provider.Settings
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.foodapp.R
import org.json.JSONObject
import util.ConnectionManager

class ResetActivity : AppCompatActivity() {

    lateinit var etotp:EditText
    lateinit var etpassword:EditText
    lateinit var etconfirmpssword:EditText
    lateinit var btnsumbit:Button
    var lastClickTime:Long= 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reset)

        etotp=findViewById(R.id.etotp)
        etpassword=findViewById(R.id.etpassword)
        etconfirmpssword=findViewById(R.id.etconfirmpssword)
        btnsumbit=findViewById(R.id.btnsumbit)

        var number=intent.getStringExtra("number")

        intent.removeExtra("number")

        btnsumbit.setOnClickListener {

            if (SystemClock.elapsedRealtime() - lastClickTime < 1000){
                return@setOnClickListener;
            }
            lastClickTime= SystemClock.elapsedRealtime()

            var password=etpassword.text.toString()
            var confirmpassword=etconfirmpssword.text.toString()
            var otp=etotp.text.toString()

            var checker:Boolean=true

            if(Register().isempty(password) || password.length<4){

                etpassword.setError("Password is requried of minimum 4 characters")
                checker=false
            }

            if(Register().isempty(confirmpassword) ||  confirmpassword.length<4){

                etconfirmpssword.setError("Confirmation Password is requried " +
                        "of minimum 4 characters")
                checker=false
            }

            if( checker==true && confirmpassword!=password){

                etconfirmpssword.setError("Confirmation Password doesnot match Password")
                checker=false

            }
            if(otp.length!=4){

                etotp.setError("OTP  should be of 4 characters")
                checker=false

            }

            if(!checker)
                return@setOnClickListener


            if (ConnectionManager().checkConnectivity(this)) {

                var url = "http://13.235.250.119/v2/reset_password/fetch_result"
                var queue = Volley.newRequestQueue(this)

                var jsonobj = JSONObject()
                jsonobj.put("mobile_number", number)
                jsonobj.put("otp", otp)
                jsonobj.put("password", password)


                var jsonrequest = object : JsonObjectRequest(Request.Method.POST, url, jsonobj,

                    Response.Listener {


                        var data = it.getJSONObject("data")

                        println("data is $data")

                        var success = data.getBoolean("success")


                        if (success) {

                            var intent = Intent(this, LoginActivity::class.java)
                            startActivity(intent)
                            finishAffinity()


                        } else {

                            Toast.makeText(
                                this,
                                data.getString("errorMessage"), Toast.LENGTH_LONG
                            ).show()
                        }

                    }, Response.ErrorListener {

                        Toast.makeText(
                            this,
                            "Some Volley error occured", Toast.LENGTH_LONG
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
}
