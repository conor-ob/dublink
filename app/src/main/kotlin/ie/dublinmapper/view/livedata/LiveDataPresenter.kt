package ie.dublinmapper.view.livedata

import com.hannesdorfmann.mosby3.mvp.MvpPresenter
import ie.dublinmapper.model.ServiceLocationUi

interface LiveDataPresenter : MvpPresenter<LiveDataView> {

    fun onViewAttached()

    fun onViewDetached()

    fun onFocusedOnServiceLocation(serviceLocation: ServiceLocationUi)

}
