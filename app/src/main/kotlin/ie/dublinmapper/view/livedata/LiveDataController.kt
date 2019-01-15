package ie.dublinmapper.view.livedata

import android.os.Bundle
import ie.dublinmapper.MvpBaseController
import ie.dublinmapper.R
import ie.dublinmapper.util.getApplicationComponent

class LiveDataController(
    args: Bundle
) : MvpBaseController<LiveDataView, LiveDataPresenter>(args), LiveDataView {

    override val layoutId = R.layout.view_live_data

    override fun createPresenter(): LiveDataPresenter {
        return getApplicationComponent().liveDataPresenter()
    }

}
