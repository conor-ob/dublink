package ie.dublinmapper.datamodel.dublinbikes

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import ie.dublinmapper.datamodel.BaseDao
import io.reactivex.Maybe

@Dao
interface DublinBikesDockDao {

    @Transaction
    @Query("SELECT * FROM dublinbikes_dock_locations")
    fun selectAll(): Maybe<List<DublinBikesDockEntity>>

}

@Dao
interface DublinBikesDockLocationDao : BaseDao<DublinBikesDockLocationEntity> {

    @Query("SELECT * FROM dublinbikes_dock_locations")
    fun selectAll(): Maybe<List<DublinBikesDockLocationEntity>>

    @Query("DELETE FROM dublinbikes_dock_locations")
    fun deleteAll()

}

@Dao
interface DublinBikesDockServiceDao : BaseDao<DublinBikesDockServiceEntity>
