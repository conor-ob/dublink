package ie.dublinmapper.data.test

import androidx.room.Dao
import androidx.room.Query
import ie.dublinmapper.data.BaseDao
import io.reactivex.Maybe

@Dao
interface LocationDao : BaseDao<LocationEntity> {

    @Query("SELECT * FROM locations")
    fun selectAll(): Maybe<List<LocationEntity>>

}
