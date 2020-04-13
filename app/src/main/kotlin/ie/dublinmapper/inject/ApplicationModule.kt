package ie.dublinmapper.inject

import android.content.Context
import android.content.res.Resources
import com.facebook.stetho.okhttp3.StethoInterceptor
import dagger.Module
import dagger.Provides
import ie.dublinmapper.BuildConfig
import ie.dublinmapper.DublinMapperApplication
import ie.dublinmapper.database.DatabaseModule
import ie.dublinmapper.domain.internet.InternetStatusChangeListener
import ie.dublinmapper.domain.service.AppConfig
import ie.dublinmapper.domain.service.EnabledServiceManager
import ie.dublinmapper.domain.service.InternetManager
import ie.dublinmapper.domain.service.LocationProvider
import ie.dublinmapper.domain.service.PermissionChecker
import ie.dublinmapper.domain.service.PreferenceStore
import ie.dublinmapper.domain.service.RxScheduler
import ie.dublinmapper.domain.service.StringProvider
import ie.dublinmapper.internet.DeviceInternetManager
import ie.dublinmapper.internet.NetworkConnectionInterceptor
import ie.dublinmapper.internet.NetworkStatusChangeListener
import ie.dublinmapper.location.GpsLocationProvider
import ie.dublinmapper.logging.NetworkLoggingInterceptor
import ie.dublinmapper.permission.UserPermissionsChecker
import ie.dublinmapper.repository.inject.RepositoryModule
import ie.dublinmapper.resource.StringResourceProvider
import ie.dublinmapper.settings.DefaultEnabledServiceManager
import ie.dublinmapper.settings.DefaultPreferenceStore
import ie.dublinmapper.settings.ThemeRepository
import ie.dublinmapper.startup.PreferencesStartupWorker
import ie.dublinmapper.startup.RxStartupWorker
import ie.dublinmapper.startup.StartupWorkers
import ie.dublinmapper.startup.StethoStartupWorker
import ie.dublinmapper.startup.ThemeStartupWorker
import ie.dublinmapper.startup.TimberStartupWorker
import ie.dublinmapper.startup.TwitterStartupWorker
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import io.rtpi.client.RtpiClient
import io.rtpi.client.RtpiClientConfiguration
import java.util.concurrent.TimeUnit
import javax.inject.Singleton
import okhttp3.OkHttpClient

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
    fun resources(context: Context): Resources = context.resources

    @Provides
    @Singleton
    fun preferenceStore(context: Context): PreferenceStore = DefaultPreferenceStore(context)

    @Provides
    @Singleton
    fun stringProvider(resources: Resources): StringProvider = StringResourceProvider(resources)

    @Provides
    @Singleton
    fun internetManager(context: Context): InternetManager = DeviceInternetManager(context)

    @Provides
    @Singleton
    fun permissionChecker(context: Context): PermissionChecker = UserPermissionsChecker(context)

    @Provides
    @Singleton
    fun schedulers(): RxScheduler = RxScheduler(Schedulers.io(), AndroidSchedulers.mainThread())

    @Provides
    @Singleton
    fun locationProvider(
        context: Context,
        preferenceStore: PreferenceStore
    ): LocationProvider = GpsLocationProvider(preferenceStore, context)

    @Provides
    @Singleton
    fun rtpiClient(
        okHttpClient: OkHttpClient,
        stringProvider: StringProvider
    ): RtpiClient = RtpiClient(
        RtpiClientConfiguration(
            okHttpClient,
            stringProvider.jcDecauxApiKey()
        )
    )

    @Provides
    @Singleton
    fun enabledServiceManager(
        preferenceStore: PreferenceStore
    ): EnabledServiceManager = DefaultEnabledServiceManager(preferenceStore)

    @Provides
    @Singleton
    fun internetStatusChangeListener(
        context: Context
    ): InternetStatusChangeListener = NetworkStatusChangeListener(context)

    @Provides
    @Singleton
    fun appConfig(): AppConfig = object :
        AppConfig {
        override fun isDebug() = BuildConfig.DEBUG
        override fun appVersion() = BuildConfig.VERSION_NAME
    }

    @Provides
    @Singleton
    fun themeRepository(
        resources: Resources,
        preferenceStore: PreferenceStore
    ): ThemeRepository = ThemeRepository(resources, preferenceStore)

    @Provides
    @Singleton
    fun startupWorkers(themeRepository: ThemeRepository): StartupWorkers = StartupWorkers(
        listOf(
            PreferencesStartupWorker(),
            TimberStartupWorker(),
            ThemeStartupWorker(themeRepository),
            RxStartupWorker(),
            StethoStartupWorker(),
            TwitterStartupWorker()
        )
    )

    @Provides
    @Singleton
    fun okHttpClient(
        internetManager: InternetManager
    ): OkHttpClient =
        OkHttpClient.Builder()
            .addNetworkInterceptor(NetworkConnectionInterceptor(internetManager))
            .addNetworkInterceptor(NetworkLoggingInterceptor())
            .addNetworkInterceptor(StethoInterceptor())
            .retryOnConnectionFailure(true)
            .connectTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .build()
}
