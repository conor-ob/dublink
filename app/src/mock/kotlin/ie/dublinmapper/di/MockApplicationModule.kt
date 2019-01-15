package ie.dublinmapper.di

import dagger.Module
import dagger.Provides
import ie.dublinmapper.util.MockStringProvider
import ie.dublinmapper.util.StringProvider
import ie.dublinmapper.util.Thread
import io.reactivex.schedulers.TestScheduler

@Module
class MockApplicationModule {

    @Provides
    fun stringProvider(): StringProvider = MockStringProvider()

    @Provides
    fun schedulers(): Thread {
        return Thread(
            io = TestScheduler(),
            ui = TestScheduler()
        )
    }

}
