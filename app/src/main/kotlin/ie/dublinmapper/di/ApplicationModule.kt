package ie.dublinmapper.di

import android.content.Context
import android.content.res.Resources
import dagger.Module
import dagger.Provides
import ie.dublinmapper.DublinMapperApplication

@Module
class ApplicationModule {

    @Provides
    fun context(application: DublinMapperApplication): Context = application.applicationContext

    @Provides
    fun resources(context: Context): Resources {
        return context.resources
    }

}
