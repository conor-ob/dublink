package ie.dublinmapper.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import ie.dublinmapper.data.Converters
import ie.dublinmapper.data.aircoach.*
import ie.dublinmapper.data.dublinbus.*
import ie.dublinmapper.data.persister.PersisterDao
import ie.dublinmapper.data.persister.PersisterEntity

@Database(
    version = 1,
    exportSchema = true,
    entities = [
        AircoachStopLocationEntity::class,
        AircoachStopServiceEntity::class,
        DublinBusStopLocationEntity::class,
        DublinBusStopServiceEntity::class,
        PersisterEntity::class
    ]
)
@TypeConverters(Converters::class)
abstract class DublinMapperDatabase : RoomDatabase() {

    abstract fun aircoachStopDao(): AircoachStopDao

    abstract fun aircoachStopLocationDao(): AircoachStopLocationDao

    abstract fun aircoachStopServiceDao(): AircoachStopServiceDao

    abstract fun dublinBusStopDao(): DublinBusStopDao

    abstract fun dublinBusStopLocationDao(): DublinBusStopLocationDao

    abstract fun dublinBusStopServiceDao(): DublinBusStopServiceDao

    abstract fun persisterDao(): PersisterDao

}
