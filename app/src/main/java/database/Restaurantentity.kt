package database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Restaurantentity (@PrimaryKey val id:String,
                             @ColumnInfo(name = "name")val name:String,
                             @ColumnInfo(name = "rating")val rating:String,
                             @ColumnInfo(name = "costforone")val cost_for_one:String,
                             @ColumnInfo(name = "image") val image_url:String)
