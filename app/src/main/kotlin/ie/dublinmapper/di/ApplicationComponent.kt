package ie.dublinmapper.di

import ie.dublinmapper.view.favourite.FavouritesPresenterImpl
import ie.dublinmapper.view.livedata.LiveDataPresenterImpl
import ie.dublinmapper.view.livedata.dart.DartLiveDataPresenterImpl
import ie.dublinmapper.view.nearby.NearbyPresenterImpl
import ie.dublinmapper.view.nearby.livedata.NearbyLiveDataPresenterImpl
import ie.dublinmapper.view.nearby.map.NearbyMapPresenterImpl
import ie.dublinmapper.view.search.SearchPresenterImpl

interface ApplicationComponent {

    fun nearbyPresenter(): NearbyPresenterImpl

    fun nearbyMapPresenter(): NearbyMapPresenterImpl

    fun nearbyLiveDataPresenter(): NearbyLiveDataPresenterImpl

    fun favouritesPresenter(): FavouritesPresenterImpl

    fun liveDataPresenter(): LiveDataPresenterImpl

    fun dartLiveDataPresenter(): DartLiveDataPresenterImpl

    fun searchPresenter(): SearchPresenterImpl

}
