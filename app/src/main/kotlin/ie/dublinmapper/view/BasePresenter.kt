package ie.dublinmapper.view

import com.hannesdorfmann.mosby3.mvp.MvpBasePresenter
import com.hannesdorfmann.mosby3.mvp.MvpView
import ie.dublinmapper.util.RxScheduler
import io.reactivex.CompletableTransformer
import io.reactivex.ObservableTransformer
import io.reactivex.disposables.CompositeDisposable

abstract class BasePresenter<V : MvpView>(
    private val scheduler: RxScheduler
) : MvpBasePresenter<V>() {

    private var subscriptions: CompositeDisposable? = null

    protected fun <T> applySchedulers(): ObservableTransformer<T, T> {
        return ObservableTransformer { upstream ->
            upstream.subscribeOn(scheduler.io)
                .observeOn(scheduler.ui)
        }
    }

    protected fun applyCompletableSchedulers(): CompletableTransformer {
        return CompletableTransformer { upstream ->
            upstream.subscribeOn(scheduler.io)
                .observeOn(scheduler.ui)
        }
    }

    protected fun unsubscribe() {
        subscriptions?.clear()
        subscriptions?.dispose()
        subscriptions = null
    }

    protected fun subscriptions(): CompositeDisposable {
        if (subscriptions == null || subscriptions!!.isDisposed) {
            subscriptions = CompositeDisposable()
        }
        return subscriptions!!
    }

}
