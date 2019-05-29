package ie.dublinmapper.view.favourite

import com.hannesdorfmann.mosby3.mvp.MvpPresenter

interface FavouritesPresenter : MvpPresenter<FavouritesView> {

    fun onViewAttached()

    fun onViewDetached()

}
