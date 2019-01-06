package ie.dublinmapper.di

import android.content.Context
import android.content.res.Resources
import dagger.Module
import dagger.Provides
import ie.dublinmapper.DublinMapperApplication
import ie.dublinmapper.util.AndroidResourceStringProvider
import ie.dublinmapper.util.StringProvider

@Module
class ApplicationModule {

    @Provides
    fun context(application: DublinMapperApplication): Context = application.applicationContext

    @Provides
    fun resources(context: Context): Resources = context.resources

    @Provides
    fun stringProvider(
        context: Context, resources: Resources
    ): StringProvider = AndroidResourceStringProvider(context, resources)

}
