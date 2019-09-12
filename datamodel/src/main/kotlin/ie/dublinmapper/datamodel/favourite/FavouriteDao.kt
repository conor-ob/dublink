package ie.dublinmapper.datamodel.favourite

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import ie.dublinmapper.datamodel.BaseDao
import ie.dublinmapper.util.Service
import io.reactivex.Maybe

@Dao
interface FavouriteDao {

    @Transaction
    @Query("SELECT * FROM favourite_locations")
    fun selectAll(): Maybe<List<FavouriteEntity>>

    @Query("SELECT * FROM favourite_locations WHERE service = :service")
    fun selectAll(service: Service): Maybe<List<FavouriteEntity>>

    @Query("SELECT * FROM favourite_locations WHERE id = :id")
    fun select(id: FavouriteKey): Maybe<FavouriteEntity>

}

@Dao
interface FavouriteLocationDao : BaseDao<FavouriteLocationEntity> {

    @Query("SELECT * FROM favourite_locations")
    fun selectAll(): Maybe<List<FavouriteLocationEntity>>

    @Query("DELETE FROM favourite_locations")
    fun deleteAll()

    @Query("SELECT COUNT(*) FROM favourite_locations")
    fun count(): Maybe<Long>

}

@Dao
interface FavouriteServiceDao : BaseDao<FavouriteServiceEntity>
