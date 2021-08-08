package com.example.foodapp.Activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.SystemClock
import android.widget.Button
import com.example.foodapp.R

class SuccessfulActivity : AppCompatActivity() {

    lateinit var btnok:Button
    var lastClickTime:Long= 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_successful)

        btnok=findViewById(R.id.btnok)

        btnok.setOnClickListener {

            if (SystemClock.elapsedRealtime() - lastClickTime < 1000){
                return@setOnClickListener;
            }


            var intent= Intent(this, User::class.java)
            finishAffinity()
            startActivity(intent)

        }

    }

    override fun onBackPressed() {

    }
}
