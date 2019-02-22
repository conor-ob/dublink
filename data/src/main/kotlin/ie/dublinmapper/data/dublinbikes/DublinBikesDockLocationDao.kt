package ie.dublinmapper.data.dublinbikes

import androidx.room.Dao
import androidx.room.Query
import ie.dublinmapper.data.BaseDao
import io.reactivex.Maybe

@Dao
interface DublinBikesDockLocationDao : BaseDao<DublinBikesDockLocationEntity> {

    @Query("SELECT * FROM dublinbikes_docks")
    fun selectAll(): Maybe<List<DublinBikesDockLocationEntity>>

    @Query("DELETE FROM dublinbikes_docks")
    fun deleteAll()

}
