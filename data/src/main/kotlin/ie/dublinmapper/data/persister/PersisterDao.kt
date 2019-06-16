package ie.dublinmapper.data.persister

import androidx.room.Dao
import androidx.room.Query
import ie.dublinmapper.data.BaseDao
import io.reactivex.Maybe

@Dao
interface PersisterDao : BaseDao<PersisterEntity> {

    @Query("SELECT * FROM persister_last_updated WHERE id = :id")
    fun select(id: String): Maybe<PersisterEntity>

    @Query("SELECT * FROM persister_last_updated")
    fun selectAll(): Maybe<List<PersisterEntity>>

}