package ie.dublinmapper.data.favourite

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import ie.dublinmapper.data.BaseDao
import io.reactivex.Maybe

@Dao
interface FavouriteDao : BaseDao<FavouriteEntity> {

    @Transaction
    @Query("SELECT * FROM favourites")
    fun selectAll(): Maybe<List<FavouriteEntity>>

}
