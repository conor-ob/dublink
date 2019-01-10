package ie.dublinmapper.view.nearby

import com.hannesdorfmann.mosby3.mvp.MvpView
import ie.dublinmapper.domain.model.ServiceLocation

interface NearbyView : MvpView {

    fun showServiceLocations(serviceLocations: Collection<ServiceLocation>)

    fun showLoading()

}
