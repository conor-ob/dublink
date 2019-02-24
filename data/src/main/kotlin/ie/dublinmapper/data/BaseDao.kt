package ie.dublinmapper.data

import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Update

interface BaseDao<E> {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(entity: E)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(entities: List<E>)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun update(entity: E)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun updateAll(entities: List<E>)

}
