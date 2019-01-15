package ie.dublinmapper.view.search

import com.hannesdorfmann.mosby3.mvp.MvpBasePresenter
import ie.dublinmapper.domain.usecase.SearchUseCase
import ie.dublinmapper.util.Thread
import io.reactivex.disposables.CompositeDisposable
import timber.log.Timber
import javax.inject.Inject

class SearchPresenterImpl @Inject constructor(
    private val useCase: SearchUseCase,
    private val thread: Thread
) : MvpBasePresenter<SearchView>(), SearchPresenter {

    private var subscriptions: CompositeDisposable? = null

    override fun onViewAttached() {

    }

    override fun onViewDetached() {
        subscriptions?.clear()
        subscriptions?.dispose()
        subscriptions = null
    }

    override fun onQueryTextSubmit(query: String) {
        Timber.d("query: $query")
        subscriptions().add(useCase.search(query)
            .subscribeOn(thread.io)
            .observeOn(thread.ui)
            .doOnSubscribe {  }
            .doOnNext { ifViewAttached { view -> view.showSearchResults(it) } }
            .doOnError { Timber.e(it) }
            .subscribe()
        )
    }

    private fun subscriptions(): CompositeDisposable {
        if (subscriptions == null || subscriptions!!.isDisposed) {
            subscriptions = CompositeDisposable()
        }
        return subscriptions!!
    }

}
