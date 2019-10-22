package ie.dublinmapper.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import ie.dublinmapper.database.aircoach.AircoachStopLocalResourceImpl
import ie.dublinmapper.database.buseireann.BusEireannStopLocalResourceImpl
import ie.dublinmapper.database.irishrail.IrishRailStationLocalResourceImpl
import ie.dublinmapper.datamodel.TxRunner
import ie.dublinmapper.datamodel.aircoach.AircoachStopLocalResource
import ie.dublinmapper.datamodel.buseireann.BusEireannStopLocalResource
import ie.dublinmapper.datamodel.irishrail.IrishRailStationLocalResource
import ie.dublinmapper.datamodel.dublinbikes.DublinBikesDockLocalResource
import ie.dublinmapper.datamodel.dublinbus.DublinBusStopLocalResource
import ie.dublinmapper.datamodel.luas.LuasStopLocalResource
import ie.dublinmapper.datamodel.persister.PersisterDao
import ie.dublinmapper.database.DatabaseTxRunner
import ie.dublinmapper.database.DublinMapperDatabase
import ie.dublinmapper.database.dublinbikes.DublinBikesDockLocalResourceImpl
import ie.dublinmapper.database.dublinbus.DublinBusStopLocalResourceImpl
import ie.dublinmapper.database.luas.LuasStopLocalResourceImpl
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
    fun aircoachStopCacheResource(database: DublinMapperDatabase, txRunner: TxRunner): AircoachStopLocalResource {
        val aircoachStopLocationDao = database.aircoachStopLocationDao()
        val aircoachStopServiceDao = database.aircoachStopServiceDao()
        val aircoachStopDao = database.aircoachStopDao()
        val favouriteDao = database.favouriteDao()
        return AircoachStopLocalResourceImpl(aircoachStopLocationDao, aircoachStopServiceDao, aircoachStopDao, favouriteDao, txRunner)
    }

    @Provides
    @Singleton
    fun busEireannStopCacheResource(database: DublinMapperDatabase, txRunner: TxRunner): BusEireannStopLocalResource {
        val busEireannStopLocationDao = database.busEireannStopLocationDao()
        val busEireannStopServiceDao = database.busEireannStopServiceDao()
        val busEireannStopDao = database.busEireannStopDao()
        val favouriteDao = database.favouriteDao()
        return BusEireannStopLocalResourceImpl(busEireannStopLocationDao, busEireannStopServiceDao, busEireannStopDao, favouriteDao, txRunner)
    }

    @Provides
    @Singleton
    fun irishRailStationCacheResource(database: DublinMapperDatabase, txRunner: TxRunner): IrishRailStationLocalResource {
        val irishRailStationLocationDao = database.irishRailStationLocationDao()
        val irishRailStationServiceDao = database.irishRailStationServiceDao()
        val irishRailStationDao = database.irishRailStationDao()
        val favouriteDao = database.favouriteDao()
        return IrishRailStationLocalResourceImpl(irishRailStationLocationDao, irishRailStationServiceDao, irishRailStationDao, favouriteDao, txRunner)
    }

    @Provides
    @Singleton
    fun dublinBikesDockCacheResource(database: DublinMapperDatabase, txRunner: TxRunner): DublinBikesDockLocalResource {
        val dublinBikesDockLocationDao = database.dublinBikesDockLocationDao()
        val dublinBikesDockServiceDao = database.dublinBikesDockServiceDao()
        val dublinBikesDockDao = database.dublinBikesDockDao()
        val favouriteDao = database.favouriteDao()
        return DublinBikesDockLocalResourceImpl(dublinBikesDockLocationDao, dublinBikesDockServiceDao, dublinBikesDockDao, favouriteDao, txRunner)
    }

    @Provides
    @Singleton
    fun dublinBusStopCacheResource(database: DublinMapperDatabase, txRunner: TxRunner): DublinBusStopLocalResource {
        val dublinBusStopLocationDao = database.dublinBusStopLocationDao()
        val dublinBusStopServiceDao = database.dublinBusStopServiceDao()
        val dublinBusStopDao = database.dublinBusStopDao()
        val favouriteDao = database.favouriteDao()
        return DublinBusStopLocalResourceImpl(dublinBusStopLocationDao, dublinBusStopServiceDao, dublinBusStopDao, favouriteDao, txRunner)
    }

    @Provides
    @Singleton
    fun luasStopCacheResource(database: DublinMapperDatabase, txRunner: TxRunner): LuasStopLocalResource {
        val luasStopLocationDao = database.luasStopLocationDao()
        val luasStopServiceDao = database.luasStopServiceDao()
        val luasStopDao = database.luasStopDao()
        val favouriteDao = database.favouriteDao()
        return LuasStopLocalResourceImpl(luasStopLocationDao, luasStopServiceDao, luasStopDao, favouriteDao, txRunner)
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
