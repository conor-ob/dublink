package ie.dublinmapper.datamodel.swordsexpress

import androidx.room.Dao
import androidx.room.Query
import ie.dublinmapper.datamodel.BaseDao
import io.reactivex.Maybe

@Dao
interface SwordsExpressStopLocationDao : BaseDao<SwordsExpressStopLocationEntity> {

    @Query("SELECT * FROM swordsexpress_stop_locations")
    fun selectAll(): Maybe<List<SwordsExpressStopLocationEntity>>

    @Query("DELETE FROM swordsexpress_stop_locations")
    fun deleteAll()

}
