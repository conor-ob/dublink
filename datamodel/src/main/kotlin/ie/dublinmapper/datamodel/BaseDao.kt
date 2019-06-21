package ie.dublinmapper.datamodel

import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Update
import io.reactivex.Maybe

interface BaseDao<E> {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(entity: E): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(entities: List<E>): List<Long>

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun update(entity: E)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun updateAll(entities: List<E>)

    @Delete
    fun delete(entity: E)

}
