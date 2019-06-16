package ie.dublinmapper.data.buseireann

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import io.reactivex.Maybe

@Dao
interface BusEireannStopDao {

    @Transaction
    @Query("SELECT * FROM buseireann_stop_locations")
    fun selectAll(): Maybe<List<BusEireannStopEntity>>

}