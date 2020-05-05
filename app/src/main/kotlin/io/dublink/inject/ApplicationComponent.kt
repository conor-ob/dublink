package io.dublink.inject

import dagger.Component
import dagger.android.AndroidInjectionModule
import dagger.android.AndroidInjector
import io.dublink.DubLinkApplication
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        AndroidInjectionModule::class,
        ApplicationModule::class,
        ActivityBuilderModule::class
    ]
)
interface ApplicationComponent : AndroidInjector<DubLinkApplication> {

    @Component.Factory
    abstract class Factory : AndroidInjector.Factory<DubLinkApplication>
}
