package ie.dublinmapper.inject

import android.content.Context
import android.content.res.Resources
import com.facebook.stetho.okhttp3.StethoInterceptor
import dagger.Module
import dagger.Provides
import ie.dublinmapper.BuildConfig
import ie.dublinmapper.DublinMapperApplication
import ie.dublinmapper.core.*
import ie.dublinmapper.core.mapping.FavouritesDomainToUiMapper
import ie.dublinmapper.core.mapping.LiveDataDomainToUiMapper
import ie.dublinmapper.core.mapping.NearbyDomainToUiMapper
import ie.dublinmapper.core.mapping.SearchDomainToUiMapper
import ie.dublinmapper.database.DatabaseModule
import ie.dublinmapper.internet.WifiManager
import ie.dublinmapper.setup.*
import ie.dublinmapper.location.GpsLocationProvider
import ie.dublinmapper.logging.NetworkLoggingInterceptor
import ie.dublinmapper.permission.UserPermissionsChecker
import ie.dublinmapper.repository.di.RepositoryModule
import ie.dublinmapper.resource.StringResourceProvider
import ie.dublinmapper.settings.DefaultEnabledServiceManager
import ie.dublinmapper.settings.DefaultPreferenceStore
import ie.dublinmapper.settings.R
import ie.dublinmapper.settings.ThemeRepository
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import io.rtpi.api.Service
import io.rtpi.client.RtpiClient
import ma.glasnost.orika.MapperFacade
import ma.glasnost.orika.impl.DefaultMapperFactory
import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module(
    includes = [
        ViewModelModule::class,
        DatabaseModule::class,
        RepositoryModule::class
    ]
)
class ApplicationModule {

    @Provides
    @Singleton
    fun context(application: DublinMapperApplication): Context = application.applicationContext

    @Provides
    @Singleton
    fun setupContainers(themeRepository: ThemeRepository): SetupContainers {
        return SetupContainers(
            listOf(
                PreferencesContainer(),
                TimberContainer(),
                ThemeContainer(themeRepository),
                RxContainer(),
                StethoContainer()
            )
        )
    }

    @Provides
    @Singleton
    fun resources(context: Context): Resources = context.resources

    @Provides
    @Singleton
    fun stringProvider(
        context: Context,
        resources: Resources
    ): StringProvider = StringResourceProvider(
        context,
        resources
    )

//    @Provides
//    fun mapMarkerManager(context: Context): GoogleMapController {
//        return GoogleMapController(context)
//    }

    @Provides
    @Singleton
    fun schedulers(): RxScheduler = RxScheduler(
        io = Schedulers.io(),
        ui = AndroidSchedulers.mainThread()
    )

    @Provides
    @Singleton
    fun internetManager(context: Context): InternetManager =
        WifiManager(context)

    @Provides
    @Singleton
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
    @Singleton
    fun client(okHttpClient: OkHttpClient): RtpiClient = RtpiClient(okHttpClient)

    @Provides
    @Singleton
    fun mapperFacade(
        stringProvider: StringProvider
    ): MapperFacade {
        val mapperFactory = DefaultMapperFactory.Builder().useBuiltinConverters(false).build()
        mapperFactory.converterFactory.apply {
            registerConverter(FavouritesDomainToUiMapper(stringProvider))
            registerConverter(NearbyDomainToUiMapper(stringProvider))
            registerConverter(LiveDataDomainToUiMapper(stringProvider))
            registerConverter(SearchDomainToUiMapper)
        }
        return mapperFactory.mapperFacade
    }

    @Provides
    @Singleton
    fun preferenceStore(context: Context): PreferenceStore = DefaultPreferenceStore(context)

    @Provides
    @Singleton
    fun themeRepository(
        resources: Resources,
        preferenceStore: PreferenceStore
    ): ThemeRepository = ThemeRepository(resources, preferenceStore)

    @Provides
    @Singleton
    fun enabledServiceManager(
        context: Context,
        preferenceStore: PreferenceStore
    ): EnabledServiceManager = DefaultEnabledServiceManager(
        preferenceStore = preferenceStore,
        serviceToEnabledServicePreferenceKey = mapOf(
            Service.AIRCOACH to context.getString(R.string.preference_key_enabled_service_aircoach),
            Service.BUS_EIREANN to context.getString(R.string.preference_key_enabled_service_bus_eireann),
            Service.DUBLIN_BIKES to context.getString(R.string.preference_key_enabled_service_dublin_bikes),
            Service.DUBLIN_BUS to context.getString(R.string.preference_key_enabled_service_dublin_bus),
            Service.IRISH_RAIL to context.getString(R.string.preference_key_enabled_service_irish_rail),
            Service.LUAS to context.getString(R.string.preference_key_enabled_service_luas)
        )
    )

    @Provides
    @Singleton
    fun locationProvider(context: Context): LocationProvider =
        GpsLocationProvider(context)

    @Provides
    @Singleton
    fun permissionChecker(context: Context): PermissionChecker = UserPermissionsChecker(context)

    @Provides
    @Singleton
    fun appConfig(): AppConfig = object : AppConfig {
        override fun isDebug() = BuildConfig.DEBUG
        override fun appVersion() = BuildConfig.VERSION_NAME
    }
}
