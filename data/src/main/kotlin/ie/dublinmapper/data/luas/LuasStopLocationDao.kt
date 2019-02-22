package ie.dublinmapper.data.luas

import androidx.room.Dao
import androidx.room.Query
import ie.dublinmapper.data.BaseDao
import io.reactivex.Maybe

@Dao
interface LuasStopLocationDao : BaseDao<LuasStopLocationEntity> {

    @Query("SELECT * FROM luas_stops")
    fun selectAll(): Maybe<List<LuasStopLocationEntity>>

    @Query("DELETE FROM luas_stops")
    fun deleteAll()

}
