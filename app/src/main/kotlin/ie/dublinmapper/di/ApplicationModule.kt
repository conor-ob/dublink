package ie.dublinmapper.di

import android.content.Context
import android.content.res.Resources
import com.facebook.stetho.okhttp3.StethoInterceptor
import dagger.Module
import dagger.Provides
import ie.dublinmapper.DublinMapperApplication
import ie.dublinmapper.core.mapping.FavouritesDomainToUiMapper
import ie.dublinmapper.core.mapping.LiveDataDomainToUiMapper
import ie.dublinmapper.core.mapping.NearbyDomainToUiMapper
import ie.dublinmapper.core.mapping.SearchDomainToUiMapper
import ie.dublinmapper.location.LocationProvider
import ie.dublinmapper.nearby.location.GpsLocationProvider
import ie.dublinmapper.permission.AndroidPermissionsChecker
import ie.dublinmapper.permission.PermissionChecker
import ie.dublinmapper.repository.aircoach.stops.AircoachStopEntityToDomainMapper
import ie.dublinmapper.repository.aircoach.stops.AircoachStopJsonToEntityMapper
import ie.dublinmapper.repository.buseireann.stops.BusEireannStopEntityToDomainMapper
import ie.dublinmapper.repository.buseireann.stops.BusEireannStopJsonToEntityMapper
import ie.dublinmapper.repository.dublinbikes.docks.DublinBikesDockJsonToEntityMapper
import ie.dublinmapper.repository.dublinbikes.docks.DublinBikesDocksEntityToDomainMapper
import ie.dublinmapper.repository.dublinbus.stops.DublinBusStopEntityToDomainMapper
import ie.dublinmapper.repository.dublinbus.stops.DublinBusStopJsonToEntityMapper
import ie.dublinmapper.repository.favourite.FavouriteDomainToEntityMapper
import ie.dublinmapper.repository.favourite.FavouriteEntityToDomainMapper
import ie.dublinmapper.repository.irishrail.stations.IrishRailStationJsonToEntityMapper
import ie.dublinmapper.repository.irishrail.stations.IrishRailStationEntityToDomainMapper
import ie.dublinmapper.repository.luas.stops.LuasStopEntityToDomainMapper
import ie.dublinmapper.repository.luas.stops.LuasStopJsonToEntityMapper
import ie.dublinmapper.settings.DefaultEnabledServiceManager
import ie.dublinmapper.settings.DefaultPreferenceStore
import ie.dublinmapper.settings.R
import ie.dublinmapper.util.*
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import io.rtpi.api.Service
import io.rtpi.client.RtpiClient
import ma.glasnost.orika.MapperFacade
import ma.glasnost.orika.impl.DefaultMapperFactory
import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit

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
    fun okHttpClient(): OkHttpClient =
        OkHttpClient.Builder()
            .addNetworkInterceptor(NetworkLoggingInterceptor())
            .addNetworkInterceptor(StethoInterceptor())
            .retryOnConnectionFailure(true)
            .connectTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .build()

    @Provides
    fun client(okHttpClient: OkHttpClient): RtpiClient = RtpiClient(okHttpClient)

    @Provides
    fun mapperFacade(
        stringProvider: StringProvider
    ): MapperFacade {
        val mapperFactory = DefaultMapperFactory.Builder().useBuiltinConverters(false).build()

        mapperFactory.converterFactory.apply {
            registerConverter(AircoachStopJsonToEntityMapper)
            registerConverter(AircoachStopEntityToDomainMapper)

            registerConverter(BusEireannStopJsonToEntityMapper)
            registerConverter(BusEireannStopEntityToDomainMapper)

            registerConverter(IrishRailStationJsonToEntityMapper)
            registerConverter(IrishRailStationEntityToDomainMapper)

            registerConverter(DublinBikesDockJsonToEntityMapper)
            registerConverter(DublinBikesDocksEntityToDomainMapper)

            registerConverter(DublinBusStopJsonToEntityMapper)
            registerConverter(DublinBusStopEntityToDomainMapper)

            registerConverter(LuasStopJsonToEntityMapper)
            registerConverter(LuasStopEntityToDomainMapper)

            registerConverter(FavouritesDomainToUiMapper(stringProvider))
            registerConverter(NearbyDomainToUiMapper(stringProvider))
            registerConverter(LiveDataDomainToUiMapper(stringProvider))
            registerConverter(SearchDomainToUiMapper)

            registerConverter(FavouriteEntityToDomainMapper)
            registerConverter(FavouriteDomainToEntityMapper)
        }

        return mapperFactory.mapperFacade
    }

    @Provides
    fun preferenceStore(context: Context): PreferenceStore = DefaultPreferenceStore(context)

    @Provides
    fun enabledServiceManager(
        context: Context,
        preferenceStore: PreferenceStore
    ): EnabledServiceManager {
        val serviceToEnabledServicePreferenceKey = mapOf(
            Service.AIRCOACH to context.getString(R.string.preference_enabled_service_aircoach),
            Service.BUS_EIREANN to context.getString(R.string.preference_enabled_service_bus_eireann),
            Service.DUBLIN_BIKES to context.getString(R.string.preference_enabled_service_dublin_bikes),
            Service.DUBLIN_BUS to context.getString(R.string.preference_enabled_service_dublin_bus),
            Service.IRISH_RAIL to context.getString(R.string.preference_enabled_service_irish_rail),
            Service.LUAS to context.getString(R.string.preference_enabled_service_luas)
        )
        return DefaultEnabledServiceManager(preferenceStore, serviceToEnabledServicePreferenceKey)
    }

    @Provides
    fun locationProvider(context: Context): LocationProvider = GpsLocationProvider(context)

    @Provides
    fun permissionChecker(context: Context): PermissionChecker = AndroidPermissionsChecker(context)

}
