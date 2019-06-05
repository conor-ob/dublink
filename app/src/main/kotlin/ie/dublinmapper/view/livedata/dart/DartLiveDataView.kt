package ie.dublinmapper.view.livedata.dart

import com.hannesdorfmann.mosby3.mvp.MvpView

interface DartLiveDataView : MvpView {

    fun render(viewModel: ViewModel)

}
