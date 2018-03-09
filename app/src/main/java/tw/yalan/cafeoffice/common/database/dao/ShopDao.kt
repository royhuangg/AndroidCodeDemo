package tw.yalan.cafeoffice.common.database.dao

import android.arch.persistence.room.*
import io.reactivex.Flowable
import tw.yalan.cafeoffice.model.database.ShopArea

/**
 * Created by Alan Ding on 2017/10/7.
 */
@Dao
interface ShopDao {
    @Query("SELECT * FROM shoparea")
    fun getAll(): Flowable<List<ShopArea>>
}