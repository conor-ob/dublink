package ie.dublinmapper.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import ie.dublinmapper.data.Converters
import ie.dublinmapper.data.aircoach.*
import ie.dublinmapper.data.buseireann.*
import ie.dublinmapper.data.dart.*
import ie.dublinmapper.data.dublinbus.*
import ie.dublinmapper.data.persister.PersisterDao
import ie.dublinmapper.data.persister.PersisterEntity
import ie.dublinmapper.data.test.*

@Database(
    version = 1,
    exportSchema = true,
    entities = [
        AircoachStopLocationEntity::class,
        AircoachStopServiceEntity::class,
        BusEireannStopLocationEntity::class,
        BusEireannStopServiceEntity::class,
        DartStationLocationEntity::class,
        DartStationServiceEntity::class,
        DublinBusStopLocationEntity::class,
        DublinBusStopServiceEntity::class,
        LocationEntity::class,
        ServiceEntity::class,
        PersisterEntity::class
    ]
)
@TypeConverters(Converters::class)
abstract class DublinMapperDatabase : RoomDatabase() {

    abstract fun aircoachStopDao(): AircoachStopDao

    abstract fun aircoachStopLocationDao(): AircoachStopLocationDao

    abstract fun aircoachStopServiceDao(): AircoachStopServiceDao

    abstract fun busEireannStopDao(): BusEireannStopDao

    abstract fun busEireannStopLocationDao(): BusEireannStopLocationDao

    abstract fun busEireannStopServiceDao(): BusEireannStopServiceDao

    abstract fun dartStationDao(): DartStationDao

    abstract fun dartStationLocationDao(): DartStationLocationDao

    abstract fun dartStationServiceDao(): DartStationServiceDao

    abstract fun dublinBusStopDao(): DublinBusStopDao

    abstract fun dublinBusStopLocationDao(): DublinBusStopLocationDao

    abstract fun dublinBusStopServiceDao(): DublinBusStopServiceDao

    abstract fun locationDao(): LocationDao

    abstract fun serviceDao(): ServiceDao

    abstract fun serviceLocationDao(): ServiceLocationDao

    abstract fun persisterDao(): PersisterDao

}
