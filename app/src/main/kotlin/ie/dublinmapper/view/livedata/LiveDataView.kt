package ie.dublinmapper.view.livedata

import com.hannesdorfmann.mosby3.mvp.MvpView
import ie.dublinmapper.model.LiveDataUi

interface LiveDataView : MvpView {

    fun showLiveData(liveData: List<LiveDataUi>)

}
