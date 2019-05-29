package ie.dublinmapper.view.favourite

import com.hannesdorfmann.mosby3.mvp.MvpView
import ie.dublinmapper.model.ServiceLocationUi

interface FavouritesView : MvpView {

    fun showFavourites(favourites: List<ServiceLocationUi>)

}
