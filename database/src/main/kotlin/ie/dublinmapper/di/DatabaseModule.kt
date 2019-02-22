package ie.dublinmapper.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import ie.dublinmapper.aircoach.AircoachStopCacheResourceImpl
import ie.dublinmapper.buseireann.BusEireannStopCacheResourceImpl
import ie.dublinmapper.dart.DartStationCacheResourceImpl
import ie.dublinmapper.data.TxRunner
import ie.dublinmapper.data.aircoach.AircoachStopCacheResource
import ie.dublinmapper.data.buseireann.BusEireannStopCacheResource
import ie.dublinmapper.data.dart.DartStationCacheResource
import ie.dublinmapper.data.dublinbikes.DublinBikesDockCacheResource
import ie.dublinmapper.data.dublinbus.DublinBusStopCacheResource
import ie.dublinmapper.data.luas.LuasStopCacheResource
import ie.dublinmapper.data.persister.PersisterDao
import ie.dublinmapper.data.swordsexpress.SwordsExpressStopCacheResource
import ie.dublinmapper.database.DatabaseTxRunner
import ie.dublinmapper.database.DublinMapperDatabase
import ie.dublinmapper.dublinbikes.DublinBikesDockCacheResourceImpl
import ie.dublinmapper.dublinbus.DublinBusStopCacheResourceImpl
import ie.dublinmapper.luas.LuasStopCacheResourceImpl
import ie.dublinmapper.swordsexpress.SwordsExpressStopCacheResourceImpl
import ie.dublinmapper.util.StringProvider
import javax.inject.Singleton

@Module
class DatabaseModule {

    @Provides
    @Singleton
    fun database(context: Context, stringProvider: StringProvider): DublinMapperDatabase = Room
        .databaseBuilder(context, DublinMapperDatabase::class.java, stringProvider.databaseName())
        .fallbackToDestructiveMigration()
        .build()

    @Provides
    @Singleton
    fun txRunner(database: DublinMapperDatabase): TxRunner {
        return DatabaseTxRunner(database)
    }

    @Provides
    @Singleton
    fun aircoachStopCacheResource(database: DublinMapperDatabase, txRunner: TxRunner): AircoachStopCacheResource {
        val aircoachStopLocationDao = database.aircoachStopLocationDao()
        val aircoachStopServiceDao = database.aircoachStopServiceDao()
        val aircoachStopDao = database.aircoachStopDao()
        return AircoachStopCacheResourceImpl(aircoachStopLocationDao, aircoachStopServiceDao, aircoachStopDao, txRunner)
    }

    @Provides
    @Singleton
    fun busEireannStopCacheResource(database: DublinMapperDatabase, txRunner: TxRunner): BusEireannStopCacheResource {
        val busEireannStopLocationDao = database.busEireannStopLocationDao()
        val busEireannStopServiceDao = database.busEireannStopServiceDao()
        val busEireannStopDao = database.busEireannStopDao()
        return BusEireannStopCacheResourceImpl(busEireannStopLocationDao, busEireannStopServiceDao, busEireannStopDao, txRunner)
    }

    @Provides
    @Singleton
    fun dartStationCacheResource(database: DublinMapperDatabase, txRunner: TxRunner): DartStationCacheResource {
        val dartStationLocationDao = database.dartStationLocationDao()
        val dartStationServiceDao = database.dartStationServiceDao()
        val dartStationDao = database.dartStationDao()
        return DartStationCacheResourceImpl(dartStationLocationDao, dartStationServiceDao, dartStationDao, txRunner)
    }

    @Provides
    @Singleton
    fun dublinBikesDockCacheResource(database: DublinMapperDatabase, txRunner: TxRunner): DublinBikesDockCacheResource {
        val dublinBikesDockLocationDao = database.dublinBikesDockLocationDao()
        val dublinBikesDockServiceDao = database.dublinBikesDockServiceDao()
        val dublinBikesDockDao = database.dublinBikesDockDao()
        return DublinBikesDockCacheResourceImpl(dublinBikesDockLocationDao, dublinBikesDockServiceDao, dublinBikesDockDao, txRunner)
    }

    @Provides
    @Singleton
    fun dublinBusStopCacheResource(database: DublinMapperDatabase, txRunner: TxRunner): DublinBusStopCacheResource {
        val dublinBusStopLocationDao = database.dublinBusStopLocationDao()
        val dublinBusStopServiceDao = database.dublinBusStopServiceDao()
        val dublinBusStopDao = database.dublinBusStopDao()
        return DublinBusStopCacheResourceImpl(dublinBusStopLocationDao, dublinBusStopServiceDao, dublinBusStopDao, txRunner)
    }

    @Provides
    @Singleton
    fun luasStopCacheResource(database: DublinMapperDatabase, txRunner: TxRunner): LuasStopCacheResource {
        val luasStopLocationDao = database.luasStopLocationDao()
        val luasStopServiceDao = database.luasStopServiceDao()
        val luasStopDao = database.luasStopDao()
        return LuasStopCacheResourceImpl(luasStopLocationDao, luasStopServiceDao, luasStopDao, txRunner)
    }

    @Provides
    @Singleton
    fun swordsExpressStopCacheResource(database: DublinMapperDatabase, txRunner: TxRunner): SwordsExpressStopCacheResource {
        val swordsExpressStopLocationDao = database.swordsExpressStopLocationDao()
        val swordsExpressStopServiceDao = database.swordsExpressStopServiceDao()
        val swordsExpressStopDao = database.swordsExpressStopDao()
        return SwordsExpressStopCacheResourceImpl(swordsExpressStopLocationDao, swordsExpressStopServiceDao, swordsExpressStopDao, txRunner)
    }

    @Provides
    @Singleton
    fun persisterDao(database: DublinMapperDatabase): PersisterDao {
        return database.persisterDao()
    }

}
