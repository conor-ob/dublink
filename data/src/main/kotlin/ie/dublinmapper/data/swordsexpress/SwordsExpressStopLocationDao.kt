package ie.dublinmapper.data.swordsexpress

import androidx.room.Dao
import androidx.room.Query
import ie.dublinmapper.data.BaseDao
import io.reactivex.Maybe

@Dao
interface SwordsExpressStopLocationDao : BaseDao<SwordsExpressStopLocationEntity> {

    @Query("SELECT * FROM swordsexpress_stops")
    fun selectAll(): Maybe<List<SwordsExpressStopLocationEntity>>

    @Query("DELETE FROM swordsexpress_stops")
    fun deleteAll()

}
