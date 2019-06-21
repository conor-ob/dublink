package ie.dublinmapper.datamodel.luas

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import ie.dublinmapper.datamodel.BaseDao
import io.reactivex.Maybe

@Dao
interface LuasStopDao {

    @Transaction
    @Query("SELECT * FROM luas_stop_locations")
    fun selectAll(): Maybe<List<LuasStopEntity>>

}

@Dao
interface LuasStopLocationDao : BaseDao<LuasStopLocationEntity> {

    @Query("SELECT * FROM luas_stop_locations")
    fun selectAll(): Maybe<List<LuasStopLocationEntity>>

    @Query("DELETE FROM luas_stop_locations")
    fun deleteAll()

}

@Dao
interface LuasStopServiceDao : BaseDao<LuasStopServiceEntity>
