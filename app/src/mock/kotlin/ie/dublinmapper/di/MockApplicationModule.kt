package ie.dublinmapper.di

import dagger.Module
import dagger.Provides
import ie.dublinmapper.util.MockStringProvider
import ie.dublinmapper.util.RxScheduler
import ie.dublinmapper.util.StringProvider
import io.reactivex.schedulers.TestScheduler

@Module
class MockApplicationModule {

    @Provides
    fun stringProvider(): StringProvider = MockStringProvider()

    @Provides
    fun schedulers(): RxScheduler {
        return RxScheduler(
            io = TestScheduler(),
            ui = TestScheduler()
        )
    }

}
