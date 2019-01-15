package ie.dublinmapper.view.nearby.livedata

import com.hannesdorfmann.mosby3.mvp.MvpView
import ie.dublinmapper.model.LiveDataUi
import ie.dublinmapper.model.ServiceLocationUi

interface NearbyLiveDataView : MvpView {

    fun showServiceLocationColour(colourId: Int)

    fun showServiceLocation(serviceLocation: ServiceLocationUi)

    fun showLiveData(liveData: List<LiveDataUi>)

}
