package io.dublink.inject

import android.content.Context
import android.content.res.Resources
import com.facebook.stetho.okhttp3.StethoInterceptor
import dagger.Module
import dagger.Provides
import io.dublink.BuildConfig
import io.dublink.DubLinkApplication
import io.dublink.database.DatabaseModule
import io.dublink.domain.internet.InternetStatusChangeListener
import io.dublink.domain.service.AppConfig
import io.dublink.domain.service.EnabledServiceManager
import io.dublink.domain.service.InternetManager
import io.dublink.domain.service.LocationProvider
import io.dublink.domain.service.PermissionChecker
import io.dublink.domain.service.PreferenceStore
import io.dublink.domain.service.RxScheduler
import io.dublink.domain.service.StringProvider
import io.dublink.internet.DeviceInternetManager
import io.dublink.internet.NetworkConnectionInterceptor
import io.dublink.internet.NetworkStatusChangeListener
import io.dublink.location.GpsLocationProvider
import io.dublink.logging.NetworkLoggingInterceptor
import io.dublink.permission.UserPermissionsChecker
import io.dublink.repository.inject.RepositoryModule
import io.dublink.resource.StringResourceProvider
import io.dublink.settings.DefaultEnabledServiceManager
import io.dublink.settings.DefaultPreferenceStore
import io.dublink.settings.ThemeRepository
import io.dublink.startup.PreferencesStartupWorker
import io.dublink.startup.RxStartupWorker
import io.dublink.startup.StartupWorkers
import io.dublink.startup.StethoStartupWorker
import io.dublink.startup.ThemeStartupWorker
import io.dublink.startup.TimberStartupWorker
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
    fun context(application: DubLinkApplication): Context = application.applicationContext

    @Provides
    @Singleton
    fun resources(context: Context): Resources = context.resources

    @Provides
    @Singleton
    fun preferenceStore(context: Context): PreferenceStore =
        DefaultPreferenceStore(context)

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
    ): ThemeRepository =
        ThemeRepository(resources, preferenceStore)

    @Provides
    @Singleton
    fun startupWorkers(themeRepository: ThemeRepository): StartupWorkers =
        StartupWorkers(
            listOf(
                PreferencesStartupWorker(),
                TimberStartupWorker(),
                ThemeStartupWorker(themeRepository),
                RxStartupWorker(),
                StethoStartupWorker()
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
