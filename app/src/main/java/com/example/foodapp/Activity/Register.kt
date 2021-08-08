package com.example.foodapp.Activity

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.SystemClock
import android.provider.Settings
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.foodapp.R
import org.json.JSONObject
import util.ConnectionManager

class Register : AppCompatActivity() {

    lateinit var toolbar:Toolbar
    lateinit var etname:EditText
    lateinit var etemailid:EditText
    lateinit var etmobilenumber:EditText
    lateinit var etdilevaryadd:EditText
    lateinit var etpassword:EditText
    lateinit var etconfirmpssword:EditText
    lateinit var btnregister:Button
    lateinit var files: SharedPreferences
    var lastClickTime:Long= 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        toolbar=findViewById(R.id.toolbar)
        etname=findViewById(R.id.etname)
        etemailid=findViewById(R.id.etemailid)
        etmobilenumber=findViewById(R.id.etmobilenumber)
        etdilevaryadd=findViewById(R.id.etdilevaryadd)
        etpassword=findViewById(R.id.etpassword)
        etconfirmpssword=findViewById(R.id.etconfirmpssword)
        btnregister=findViewById(R.id.btnregister)
        files=getSharedPreferences(getString(R.string.file), Context.MODE_PRIVATE)

        btnregister.setOnClickListener {

            if (SystemClock.elapsedRealtime() - lastClickTime < 1000){
                return@setOnClickListener;
            }
            lastClickTime=SystemClock.elapsedRealtime()

            var name=etname.text.toString()
            var emailid=etemailid.text.toString()
            var mobilenumber=etmobilenumber.text.toString()
            var dilevaryadd=etdilevaryadd.text.toString()
            var  password=etpassword.text.toString()
            var confirmpssword=etconfirmpssword.text.toString()

            var check=true

            if(isempty(name)){

                etname.setError("Name is required of minimum 3 characters")
                check=false
            }

            if(isempty(dilevaryadd)){

                etdilevaryadd.setError("Deilvery Address is required")
                check=false
            }

            if(mobilenumber.length!=10){

                etmobilenumber.setError("Enter valid mobile number")
                check=false
            }

            if(isempty(password)){

                etpassword.setError("Password is required of minimum 4 characters")
                check=false
            }

            if(isempty(confirmpssword)){

                etconfirmpssword.setError("Confirmation Password is required " +
                        "of minimum 4 characters")
                check=false
            }

            if(!isempty(password) && !isempty(confirmpssword) && password!=confirmpssword){

                etconfirmpssword.setError("Confirmation Password does not match the passoword")
                check=false
            }

            if(!isemailvalid(emailid)){

                etemailid.setError("Email is invalid")
                check=false
            }

            if (ConnectionManager().checkConnectivity(this)) {


                if(check==true ) {

                    var queue = Volley.newRequestQueue(this)

                    var url = "http://13.235.250.119/v2/register/fetch_result"

                    var jsonobj = JSONObject()
                    jsonobj.put("name", name)
                    jsonobj.put("mobile_number", mobilenumber)
                    jsonobj.put("password", password)
                    jsonobj.put("address", dilevaryadd)
                    jsonobj.put("email", emailid)


                    var jsonrequest = object : JsonObjectRequest(Request.Method.POST, url, jsonobj,

                        Response.Listener {

                            var data = it.getJSONObject("data")

                            var success = data.getBoolean("success")

                            if (success) {

                                var intent = Intent(this, User::class.java)

                                files.edit().putString("mobile_number", mobilenumber).apply()
                                files.edit().putString("password", password).apply()

                                startActivity(intent)
                                finishAffinity()

                            } else {

                                Toast.makeText(
                                    this, data.getString("errorMessage"),
                                    Toast.LENGTH_LONG
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


        }

        setuptoolbar()

    }
    fun setuptoolbar(){

        setSupportActionBar(toolbar)
        supportActionBar?.title="Register Yourself"
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        if(item.itemId==android.R.id.home)
            onBackPressed()

        return super.onOptionsItemSelected(item)
    }

    fun isempty(str:String):Boolean{

        if(str.length==0)
            return true

        return false

    }

    fun isemailvalid(str:String):Boolean{

        return(android.util.Patterns.EMAIL_ADDRESS.matcher(str).matches())

    }
}
