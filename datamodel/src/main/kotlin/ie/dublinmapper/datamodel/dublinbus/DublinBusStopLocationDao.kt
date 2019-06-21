package ie.dublinmapper.datamodel.dublinbus

import androidx.room.Dao
import androidx.room.Query
import ie.dublinmapper.datamodel.BaseDao
import io.reactivex.Maybe

@Dao
interface DublinBusStopLocationDao : BaseDao<DublinBusStopLocationEntity> {

    @Query("SELECT * FROM dublinbus_stop_locations")
    fun selectAll(): Maybe<List<DublinBusStopLocationEntity>>

    @Query("DELETE FROM dublinbus_stop_locations")
    fun deleteAll()

}
