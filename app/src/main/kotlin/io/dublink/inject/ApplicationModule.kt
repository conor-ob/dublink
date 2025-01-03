package io.dublink.inject

import android.content.Context
import android.content.res.Resources
import dagger.Module
import dagger.Provides
import io.dublink.BuildConfig
import io.dublink.DubLinkApplication
import io.dublink.database.DatabaseModule
import io.dublink.domain.internet.InternetStatusChangeListener
import io.dublink.domain.service.AppConfig
import io.dublink.domain.service.DubLinkProService
import io.dublink.domain.service.EnabledServiceManager
import io.dublink.domain.service.InternetManager
import io.dublink.domain.service.LocationProvider
import io.dublink.domain.service.PermissionChecker
import io.dublink.domain.service.PreferenceStore
import io.dublink.domain.service.RxScheduler
import io.dublink.domain.service.StringProvider
import io.dublink.domain.service.ThemeService
import io.dublink.domain.util.XorEncryption
import io.dublink.iap.BillingClientFactory
import io.dublink.iap.InAppPurchaseVerifier
import io.dublink.iap.RxBilling
import io.dublink.iap.RxBillingImpl
import io.dublink.internet.DeviceInternetManager
import io.dublink.internet.NetworkStatusChangeListener
import io.dublink.location.GpsLocationProvider
import io.dublink.logging.NetworkLoggingInterceptor
import io.dublink.permission.UserPermissionsChecker
import io.dublink.repository.inject.RepositoryModule
import io.dublink.resource.StringResourceProvider
import io.dublink.search.LuceneSearchService
import io.dublink.search.SearchService
import io.dublink.settings.DefaultEnabledServiceManager
import io.dublink.settings.DefaultPreferenceStore
import io.dublink.settings.DubLinkProPreferencesService
import io.dublink.settings.ThemeRepository
import io.dublink.startup.LoggingStartupWorker
import io.dublink.startup.PreferencesStartupWorker
import io.dublink.startup.ProcessLifecycleStartupWorker
import io.dublink.startup.RxStartupWorker
import io.dublink.startup.StartupWorkers
import io.dublink.startup.ThemeStartupWorker
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import io.rtpi.client.RtpiClient
import io.rtpi.client.RtpiClientConfiguration
import java.time.Duration
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
    fun dubLinkProService(
        preferenceStore: PreferenceStore,
        themeService: ThemeService,
        resources: Resources
    ): DubLinkProService = DubLinkProPreferencesService(preferenceStore, themeService, resources)

    @Provides
    @Singleton
    fun locationProvider(
        context: Context,
        preferenceStore: PreferenceStore
    ): LocationProvider = GpsLocationProvider(preferenceStore, context)

    @Provides
    @Singleton
    fun rtpiClient(
        okHttpClient: OkHttpClient
    ): RtpiClient = RtpiClient(
        rtpiClientConfiguration = RtpiClientConfiguration(
            okHttpClient = okHttpClient,
            dublinBikesApiKey = XorEncryption.decode(BuildConfig.NZAYAPNK)
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
    fun appConfig(): AppConfig = object : AppConfig {
        override fun isDebug() = BuildConfig.DEBUG
        override fun appVersion() = BuildConfig.VERSION_NAME
        override fun publicKey() = XorEncryption.decode(BuildConfig.KQVBLPMG)
    }

    @Provides
    @Singleton
    fun themeService(
        resources: Resources,
        preferenceStore: PreferenceStore
    ): ThemeService = ThemeRepository(resources, preferenceStore)

    @Provides
    @Singleton
    fun startupWorkers(
        themeService: ThemeService,
        dubLinkProService: DubLinkProService
    ): StartupWorkers =
        StartupWorkers(
            listOf(
                PreferencesStartupWorker(),
                LoggingStartupWorker(),
                ThemeStartupWorker(themeService),
                RxStartupWorker(),
                ProcessLifecycleStartupWorker(dubLinkProService)
            )
        )

    @Provides
    @Singleton
    fun okHttpClient(
        internetManager: InternetManager
    ): OkHttpClient =
        OkHttpClient.Builder()
//            .addNetworkInterceptor(NetworkConnectionInterceptor(internetManager))
            .addNetworkInterceptor(NetworkLoggingInterceptor())
//            .retryOnConnectionFailure(true)
            .connectTimeout(Duration.ofSeconds(30L))
//            .writeTimeout(Duration.ofSeconds(30L))
            .readTimeout(Duration.ofSeconds(30L))
            .build()

    @Provides
    @Singleton
    fun searchService(): SearchService = LuceneSearchService()

    @Provides
    @Singleton
    fun rxBilling(
        context: Context,
        scheduler: RxScheduler
    ): RxBilling = RxBillingImpl(BillingClientFactory(context), scheduler)

    @Provides
    @Singleton
    fun inAppPurchaseVerifier(
        appConfig: AppConfig
    ): InAppPurchaseVerifier = InAppPurchaseVerifier(appConfig.publicKey())
}
