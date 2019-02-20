package ie.dublinmapper.data.test

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import io.reactivex.Maybe

@Dao
interface ServiceLocationDao {

    @Transaction
    @Query("SELECT * FROM locations l JOIN services s on l.id = s.location_id WHERE s.operator = :operator")
    fun selectAll(operator: String): Maybe<List<ServiceLocationEntity>>

}
