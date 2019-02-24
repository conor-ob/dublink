package ie.dublinmapper.data.dart

import androidx.room.Dao
import androidx.room.Query
import ie.dublinmapper.data.BaseDao
import io.reactivex.Maybe

@Dao
interface DartStationLocationDao : BaseDao<DartStationLocationEntity> {

    @Query("SELECT * FROM dart_station_locations")
    fun selectAll(): Maybe<List<DartStationLocationEntity>>

    @Query("DELETE FROM dart_station_locations")
    fun deleteAll()

}
