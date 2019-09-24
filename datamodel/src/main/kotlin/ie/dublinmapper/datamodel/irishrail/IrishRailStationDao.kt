package ie.dublinmapper.datamodel.irishrail

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import ie.dublinmapper.datamodel.BaseDao
import io.reactivex.Maybe

@Dao
interface IrishRailStationDao {

    @Transaction
    @Query("SELECT * FROM irishrail_station_locations")
    fun selectAll(): Maybe<List<IrishRailStationEntity>>

}

@Dao
interface IrishRailStationLocationDao : BaseDao<IrishRailStationLocationEntity> {

    @Query("SELECT * FROM irishrail_station_locations")
    fun selectAll(): Maybe<List<IrishRailStationLocationEntity>>

    @Query("DELETE FROM irishrail_station_locations")
    fun deleteAll()

}

@Dao
interface IrishRailStationServiceDao : BaseDao<IrishRailStationServiceEntity>
