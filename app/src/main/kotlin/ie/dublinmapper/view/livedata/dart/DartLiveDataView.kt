package ie.dublinmapper.view.livedata.dart

import com.hannesdorfmann.mosby3.mvp.MvpView
import ie.dublinmapper.model.LiveDataUi

interface DartLiveDataView : MvpView {

    fun showLiveData(liveData: List<LiveDataUi>)

}
