package ie.dublinmapper.view.nearby.livedata

import com.hannesdorfmann.mosby3.mvp.MvpView

interface NearbyLiveDataView : MvpView {

    fun render(viewModel: NearbyLiveDataViewModel)

}
