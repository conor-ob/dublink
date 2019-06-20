package ie.dublinmapper.view.search

import com.xwray.groupie.Group
import ie.dublinmapper.domain.usecase.SearchUseCase
import ie.dublinmapper.util.RxScheduler
import ie.dublinmapper.view.BasePresenter
import ma.glasnost.orika.MapperFacade
import javax.inject.Inject

class SearchPresenterImpl @Inject constructor(
    private val useCase: SearchUseCase,
    private val mapper: MapperFacade,
    scheduler: RxScheduler
) : BasePresenter<SearchView>(scheduler), SearchPresenter {

    override fun start(query: String) {
        subscriptions().add(useCase.search(query)
            .compose(applySchedulers())
            .doOnSubscribe {
                ifViewAttached { view -> view.showLoading(true) }
            }
            .doOnNext { response ->
                val searchResults = mapper.map(response, Group::class.java)
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
