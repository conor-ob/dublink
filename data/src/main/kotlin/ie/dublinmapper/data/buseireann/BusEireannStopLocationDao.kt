package ie.dublinmapper.data.buseireann

import androidx.room.Dao
import androidx.room.Query
import ie.dublinmapper.data.BaseDao
import io.reactivex.Maybe

@Dao
interface BusEireannStopLocationDao : BaseDao<BusEireannStopLocationEntity> {

    @Query("SELECT * FROM buseireann_stop_locations")
    fun selectAll(): Maybe<List<BusEireannStopLocationEntity>>

    @Query("DELETE FROM buseireann_stop_locations")
    fun deleteAll()

}
