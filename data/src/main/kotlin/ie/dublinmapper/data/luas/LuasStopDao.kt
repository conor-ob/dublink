package ie.dublinmapper.data.luas

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import io.reactivex.Maybe

@Dao
interface LuasStopDao {

    @Transaction
    @Query("SELECT * FROM luas_stop_locations")
    fun selectAll(): Maybe<List<LuasStopEntity>>

}
