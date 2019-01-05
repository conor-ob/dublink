package ie.dublinmapper

import dagger.android.AndroidInjector
import dagger.android.DaggerApplication
import ie.dublinmapper.di.DaggerApplicationComponent
import ie.dublinmapper.repository.dublinbikes.DublinBikesRepositoryModule
import ie.dublinmapper.service.jcdecaux.JcdecauxModule
import ie.dublinmapper.util.MetadataUtils

class DublinMapperApplication : DaggerApplication() {

    override fun onCreate() {
        super.onCreate()
    }

    override fun applicationInjector(): AndroidInjector<out DaggerApplication> {
        return DaggerApplicationComponent.builder()
            .application(this)
            .jcdecauxModule(JcdecauxModule(getString(R.string.jcdecaux_base_url)))
            .dublinBikesRepositoryModule(DublinBikesRepositoryModule(
                MetadataUtils.getMetadata(this, "api.jcdecaux.API_KEY"),
                getString(R.string.jcdecaux_contract)
            ))
            .create(this)
    }

}
