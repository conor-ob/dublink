package ie.dublinmapper.view.search

import ie.dublinmapper.domain.usecase.SearchUseCase
import ie.dublinmapper.util.ServiceLocationUiMapper
import ie.dublinmapper.util.RxScheduler
import ie.dublinmapper.view.BasePresenter
import javax.inject.Inject

class SearchPresenterImpl @Inject constructor(
    private val useCase: SearchUseCase,
    scheduler: RxScheduler
) : BasePresenter<SearchView>(scheduler), SearchPresenter {

    override fun start(query: String) {
        subscriptions().add(useCase.search(query)
            .compose(applySchedulers())
            .doOnSubscribe {
                ifViewAttached { view -> view.showLoading(true) }
            }
            .doOnNext { serviceLocations ->
                val searchResults = ServiceLocationUiMapper.map(serviceLocations)
                ifViewAttached { view -> view.showSearchResults(searchResults) }
            }
            .doFinally {
                ifViewAttached { view -> view.showLoading(false) }
            }
            .doOnError {
                ifViewAttached { view -> view.showError() }
            }
            .subscribe()
        )
    }

    override fun stop() {
        unsubscribe()
    }

}
