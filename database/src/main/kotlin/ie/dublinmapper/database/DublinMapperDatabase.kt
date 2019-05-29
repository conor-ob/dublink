package ie.dublinmapper.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import ie.dublinmapper.data.Converters
import ie.dublinmapper.data.aircoach.*
import ie.dublinmapper.data.buseireann.*
import ie.dublinmapper.data.dart.*
import ie.dublinmapper.data.dublinbikes.*
import ie.dublinmapper.data.dublinbus.*
import ie.dublinmapper.data.favourite.*
import ie.dublinmapper.data.luas.*
import ie.dublinmapper.data.persister.PersisterDao
import ie.dublinmapper.data.persister.PersisterEntity
import ie.dublinmapper.data.swordsexpress.*
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
        DublinBikesDockLocationEntity::class,
        DublinBikesDockServiceEntity::class,
        DublinBusStopLocationEntity::class,
        DublinBusStopServiceEntity::class,
        LuasStopLocationEntity::class,
        LuasStopServiceEntity::class,
        SwordsExpressStopLocationEntity::class,
        SwordsExpressStopServiceEntity::class,
        LocationEntity::class,
        ServiceEntity::class,
        PersisterEntity::class,
        FavouriteLocationEntity::class,
        FavouriteServiceEntity::class
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

    abstract fun dublinBikesDockDao(): DublinBikesDockDao

    abstract fun dublinBikesDockLocationDao(): DublinBikesDockLocationDao

    abstract fun dublinBikesDockServiceDao(): DublinBikesDockServiceDao

    abstract fun dublinBusStopDao(): DublinBusStopDao

    abstract fun dublinBusStopLocationDao(): DublinBusStopLocationDao

    abstract fun dublinBusStopServiceDao(): DublinBusStopServiceDao

    abstract fun luasStopDao(): LuasStopDao

    abstract fun luasStopLocationDao(): LuasStopLocationDao

    abstract fun luasStopServiceDao(): LuasStopServiceDao

    abstract fun swordsExpressStopDao(): SwordsExpressStopDao

    abstract fun swordsExpressStopLocationDao(): SwordsExpressStopLocationDao

    abstract fun swordsExpressStopServiceDao(): SwordsExpressStopServiceDao

    abstract fun locationDao(): LocationDao

    abstract fun serviceDao(): ServiceDao

    abstract fun serviceLocationDao(): ServiceLocationDao

    abstract fun persisterDao(): PersisterDao

    abstract fun favouriteDao(): FavouriteDao

    abstract fun favouriteLocationDao(): FavouriteLocationDao

    abstract fun favouriteServiceDao(): FavouriteServiceDao

}
