package ie.dublinmapper.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import ie.dublinmapper.database.aircoach.AircoachStopCacheResourceImpl
import ie.dublinmapper.database.buseireann.BusEireannStopCacheResourceImpl
import ie.dublinmapper.database.dart.DartStationCacheResourceImpl
import ie.dublinmapper.datamodel.TxRunner
import ie.dublinmapper.datamodel.aircoach.AircoachStopCacheResource
import ie.dublinmapper.datamodel.buseireann.BusEireannStopCacheResource
import ie.dublinmapper.datamodel.dart.DartStationCacheResource
import ie.dublinmapper.datamodel.dublinbikes.DublinBikesDockCacheResource
import ie.dublinmapper.datamodel.dublinbus.DublinBusStopCacheResource
import ie.dublinmapper.datamodel.luas.LuasStopCacheResource
import ie.dublinmapper.datamodel.persister.PersisterDao
import ie.dublinmapper.datamodel.swordsexpress.SwordsExpressStopCacheResource
import ie.dublinmapper.database.DatabaseTxRunner
import ie.dublinmapper.database.DublinMapperDatabase
import ie.dublinmapper.database.dublinbikes.DublinBikesDockCacheResourceImpl
import ie.dublinmapper.database.dublinbus.DublinBusStopCacheResourceImpl
import ie.dublinmapper.database.luas.LuasStopCacheResourceImpl
import ie.dublinmapper.database.swordsexpress.SwordsExpressStopCacheResourceImpl
import ie.dublinmapper.datamodel.favourite.FavouriteServiceLocationCacheResource
import ie.dublinmapper.favourite.FavouriteServiceLocationCacheResourceImpl
import ie.dublinmapper.util.StringProvider
import javax.inject.Singleton

@Module
class DatabaseModule {

    @Provides
    @Singleton
    fun database(context: Context, stringProvider: StringProvider): DublinMapperDatabase = Room
        .databaseBuilder(context, DublinMapperDatabase::class.java, stringProvider.databaseName())
        .fallbackToDestructiveMigration() //TODO temporary
//        .allowMainThreadQueries() //TODO temporary
//        .addCallback(object : RoomDatabase.Callback() { //TODO temporary
//
//            override fun onCreate(db: SupportSQLiteDatabase) {
//                super.onCreate(db)
//                val database = database(context, stringProvider)
//                database.favouriteLocationDao().insertAll(
//                    listOf(
//                        FavouriteIrishRailStationLocationEntity(serviceId = "BROCK", name = "Blackrock DART", service = Service.IRISH_RAIL),
//                        FavouriteIrishRailStationLocationEntity(serviceId = "PERSE", name = "Pearse DART", service = Service.IRISH_RAIL)
//                    )
//                )
//                database.favouriteServiceDao().insertAll(
//                    listOf(
//                        FavouriteIrishRailStationServiceEntity(locationId = "BROCK", operator = Operator.COMMUTER, route = "Commuter"),
//                        FavouriteIrishRailStationServiceEntity(locationId = "BROCK", operator = Operator.DART, route = "Dart"),
//                        FavouriteIrishRailStationServiceEntity(locationId = "BROCK", operator = Operator.INTERCITY, route = "Intercity"),
//                        FavouriteIrishRailStationServiceEntity(locationId = "PERSE", operator = Operator.COMMUTER, route = "Commuter"),
//                        FavouriteIrishRailStationServiceEntity(locationId = "PERSE", operator = Operator.DART, route = "Dart"),
//                        FavouriteIrishRailStationServiceEntity(locationId = "PERSE", operator = Operator.INTERCITY, route = "Intercity")
//                    )
//                )
//            }
//
//        })
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
    fun favouriteCacheResource(database: DublinMapperDatabase, txRunner: TxRunner): FavouriteServiceLocationCacheResource {
        val favouriteLocationDao = database.favouriteLocationDao()
        val favouriteServiceDao = database.favouriteServiceDao()
        val favouriteDao = database.favouriteDao()
        return FavouriteServiceLocationCacheResourceImpl(favouriteLocationDao, favouriteServiceDao, favouriteDao, txRunner)
    }

    @Provides
    @Singleton
    fun persisterDao(database: DublinMapperDatabase): PersisterDao {
        return database.persisterDao()
    }

}
