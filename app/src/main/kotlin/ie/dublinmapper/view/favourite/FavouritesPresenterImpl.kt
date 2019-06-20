package ie.dublinmapper.view.favourite

import com.xwray.groupie.Group
import ie.dublinmapper.domain.usecase.FavouritesUseCase
import ie.dublinmapper.util.RxScheduler
import ie.dublinmapper.view.BasePresenter
import ma.glasnost.orika.MapperFacade
import timber.log.Timber
import javax.inject.Inject

class FavouritesPresenterImpl @Inject constructor(
    private val useCase: FavouritesUseCase,
    private val mapper: MapperFacade,
    scheduler: RxScheduler
) : BasePresenter<FavouritesView>(scheduler), FavouritesPresenter {

    override fun start() {
        subscriptions().add(useCase.getFavourites()
            .compose(applySchedulers())
            .map { mapper.map(it, Group::class.java) }
            .doOnNext { ifViewAttached { view -> view.showFavourites(it) } }
            .doOnError { Timber.e(it) }
            .subscribe()
        )
    }

    override fun stop() {
        unsubscribe()
    }

}
