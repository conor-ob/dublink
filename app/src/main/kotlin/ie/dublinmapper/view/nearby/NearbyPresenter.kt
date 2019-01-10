package ie.dublinmapper.view.nearby

import com.hannesdorfmann.mosby3.mvp.MvpPresenter
import ie.dublinmapper.util.Coordinate

interface NearbyPresenter : MvpPresenter<NearbyView> {

    fun onViewAttached()

    fun onViewDetached()

    fun onCameraMoved(coordinate: Coordinate)

}