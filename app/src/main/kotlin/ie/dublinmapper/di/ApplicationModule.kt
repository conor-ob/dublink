package ie.dublinmapper.di

import android.content.Context
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class ApplicationModule {

//    @Provides
//    @Singleton
//    fun context(): Context {
//        return context
//    }

    @Provides
    @Singleton
    fun dummy(): String {
        return "Hello World"
    }

}
