package ie.dublinmapper.repository.di

import com.nytimes.android.external.store3.base.impl.MemoryPolicy
import dagger.Module
import dagger.Provides
import ie.dublinmapper.repository.aircoach.AircoachRepositoryModule
import ie.dublinmapper.repository.buseireann.BusEireannRepositoryModule
import ie.dublinmapper.repository.dublinbikes.DublinBikesRepositoryModule
import ie.dublinmapper.repository.dublinbus.DublinBusRepositoryModule
import ie.dublinmapper.repository.favourite.FavouriteRepositoryModule
import ie.dublinmapper.repository.irishrail.IrishRailRepositoryModule
import ie.dublinmapper.repository.luas.LuasRepositoryModule
import io.rtpi.client.RtpiClient
import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit
import javax.inject.Named
import javax.inject.Singleton

@Module(
    includes = [
        AircoachRepositoryModule::class,
        BusEireannRepositoryModule::class,
        IrishRailRepositoryModule::class,
        DublinBikesRepositoryModule::class,
        DublinBusRepositoryModule::class,
        LuasRepositoryModule::class,
        FavouriteRepositoryModule::class
    ]
)
class RepositoryModule {

    @Provides
    @Singleton
    fun okHttpClient(): OkHttpClient =
        OkHttpClient.Builder()
            .addNetworkInterceptor(NetworkLoggingInterceptor())
            .retryOnConnectionFailure(true)
            .connectTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .build()

    @Provides
    @Singleton
    fun client(okHttpClient: OkHttpClient): RtpiClient = RtpiClient(okHttpClient)

    @Provides
    @Named("SHORT_TERM")
    fun shortTermMemoryPolicy(): MemoryPolicy {
        return newMemoryPolicy(1, TimeUnit.MINUTES)
    }

    @Provides
    @Named("LONG_TERM")
    fun longTermMemoryPolicy(): MemoryPolicy {
        return newMemoryPolicy(1, TimeUnit.DAYS)
    }

    private fun newMemoryPolicy(value: Long, timeUnit: TimeUnit): MemoryPolicy {
        return MemoryPolicy.builder()
            .setExpireAfterWrite(value)
            .setExpireAfterTimeUnit(timeUnit)
            .build()
    }

}
