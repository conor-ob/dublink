package ie.dublinmapper.view.livedata

import com.hannesdorfmann.mosby3.mvp.MvpBasePresenter
import ie.dublinmapper.domain.usecase.LiveDataUseCase
import ie.dublinmapper.util.RxScheduler
import javax.inject.Inject

class LiveDataPresenterImpl @Inject constructor(
    private val useCase: LiveDataUseCase,
    private val scheduler: RxScheduler
): MvpBasePresenter<LiveDataView>(), LiveDataPresenter {

}
