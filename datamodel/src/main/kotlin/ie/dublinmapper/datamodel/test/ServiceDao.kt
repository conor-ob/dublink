package ie.dublinmapper.datamodel.test

import androidx.room.Dao
import ie.dublinmapper.datamodel.BaseDao

@Dao
interface ServiceDao : BaseDao<ServiceEntity>
