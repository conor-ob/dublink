package ie.dublinmapper.view.favourite

import com.hannesdorfmann.mosby3.mvp.MvpBasePresenter
import ie.dublinmapper.domain.usecase.FavouritesUseCase
import ie.dublinmapper.util.Thread
import ie.dublinmapper.util.ServiceLocationUiMapper
import io.reactivex.disposables.CompositeDisposable
import timber.log.Timber
import javax.inject.Inject

class FavouritesPresenterImpl @Inject constructor(
    private val useCase: FavouritesUseCase,
    private val thread: Thread
) : MvpBasePresenter<FavouritesView>(), FavouritesPresenter {

    private var subscriptions: CompositeDisposable? = null

    override fun onViewAttached() {
        subscriptions().add(useCase.getFavourites()
            .subscribeOn(thread.io)
            .observeOn(thread.ui)
            .map { ServiceLocationUiMapper.map(it) }
            .doOnNext { ifViewAttached { view -> view.showFavourites(it) } }
            .doOnError { Timber.e(it) }
            .subscribe()
        )
    }

    override fun onViewDetached() {
        subscriptions?.clear()
        subscriptions?.dispose()
        subscriptions = null
    }

    private fun subscriptions(): CompositeDisposable {
        if (subscriptions == null || subscriptions!!.isDisposed) {
            subscriptions = CompositeDisposable()
        }
        return subscriptions!!
    }

}
