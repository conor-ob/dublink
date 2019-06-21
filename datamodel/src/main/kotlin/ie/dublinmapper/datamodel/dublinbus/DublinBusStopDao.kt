package ie.dublinmapper.datamodel.dublinbus

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import io.reactivex.Maybe

@Dao
interface DublinBusStopDao {

    @Transaction
    @Query("SELECT * FROM dublinbus_stop_locations")
    fun selectAll(): Maybe<List<DublinBusStopEntity>>

}
