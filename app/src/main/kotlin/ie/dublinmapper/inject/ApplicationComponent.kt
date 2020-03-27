package ie.dublinmapper.inject

import dagger.Component
import dagger.android.AndroidInjectionModule
import dagger.android.AndroidInjector
import ie.dublinmapper.DublinMapperApplication
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        AndroidInjectionModule::class,
        ApplicationModule::class,
        ActivityBuilderModule::class
    ]
)
interface ApplicationComponent : AndroidInjector<DublinMapperApplication> {

    @Component.Factory
    abstract class Factory : AndroidInjector.Factory<DublinMapperApplication>
}
