package ie.dublinmapper.view.nearby

import com.hannesdorfmann.mosby3.mvp.MvpView
import ie.dublinmapper.model.ServiceLocationUi

interface NearbyView : MvpView {

    fun showServiceLocations(serviceLocations: Collection<ServiceLocationUi>)

    fun showLoading()

}
