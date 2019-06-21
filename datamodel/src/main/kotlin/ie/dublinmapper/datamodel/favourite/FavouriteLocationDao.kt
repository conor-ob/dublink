package ie.dublinmapper.datamodel.favourite

import androidx.room.Dao
import androidx.room.Query
import ie.dublinmapper.datamodel.BaseDao
import io.reactivex.Maybe

@Dao
interface FavouriteLocationDao : BaseDao<FavouriteLocationEntity> {

    @Query("SELECT * FROM favourite_locations")
    fun selectAll(): Maybe<List<FavouriteLocationEntity>>

    @Query("DELETE FROM favourite_locations")
    fun deleteAll()

    @Query("DELETE FROM favourite_locations WHERE id = ")
    override fun delete(entity: FavouriteLocationEntity)

}
