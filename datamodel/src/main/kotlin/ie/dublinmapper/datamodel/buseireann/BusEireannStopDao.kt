package ie.dublinmapper.datamodel.buseireann

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import ie.dublinmapper.datamodel.BaseDao
import io.reactivex.Maybe

@Dao
interface BusEireannStopDao {

    @Transaction
    @Query("SELECT * FROM buseireann_stop_locations")
    fun selectAll(): Maybe<List<BusEireannStopEntity>>

}

@Dao
interface BusEireannStopLocationDao : BaseDao<BusEireannStopLocationEntity> {

    @Query("SELECT * FROM buseireann_stop_locations")
    fun selectAll(): Maybe<List<BusEireannStopLocationEntity>>

    @Query("DELETE FROM buseireann_stop_locations")
    fun deleteAll()

}

@Dao
interface BusEireannStopServiceDao : BaseDao<BusEireannStopServiceEntity>
