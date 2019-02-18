package ie.dublinmapper.data.dublinbus

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import io.reactivex.Maybe

@Dao
interface DublinBusStopDao {

    @Transaction
    @Query("SELECT * FROM dublinbus_stops")
    fun selectAll(): Maybe<List<DublinBusStopEntity>>

}
