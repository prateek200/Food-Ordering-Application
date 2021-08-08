package database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface Restaurantdao {

    @Insert
    fun insertrest(rest:Restaurantentity)

    @Delete
    fun deleterest(rest:Restaurantentity)

    @Query("SELECT * FROM Restaurantentity WHERE id=:str")
    fun findrestbyid(str:String):Restaurantentity

    @Query("SELECT * FROM Restaurantentity")
    fun selectall():List<Restaurantentity>

    @Query("DELETE FROM restaurantentity")
    fun deleteallrow():Unit
}