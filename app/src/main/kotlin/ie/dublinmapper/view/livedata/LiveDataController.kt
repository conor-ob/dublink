package ie.dublinmapper.view.livedata

import android.os.Bundle
import ie.dublinmapper.MvpBaseController
import ie.dublinmapper.R
import ie.dublinmapper.domain.model.LiveData
import ie.dublinmapper.domain.model.ServiceLocation
import ie.dublinmapper.util.getApplicationComponent
import timber.log.Timber

class LiveDataController(
    args: Bundle
) : MvpBaseController<LiveDataView, LiveDataPresenter>(args), LiveDataView {

    override val layoutId = R.layout.view_live_data

    override fun createPresenter(): LiveDataPresenter {
        return getApplicationComponent().liveDataPresenter()
    }

    fun focusOnServiceLocation(serviceLocation: ServiceLocation) {
        presenter.onServiceLocationFocused(serviceLocation)
    }

    override fun showLiveData(liveData: List<LiveData>) {
        liveData.forEach { Timber.d(it.toString()) }
    }

}
