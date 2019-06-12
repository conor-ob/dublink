package ie.dublinmapper.view.livedata

import com.hannesdorfmann.mosby3.mvp.MvpView
import com.xwray.groupie.Group

interface LiveDataView : MvpView {

    fun showLiveData(liveData: List<Group>)

}
