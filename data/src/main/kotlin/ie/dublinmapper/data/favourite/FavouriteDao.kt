package ie.dublinmapper.data.favourite

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import io.reactivex.Maybe

@Dao
interface FavouriteDao {

    @Transaction
    @Query("SELECT * FROM favourite_locations")
    fun selectAll(): Maybe<List<FavouriteEntity>>

}
