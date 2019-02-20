package ie.dublinmapper.data.dart

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import io.reactivex.Maybe

@Dao
interface DartStationDao {

    @Transaction
    @Query("SELECT * FROM dart_stations")
    fun selectAll(): Maybe<List<DartStationEntity>>

}
