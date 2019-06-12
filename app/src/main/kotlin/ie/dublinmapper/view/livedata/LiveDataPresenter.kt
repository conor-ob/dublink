package ie.dublinmapper.view.livedata

import com.hannesdorfmann.mosby3.mvp.MvpPresenter
import ie.dublinmapper.util.Service

interface LiveDataPresenter : MvpPresenter<LiveDataView> {

    fun start(serviceLocationId: String, service: Service)

    fun stop()

}
