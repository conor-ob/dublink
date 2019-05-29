package ie.dublinmapper.view.livedata.dart

import com.hannesdorfmann.mosby3.mvp.MvpPresenter

interface DartLiveDataPresenter : MvpPresenter<DartLiveDataView> {

    fun start(stationId: String)

    fun stop()

}
