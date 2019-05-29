package ie.dublinmapper.di

import android.content.Context
import android.content.res.Resources
import dagger.Module
import dagger.Provides
import ie.dublinmapper.DublinMapperApplication
import ie.dublinmapper.util.*
import ie.dublinmapper.view.nearby.map.GoogleMapController
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Singleton

@Module
class ApplicationModule(
    private val application: DublinMapperApplication
) {

    @Provides
    fun context(): Context = application.applicationContext

    @Provides
    fun resources(context: Context): Resources = context.resources

    @Provides
    fun stringProvider(
        context: Context,
        resources: Resources
    ): StringProvider = AndroidResourceStringProvider(context, resources)

    @Provides
    fun mapMarkerManager(context: Context): GoogleMapController =
        GoogleMapController(context)

    @Provides
    fun schedulers(): RxScheduler {
        return RxScheduler(
            io = Schedulers.io(),
            ui = AndroidSchedulers.mainThread()
        )
    }

    @Provides
    @Singleton
    fun internetManager(context: Context): InternetManager {
        return InternetManagerImpl(context)
    }

}
