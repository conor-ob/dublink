package ie.dublinmapper.view.livedata

import com.hannesdorfmann.mosby3.mvp.MvpView
import ie.dublinmapper.domain.model.LiveData

interface LiveDataView : MvpView {

    fun showLiveData(liveData: List<LiveData>)

}
