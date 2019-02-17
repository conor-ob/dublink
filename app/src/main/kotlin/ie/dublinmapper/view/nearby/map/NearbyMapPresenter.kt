package ie.dublinmapper.view.nearby.map

import com.hannesdorfmann.mosby3.mvp.MvpPresenter
import ie.dublinmapper.util.Coordinate

interface NearbyMapPresenter : MvpPresenter<NearbyMapView> {

    fun onViewAttached()

    fun onViewDetached()

    fun onViewDestroyed()

    fun onMapReady()

    fun onCameraMoved(coordinate: Coordinate)

}
