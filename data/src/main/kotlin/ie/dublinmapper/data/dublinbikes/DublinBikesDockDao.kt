package ie.dublinmapper.data.dublinbikes

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import io.reactivex.Maybe

@Dao
interface DublinBikesDockDao {

    @Transaction
    @Query("SELECT * FROM dublinbikes_docks")
    fun selectAll(): Maybe<List<DublinBikesDockEntity>>

}
