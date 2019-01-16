package ie.dublinmapper.view.favourite

import android.os.Bundle
import ie.dublinmapper.MvpBaseController
import ie.dublinmapper.R
import ie.dublinmapper.util.getApplicationComponent

class FavouritesController(args: Bundle) : MvpBaseController<FavouritesView, FavouritesPresenter>(args), FavouritesView {

    override val layoutId = R.layout.view_favourites

    override fun createPresenter(): FavouritesPresenter {
        return getApplicationComponent().favouritesPresenter()
    }

}
