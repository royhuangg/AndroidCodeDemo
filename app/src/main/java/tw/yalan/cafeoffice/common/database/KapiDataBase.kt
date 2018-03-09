package tw.yalan.cafeoffice.common.database

import android.arch.persistence.room.RoomDatabase
import android.arch.persistence.room.TypeConverter
import android.arch.persistence.room.TypeConverters
import com.google.gson.GsonBuilder
import tw.yalan.cafeoffice.common.database.dao.ShopDao
import tw.yalan.cafeoffice.model.database.ShopArea

/**
 * Created by Alan Ding on 2017/10/7.
 */
//@android.arch.persistence.room.Database(entities = arrayOf(ShopArea::class), version = 2)
@TypeConverters(RoomConverters::class)
abstract class KapiDataBase : RoomDatabase() {
//    abstract fun shopDao(): ShopDao


}

class RoomConverters {
    companion object {
        @TypeConverter
        @JvmStatic
        fun fromDouble(value: Double?): String? {
            if (value == null) return null
            return value.toString()
        }

        @TypeConverter
        @JvmStatic
        fun toDouble(value: String?): Double? {
            if (value == null) return null
            return value.toDouble()
        }

    }}