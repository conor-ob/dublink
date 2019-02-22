package ie.dublinmapper.data.swordsexpress

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import io.reactivex.Maybe

@Dao
interface SwordsExpressStopDao {

    @Transaction
    @Query("SELECT * FROM swordsexpress_stops")
    fun selectAll(): Maybe<List<SwordsExpressStopEntity>>

}
