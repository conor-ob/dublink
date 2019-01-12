package ie.dublinmapper.view.livedata

import android.os.Bundle
import ie.dublinmapper.MvpBaseController
import ie.dublinmapper.R
import ie.dublinmapper.model.LiveDataUi
import ie.dublinmapper.model.ServiceLocationUi
import ie.dublinmapper.util.getApplicationComponent
import timber.log.Timber

class LiveDataController(
    args: Bundle
) : MvpBaseController<LiveDataView, LiveDataPresenter>(args), LiveDataView {

    override val layoutId = R.layout.view_live_data

    override fun createPresenter(): LiveDataPresenter {
        return getApplicationComponent().liveDataPresenter()
    }

    fun focusOnServiceLocation(serviceLocation: ServiceLocationUi) {
        presenter.onFocusedOnServiceLocation(serviceLocation)
    }

    override fun showLiveData(liveData: List<LiveDataUi>) {
        liveData.forEach { Timber.d(it.toString()) }
    }

}
