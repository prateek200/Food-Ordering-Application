package database

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [Restaurantentity::class], version = 1)
abstract class Restaurantdatabase : RoomDatabase(){

    abstract fun restdao():Restaurantdao

}