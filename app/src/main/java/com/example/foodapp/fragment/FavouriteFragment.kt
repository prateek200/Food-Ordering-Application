package com.example.foodapp.fragment

import android.content.Context
import android.os.AsyncTask
import android.os.Bundle
import android.os.SystemClock
import android.view.*
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.example.foodapp.adapter.Favouriteadapter
import com.example.foodapp.adapter.Homeadapter
import com.example.foodapp.R
import database.Restaurantdatabase
import database.Restaurantentity
import model.Restaurant
import java.util.*
import kotlin.collections.ArrayList

/**
 * A simple [Fragment] subclass.
 */
class FavouriteFragment : Fragment() {

    lateinit var recyclerfav:RecyclerView
    lateinit var layoutmanager:RecyclerView.LayoutManager
    lateinit var recycleradapter: Favouriteadapter
    lateinit var etsearch: EditText
    lateinit var btnsearch: Button
    var lastClickTime:Long= 0
    lateinit var list:ArrayList<Restaurantentity>
    lateinit var progressLayout: RelativeLayout
    lateinit var txtnorestfound:TextView
    lateinit var imgheart:ImageView
    var restaurantlistsave=ArrayList<Restaurantentity>()

    var ratingComparator = Comparator<Restaurantentity>{Restaurant1, Restaurant2 ->

        if (Restaurant1.rating.compareTo(Restaurant2.rating, true) == 0) {
            // sort according to name if rating is same
            Restaurant1.name.compareTo(Restaurant2.name, true)
        } else {
            Restaurant2.rating.compareTo(Restaurant1.rating, true)
        }
    }
    var costComparatorhightolow = Comparator<Restaurantentity>{Restaurant1, Restaurant2 ->

        if (Restaurant1.cost_for_one.compareTo(Restaurant2.cost_for_one, true) == 0) {
            // sort according to name if rating is same
            Restaurant1.name.compareTo(Restaurant2.name, true)
        } else {
            Restaurant2.cost_for_one.compareTo(Restaurant1.cost_for_one, true)
        }
    }

    var costComparatorlowtohigh = Comparator<Restaurantentity>{Restaurant1, Restaurant2 ->

        if (Restaurant1.cost_for_one.compareTo(Restaurant2.cost_for_one, true) == 0) {
            // sort according to name if rating is same
            Restaurant1.name.compareTo(Restaurant2.name, true)
        } else {
            Restaurant1.cost_for_one.compareTo(Restaurant2.cost_for_one, true)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        var view=inflater.inflate(R.layout.fragment_favourite, container, false)

        layoutmanager=LinearLayoutManager(activity as Context)
        recyclerfav=view.findViewById(R.id.recyclerfav)
        etsearch=view.findViewById(R.id.etsearch)
        btnsearch=view.findViewById(R.id.btnsearch)
        txtnorestfound=view.findViewById(R.id.txtnorestfound)
        imgheart=view.findViewById(R.id.imgheart)

        setHasOptionsMenu(true)

        progressLayout=view.findViewById(R.id.progressLayout)

        progressLayout.visibility=View.VISIBLE

        list= FavRestasyntask(
            activity as Context
        ).execute().get() as ArrayList<Restaurantentity>


        for(i in 0 until list.size)
            restaurantlistsave.add(list[i])


        if(list.size==0){
            txtnorestfound.visibility=View.VISIBLE
            imgheart.visibility=View.VISIBLE
        }
        progressLayout.visibility=View.GONE

        recycleradapter=
            Favouriteadapter(
                activity as Context,
                list
            )

        recyclerfav.layoutManager=layoutmanager
        recyclerfav.adapter=recycleradapter

        btnsearch.setOnClickListener {

            restaurantlistsave= FavRestasyntask(activity as Context).
            execute().get() as ArrayList<Restaurantentity>

            var seachpart=etsearch.text.toString().toUpperCase()

            txtnorestfound.visibility=View.GONE
            imgheart.visibility=View.GONE

            if(seachpart.length==0){

                list.clear()

                for(i in 0 until restaurantlistsave.size)
                    list.add(restaurantlistsave[i])

                if(list.size==0){
                    txtnorestfound.visibility=View.VISIBLE
                    imgheart.visibility=View.VISIBLE
                }
                recycleradapter.notifyDataSetChanged()
                return@setOnClickListener
            }

            list.clear()

            for( i in 0 until restaurantlistsave.size){


                var nameofrest=restaurantlistsave[i].name

                var j=0
                var n=seachpart.length
                var m=nameofrest.length

                if(n>m)
                    continue

                while(j<=m-n){

                    var name=nameofrest.subSequence(j,j+n).toString().toUpperCase()

                    if(name==seachpart){
                        list.add(restaurantlistsave[i])
                        break
                    }

                    j++

                }

            }

            if(list.size==0){
                txtnorestfound.visibility=View.VISIBLE
                imgheart.visibility=View.VISIBLE
            }
            recycleradapter.notifyDataSetChanged()

            return@setOnClickListener


        }

        return view
    }


    class FavRestasyntask(val  context:Context):
        AsyncTask<Void, Void, List<Restaurantentity>>(){

        var db= Room.databaseBuilder(context,
            Restaurantdatabase::class.java,"favrestaurant").build()

        override fun doInBackground(vararg params: Void?): List<Restaurantentity> {

            return db.restdao().selectall()

        }


    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {

        inflater?.inflate(R.menu.menuhome, menu)

    }
    override fun onOptionsItemSelected(item: MenuItem?): Boolean {

        val id = item?.itemId



        if (id == R.id.sort && SystemClock.elapsedRealtime() - lastClickTime > 1000){

            lastClickTime= SystemClock.elapsedRealtime()

            val dialog = androidx.appcompat.app.AlertDialog.Builder(activity as Context)

            dialog.setTitle("Sort By?")

            var arr= arrayOf("Cost(Low to High)", "Cost(High to Low)", "Rating")

            dialog.setItems(arr){text,which->

                when(which) {

                    0 -> {

                        Collections.sort(list, costComparatorlowtohigh)
                        recycleradapter.notifyDataSetChanged()

                    }

                    1->{

                        Collections.sort(list, costComparatorhightolow)
                        recycleradapter.notifyDataSetChanged()

                    }

                    2->{
                        Collections.sort(list, ratingComparator)
                        recycleradapter.notifyDataSetChanged()
                    }

                }
            }


            dialog.create()
            dialog.show()
        }

        return super.onOptionsItemSelected(item)

    }

}
