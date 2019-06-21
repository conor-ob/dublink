package ie.dublinmapper.datamodel.aircoach

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import ie.dublinmapper.datamodel.BaseDao
import io.reactivex.Maybe

@Dao
interface AircoachStopDao {

    @Transaction
    @Query("SELECT * FROM aircoach_stop_locations")
    fun selectAll(): Maybe<List<AircoachStopEntity>>

}

@Dao
interface AircoachStopLocationDao : BaseDao<AircoachStopLocationEntity> {

    @Query("SELECT * FROM aircoach_stop_locations")
    fun selectAll(): Maybe<List<AircoachStopLocationEntity>>

    @Query("DELETE FROM aircoach_stop_locations")
    fun deleteAll()

}

@Dao
interface AircoachStopServiceDao : BaseDao<AircoachStopServiceEntity>
