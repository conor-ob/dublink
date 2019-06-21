package ie.dublinmapper.datamodel.dublinbus

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import ie.dublinmapper.datamodel.BaseDao
import io.reactivex.Maybe

@Dao
interface DublinBusStopDao {

    @Transaction
    @Query("SELECT * FROM dublinbus_stop_locations")
    fun selectAll(): Maybe<List<DublinBusStopEntity>>

}

@Dao
interface DublinBusStopLocationDao : BaseDao<DublinBusStopLocationEntity> {

    @Query("SELECT * FROM dublinbus_stop_locations")
    fun selectAll(): Maybe<List<DublinBusStopLocationEntity>>

    @Query("DELETE FROM dublinbus_stop_locations")
    fun deleteAll()

}

@Dao
interface DublinBusStopServiceDao : BaseDao<DublinBusStopServiceEntity>
