package ie.dublinmapper.di

import ie.dublinmapper.view.favourite.FavouritesPresenterImpl
import ie.dublinmapper.view.livedata.LiveDataPresenterImpl
import ie.dublinmapper.view.search.SearchPresenterImpl
import dagger.Component
import ie.dublinmapper.repository.aircoach.AircoachRepositoryModule
import ie.dublinmapper.repository.buseireann.BusEireannRepositoryModule
import ie.dublinmapper.repository.dart.DartRepositoryModule
import ie.dublinmapper.repository.dublinbikes.DublinBikesRepositoryModule
import ie.dublinmapper.repository.dublinbus.DublinBusRepositoryModule
import ie.dublinmapper.repository.favourite.FavouriteRepositoryModule
import ie.dublinmapper.repository.luas.LuasRepositoryModule
import ie.dublinmapper.repository.swordsexpress.SwordsExpressRepositoryModule
import ie.dublinmapper.service.aircoach.AircoachModule
import ie.dublinmapper.service.di.ServiceModule
import ie.dublinmapper.service.dublinbus.DublinBusModule
import ie.dublinmapper.service.github.GithubModule
import ie.dublinmapper.service.irishrail.IrishRailModule
import ie.dublinmapper.service.jcdecaux.JcDecauxModule
import ie.dublinmapper.service.rtpi.RtpiModule
import ie.dublinmapper.service.swordsexpress.SwordsExpressModule
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        ApplicationModule::class,
        AircoachRepositoryModule::class,
        BusEireannRepositoryModule::class,
        DartRepositoryModule::class,
        DublinBikesRepositoryModule::class,
        DublinBusRepositoryModule::class,
        LuasRepositoryModule::class,
        SwordsExpressRepositoryModule::class,
        FavouriteRepositoryModule::class,
        AircoachModule::class,
        DublinBusModule::class,
        GithubModule::class,
        IrishRailModule::class,
        JcDecauxModule::class,
        RtpiModule::class,
        SwordsExpressModule::class,
        DatabaseModule::class,
        ServiceModule::class
    ]
)
interface ApplicationComponent {

    fun favouritesPresenter(): FavouritesPresenterImpl

    fun liveDataPresenter(): LiveDataPresenterImpl

    fun searchPresenter(): SearchPresenterImpl

}
