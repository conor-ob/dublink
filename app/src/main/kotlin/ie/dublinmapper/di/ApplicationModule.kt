package ie.dublinmapper.di

import android.content.Context
import android.content.res.Resources
import dagger.Module
import dagger.Provides
import ie.dublinmapper.DublinMapperApplication
import ie.dublinmapper.core.mapping.FavouritesDomainToUiMapper
import ie.dublinmapper.core.mapping.LiveDataDomainToUiMapper
import ie.dublinmapper.core.mapping.SearchDomainToUiMapper
import ie.dublinmapper.repository.aircoach.livedata.AircoachLiveDataJsonToDomainMapper
import ie.dublinmapper.repository.aircoach.stops.AircoachStopEntityToDomainMapper
import ie.dublinmapper.repository.aircoach.stops.AircoachStopJsonToEntityMapper
import ie.dublinmapper.repository.buseireann.livedata.BusEireannLiveDataJsonToDomainMapper
import ie.dublinmapper.repository.buseireann.stops.BusEireannStopEntityToDomainMapper
import ie.dublinmapper.repository.buseireann.stops.BusEireannStopJsonToEntityMapper
import ie.dublinmapper.repository.dublinbikes.docks.DublinBikesDockJsonToEntityMapper
import ie.dublinmapper.repository.dublinbikes.docks.DublinBikesDocksEntityToDomainMapper
import ie.dublinmapper.repository.dublinbikes.livedata.DublinBikesLiveDataJsonToDomainMapper
import ie.dublinmapper.repository.dublinbus.livedata.DublinBusLiveDataJsonToDomainMapper
import ie.dublinmapper.repository.dublinbus.stops.DublinBusStopEntityToDomainMapper
import ie.dublinmapper.repository.dublinbus.stops.DublinBusStopJsonToEntityMapper
import ie.dublinmapper.repository.favourite.FavouriteDomainToEntityMapper
import ie.dublinmapper.repository.favourite.FavouriteEntityToDomainMapper
import ie.dublinmapper.repository.irishrail.livedata.IrishRailLiveDataJsonToDomainMapper
import ie.dublinmapper.repository.irishrail.stations.IrishRailJsonToEntityMapper
import ie.dublinmapper.repository.irishrail.stations.IrishRailStationEntityToDomainMapper
import ie.dublinmapper.repository.luas.livedata.LuasLiveDataJsonToDomainMapper
import ie.dublinmapper.repository.luas.stops.LuasStopEntityToDomainMapper
import ie.dublinmapper.repository.luas.stops.LuasStopJsonToEntityMapper
import ie.dublinmapper.util.AndroidAssetSslContextProvider
import ie.dublinmapper.util.AndroidResourceStringProvider
import ie.dublinmapper.util.InternetManager
import ie.dublinmapper.util.InternetManagerImpl
import ie.dublinmapper.util.RxScheduler
import ie.dublinmapper.util.SslContextProvider
import ie.dublinmapper.util.StringProvider
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import ma.glasnost.orika.MapperFacade
import ma.glasnost.orika.impl.DefaultMapperFactory
import javax.inject.Singleton

@Module
class ApplicationModule {

    @Provides
    fun context(application: DublinMapperApplication): Context = application.applicationContext

    @Provides
    fun resources(context: Context): Resources {
        return context.resources
    }

    @Provides
    fun stringProvider(
        context: Context,
        resources: Resources
    ): StringProvider {
        return AndroidResourceStringProvider(context, resources)
    }

    @Provides
    fun sslContextProvider(
        context: Context
    ): SslContextProvider {
        return AndroidAssetSslContextProvider(context) //TODO inject in service module
    }

//    @Provides
//    fun mapMarkerManager(context: Context): GoogleMapController {
//        return GoogleMapController(context)
//    }

    @Provides
    fun schedulers(): RxScheduler {
        return RxScheduler(
            io = Schedulers.io(),
            ui = AndroidSchedulers.mainThread()
        )
    }

    @Provides
    fun internetManager(context: Context): InternetManager {
        return InternetManagerImpl(context)
    }

    @Provides
    fun mapperFacade(
        stringProvider: StringProvider
    ): MapperFacade {
        val mapperFactory = DefaultMapperFactory.Builder().useBuiltinConverters(false).build()

        mapperFactory.converterFactory.apply {
            registerConverter(AircoachStopJsonToEntityMapper)
            registerConverter(AircoachStopEntityToDomainMapper)
            registerConverter(AircoachLiveDataJsonToDomainMapper)

            registerConverter(BusEireannStopJsonToEntityMapper)
            registerConverter(BusEireannStopEntityToDomainMapper)
            registerConverter(BusEireannLiveDataJsonToDomainMapper)

            registerConverter(IrishRailJsonToEntityMapper)
            registerConverter(IrishRailStationEntityToDomainMapper)
            registerConverter(IrishRailLiveDataJsonToDomainMapper)

            registerConverter(DublinBikesDockJsonToEntityMapper)
            registerConverter(DublinBikesDocksEntityToDomainMapper)
            registerConverter(DublinBikesLiveDataJsonToDomainMapper)

            registerConverter(DublinBusStopJsonToEntityMapper)
            registerConverter(DublinBusStopEntityToDomainMapper)
            registerConverter(DublinBusLiveDataJsonToDomainMapper)

            registerConverter(LuasStopJsonToEntityMapper)
            registerConverter(LuasStopEntityToDomainMapper)
            registerConverter(LuasLiveDataJsonToDomainMapper)

            registerConverter(FavouritesDomainToUiMapper(stringProvider))
            registerConverter(LiveDataDomainToUiMapper(stringProvider))
            registerConverter(SearchDomainToUiMapper)

            registerConverter(FavouriteEntityToDomainMapper)
            registerConverter(FavouriteDomainToEntityMapper)
        }

        return mapperFactory.mapperFacade
    }

}
