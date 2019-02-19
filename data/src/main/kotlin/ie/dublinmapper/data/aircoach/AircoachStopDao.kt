package ie.dublinmapper.data.aircoach

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import io.reactivex.Maybe

@Dao
interface AircoachStopDao {

    @Transaction
    @Query("SELECT * FROM aircoach_stops")
    fun selectAll(): Maybe<List<AircoachStopEntity>>

}
