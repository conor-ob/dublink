package ie.dublinmapper.view.livedata

import com.hannesdorfmann.mosby3.mvp.MvpPresenter
import ie.dublinmapper.domain.model.ServiceLocation

interface LiveDataPresenter : MvpPresenter<LiveDataView> {

    fun onViewAttached()

    fun onViewDetached()

    fun onServiceLocationFocused(serviceLocation: ServiceLocation)

}
