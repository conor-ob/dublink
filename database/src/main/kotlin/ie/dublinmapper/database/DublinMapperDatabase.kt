package ie.dublinmapper.database

import android.database.Cursor
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteQuery
import androidx.sqlite.db.SupportSQLiteStatement
import ie.dublinmapper.datamodel.Converters
import ie.dublinmapper.datamodel.aircoach.*
import ie.dublinmapper.datamodel.buseireann.*
import ie.dublinmapper.datamodel.irishrail.*
import ie.dublinmapper.datamodel.dublinbikes.*
import ie.dublinmapper.datamodel.dublinbus.*
import ie.dublinmapper.datamodel.favourite.*
import ie.dublinmapper.datamodel.luas.*
import ie.dublinmapper.datamodel.persister.PersisterDao
import ie.dublinmapper.datamodel.persister.PersisterEntity
import ie.dublinmapper.datamodel.swordsexpress.*
//import timber.log.Timber

@Database(
    version = 1,
    exportSchema = true,
    entities = [
        AircoachStopLocationEntity::class,
        AircoachStopServiceEntity::class,
        BusEireannStopLocationEntity::class,
        BusEireannStopServiceEntity::class,
        IrishRailStationLocationEntity::class,
        IrishRailStationServiceEntity::class,
        DublinBikesDockLocationEntity::class,
        DublinBikesDockServiceEntity::class,
        DublinBusStopLocationEntity::class,
        DublinBusStopServiceEntity::class,
        LuasStopLocationEntity::class,
        LuasStopServiceEntity::class,
        SwordsExpressStopLocationEntity::class,
        SwordsExpressStopServiceEntity::class,
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

    abstract fun irishRailStationDao(): IrishRailStationDao

    abstract fun irishRailStationLocationDao(): IrishRailStationLocationDao

    abstract fun irishRailStationServiceDao(): IrishRailStationServiceDao

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

    abstract fun persisterDao(): PersisterDao

    abstract fun favouriteDao(): FavouriteDao

    abstract fun favouriteLocationDao(): FavouriteLocationDao

    abstract fun favouriteServiceDao(): FavouriteServiceDao

    override fun query(query: SupportSQLiteQuery?): Cursor {
//        Timber.d(query?.sql)
        return super.query(query)
    }

    override fun compileStatement(sql: String): SupportSQLiteStatement {
//        Timber.d(sql)
        return super.compileStatement(sql)
    }

}
