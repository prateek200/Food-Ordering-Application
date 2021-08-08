package com.example.foodapp.Activity

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.room.Room
import com.example.foodapp.*
import com.example.foodapp.fragment.*
import com.google.android.material.navigation.NavigationView
import database.Restaurantdatabase

class User : AppCompatActivity() {

    lateinit var toolbar:androidx.appcompat.widget.Toolbar
    lateinit var drawerlayout:DrawerLayout
    lateinit var navigationview:NavigationView
    lateinit var txtnameofuser:TextView
    lateinit var txtphnnumber:TextView
    lateinit var files: SharedPreferences
   var previousitem: MenuItem? =null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user)

        toolbar=findViewById(R.id.toolbar)
        drawerlayout=findViewById(R.id.drawerlayout)
        navigationview=findViewById(R.id.navigationview)

        txtnameofuser=navigationview.getHeaderView(0).findViewById(R.id.txtnameofuser)
        txtphnnumber=navigationview.getHeaderView(0).findViewById(R.id.txtphnnumber)


        files=getSharedPreferences(getString(R.string.file), Context.MODE_PRIVATE)

        var num=files.getString("mobile_number","-1")
        var name=files.getString("name","-1")

        txtnameofuser.text=name
        txtphnnumber.text=num

        setuptoolbar()
        dashboardsetter()


        var actionbartoggle=ActionBarDrawerToggle(this,drawerlayout,
            R.string.open,
            R.string.close
        )

        drawerlayout.addDrawerListener(actionbartoggle)
        actionbartoggle.syncState()


        navigationview.setNavigationItemSelectedListener {

            if(previousitem!=null && it.itemId!= R.id.logout){

                previousitem?.isChecked=false

            }

            if(it.itemId!= R.id.logout) {
                it.isCheckable = true
                it.isChecked = true
            }

            when(it.itemId){


                R.id.home ->{


                    dashboardsetter()

                }

                R.id.myprofile ->{


                 supportFragmentManager.beginTransaction()
                     .replace(
                         R.id.framelayout,
                         Myprofilefragment()
                     ).commit()

                    supportActionBar?.title="My Profile"
                    drawerlayout.closeDrawers()
                }

                R.id.favourite ->{


                supportFragmentManager.beginTransaction().
                        replace(
                            R.id.framelayout,
                            FavouriteFragment()
                        ).commit()

                    supportActionBar?.title="Favourite Restaurants"
                    drawerlayout.closeDrawers()
                }

                R.id.orderhistory ->{
                    supportFragmentManager.beginTransaction().
                    replace(
                        R.id.framelayout,
                        OrderHistoryFragment()
                    ).commit()

                    supportActionBar?.title="Previous Orders"
                    drawerlayout.closeDrawers()

                }

                R.id.faqs ->{


                    supportFragmentManager.beginTransaction().
                            replace(
                                R.id.framelayout,
                                FaqsFragment()
                            ).commit()

                    supportActionBar?.title="Frequently Asked Questions"
                    drawerlayout.closeDrawers()

                }
                R.id.logout ->{

                    val dialog =AlertDialog.Builder(this)
                    dialog.setTitle("Log Out")
                    dialog.setMessage("Do you want to Log Out?")
                    drawerlayout.closeDrawers()
                    dialog.setPositiveButton("Yes"){text, listener->

                        files.edit().clear().apply()

                        finishAffinity()

                        asyntask(this).execute().get()

                        var intent=Intent(this,
                            LoginActivity::class.java)
                        startActivity(intent)
                    }

                    dialog.setNegativeButton("No"){text, listener->

                        if(previousitem==null){

                            navigationview.setCheckedItem(R.id.home)

                        }else {

                            previousitem?.isCheckable = true
                            previousitem?.isChecked = true
                        }
                    }
                    dialog.create()
                    dialog.show()

                }

            }

            if(it.itemId!= R.id.logout) {
                previousitem=it
            }

        return@setNavigationItemSelectedListener true

    }


    }

    fun dashboardsetter(){


        supportFragmentManager.beginTransaction().replace(
            R.id.framelayout,
            HomeFragment()
        ).commit()

        supportActionBar?.title="All Restaurants"

        drawerlayout.closeDrawers()
        navigationview.setCheckedItem(R.id.home)
    }

    fun setuptoolbar(){

        setSupportActionBar(toolbar)
        supportActionBar?.title="user"
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        var id=item.itemId

        if(id==android.R.id.home){
            drawerlayout.openDrawer(GravityCompat.START)
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {

        var fragment=supportFragmentManager.findFragmentById(R.id.framelayout)

        when(fragment){

            !is HomeFragment ->dashboardsetter()

            else ->super.onBackPressed()
        }

    }

    class asyntask(var context:Context):AsyncTask<Void,Void,Boolean>(){

        override fun doInBackground(vararg params: Void?): Boolean {


            var db= Room.databaseBuilder(context,
                Restaurantdatabase::class.java,"favrestaurant").build()

            db.restdao().deleteallrow()

            db.close()

            return true

        }


    }

}
