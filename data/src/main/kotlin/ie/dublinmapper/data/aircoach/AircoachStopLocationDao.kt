package ie.dublinmapper.data.aircoach

import androidx.room.Dao
import androidx.room.Query
import ie.dublinmapper.data.BaseDao
import io.reactivex.Maybe

@Dao
interface AircoachStopLocationDao : BaseDao<AircoachStopLocationEntity> {

    @Query("SELECT * FROM aircoach_stops")
    fun selectAll(): Maybe<List<AircoachStopLocationEntity>>

    @Query("DELETE FROM aircoach_stops")
    fun deleteAll()

}
