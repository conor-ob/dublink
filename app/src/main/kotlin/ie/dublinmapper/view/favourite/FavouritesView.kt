package ie.dublinmapper.view.favourite

import com.hannesdorfmann.mosby3.mvp.MvpView
import com.xwray.groupie.Group
import com.xwray.groupie.kotlinandroidextensions.Item
import ie.dublinmapper.domain.model.ServiceLocation

interface FavouritesView : MvpView {

    fun showFavourites(favourites: Group)

}
