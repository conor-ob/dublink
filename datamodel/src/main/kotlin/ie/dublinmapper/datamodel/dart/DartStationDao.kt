package ie.dublinmapper.datamodel.dart

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import ie.dublinmapper.datamodel.BaseDao
import io.reactivex.Maybe

@Dao
interface DartStationDao {

    @Transaction
    @Query("SELECT * FROM dart_station_locations")
    fun selectAll(): Maybe<List<DartStationEntity>>

}

@Dao
interface DartStationLocationDao : BaseDao<DartStationLocationEntity> {

    @Query("SELECT * FROM dart_station_locations")
    fun selectAll(): Maybe<List<DartStationLocationEntity>>

    @Query("DELETE FROM dart_station_locations")
    fun deleteAll()

}

@Dao
interface DartStationServiceDao : BaseDao<DartStationServiceEntity>
