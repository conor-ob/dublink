package ie.dublinmapper.view.nearby.livedata

import com.hannesdorfmann.mosby3.mvp.MvpPresenter
import ie.dublinmapper.model.ServiceLocationUi

interface NearbyLiveDataPresenter : MvpPresenter<NearbyLiveDataView> {

    fun onViewAttached()

    fun onViewDetached()

    fun onFocusedOnServiceLocation(serviceLocation: ServiceLocationUi)

}
