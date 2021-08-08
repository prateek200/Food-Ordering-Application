package com.example.foodapp.Activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.SystemClock
import android.provider.Settings
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.foodapp.R
import org.json.JSONObject
import util.ConnectionManager

class ForgetPassword : AppCompatActivity() {

    lateinit var etmobilenumber:EditText
    lateinit var etemailid:EditText
    lateinit var btnnext:Button
    var lastClickTime:Long= 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forget_password)

        etmobilenumber=findViewById(R.id.etmobilenumber)
        etemailid=findViewById(R.id.etemailid)
        btnnext=findViewById(R.id.btnnext)

        btnnext.setOnClickListener {
            if (SystemClock.elapsedRealtime() - lastClickTime < 1000){
                return@setOnClickListener;
            }
            lastClickTime= SystemClock.elapsedRealtime()

            var check=true

            var number=etmobilenumber.text.toString()
            var email=etemailid.text.toString()

            if(Register().isempty(number)){

                etmobilenumber.setError("Mobile number is required")
                check=false
            }

            if(!Register().isemailvalid(email)){

                etemailid.setError("Email is invalid")
                check=false
            }

            if(!Register().isempty(number) && number.length!=10){

                etmobilenumber.setError("Mobile number is invalid")
                check=false
            }


            if(!check)
                return@setOnClickListener

            if (ConnectionManager().checkConnectivity(this)) {


            var url="http://13.235.250.119/v2/forgot_password/fetch_result"
            var queue= Volley.newRequestQueue(this)

            var jsonobj=JSONObject()
                jsonobj.put("mobile_number",number)
                jsonobj.put("email",email)


            var jsonrequest=object:JsonObjectRequest(Request.Method.POST,url,jsonobj,

                Response.Listener {


                    var data=it.getJSONObject("data")

                    var success=data.getBoolean("success")


                    if(success){

                        var dialog=AlertDialog.Builder(this)
                        dialog.setTitle("Information")
                        dialog.setMessage("Please Refer to the email for the OTP")

                        dialog.setPositiveButton("OK") { text , listener->

                            etmobilenumber.text.clear()
                            etemailid.text.clear()

                            var intent = Intent(this, ResetActivity::class.java)
                            intent.putExtra("number",number)
                            startActivity(intent)

                        }
                        dialog.create()
                        dialog.show()


                    }else{

                        Toast.makeText(this,
                            data.getString("errorMessage"),Toast.LENGTH_LONG).show()
                    }

                }, Response.ErrorListener {

                    Toast.makeText(this,
                        "Some Volley error occured", Toast.LENGTH_LONG).show()

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
}
