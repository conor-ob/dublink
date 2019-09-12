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
import ie.dublinmapper.repository.swordsexpress.SwordsExpressRepositoryModule
import java.util.concurrent.TimeUnit
import javax.inject.Named

@Module(
    includes = [
        AircoachRepositoryModule::class,
        BusEireannRepositoryModule::class,
        IrishRailRepositoryModule::class,
        DublinBikesRepositoryModule::class,
        DublinBusRepositoryModule::class,
        LuasRepositoryModule::class,
        SwordsExpressRepositoryModule::class,
        FavouriteRepositoryModule::class
    ]
)
class RepositoryModule {

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
