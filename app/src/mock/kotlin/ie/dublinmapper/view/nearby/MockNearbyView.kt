package ie.dublinmapper.view.nearby

import com.hannesdorfmann.mosby3.mvp.MvpView
import ie.dublinmapper.model.ServiceLocationUi

class MockNearbyView : MvpView, NearbyView {

    override fun showServiceLocations(serviceLocations: Collection<ServiceLocationUi>) {
        print(serviceLocations)
    }

    override fun showLoading() {

    }

}
