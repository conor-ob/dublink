package ie.dublinmapper.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import ie.dublinmapper.aircoach.AircoachStopCacheResourceImpl
import ie.dublinmapper.buseireann.BusEireannStopCacheResourceImpl
import ie.dublinmapper.data.TxRunner
import ie.dublinmapper.data.aircoach.AircoachStopCacheResource
import ie.dublinmapper.data.buseireann.BusEireannStopCacheResource
import ie.dublinmapper.data.dublinbus.DublinBusStopCacheResource
import ie.dublinmapper.data.persister.PersisterDao
import ie.dublinmapper.database.DatabaseTxRunner
import ie.dublinmapper.database.DublinMapperDatabase
import ie.dublinmapper.dublinbus.DublinBusStopCacheResourceImpl
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
    fun dublinBusStopCacheResource(database: DublinMapperDatabase, txRunner: TxRunner): DublinBusStopCacheResource {
        val dublinBusStopLocationDao = database.dublinBusStopLocationDao()
        val dublinBusStopServiceDao = database.dublinBusStopServiceDao()
        val dublinBusStopDao = database.dublinBusStopDao()
        return DublinBusStopCacheResourceImpl(dublinBusStopLocationDao, dublinBusStopServiceDao, dublinBusStopDao, txRunner)
    }

    @Provides
    @Singleton
    fun persisterDao(database: DublinMapperDatabase): PersisterDao {
        return database.persisterDao()
    }

}
