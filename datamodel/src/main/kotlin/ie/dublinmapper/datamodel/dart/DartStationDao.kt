package ie.dublinmapper.datamodel.dart

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import io.reactivex.Maybe

@Dao
interface DartStationDao {

    @Transaction
    @Query("SELECT * FROM dart_station_locations")
    fun selectAll(): Maybe<List<DartStationEntity>>

}
