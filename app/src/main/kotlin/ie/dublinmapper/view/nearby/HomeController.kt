package ie.dublinmapper.view.nearby

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import com.bluelinelabs.conductor.RouterTransaction
import com.bluelinelabs.conductor.changehandler.FadeChangeHandler
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.floatingactionbutton.FloatingActionButton
import ie.dublinmapper.MvpBaseController
import ie.dublinmapper.model.ServiceLocationUi
import ie.dublinmapper.util.CircularRevealChangeHandler
import ie.dublinmapper.util.getApplicationComponent
import ie.dublinmapper.view.favourite.FavouritesController
import ie.dublinmapper.view.nearby.livedata.NearbyLiveDataController
import ie.dublinmapper.view.nearby.map.NearbyMapController
import ie.dublinmapper.view.search.SearchController
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.view_home.view.*
import timber.log.Timber
import java.util.concurrent.TimeUnit
import android.util.TypedValue
import ie.dublinmapper.R

class HomeController(args: Bundle) : MvpBaseController<NearbyView, NearbyPresenter>(args), NearbyView {

    private lateinit var nearbyMapController: NearbyMapController
    private lateinit var nearbyLiveDataController: NearbyLiveDataController
    private lateinit var searchFab: FloatingActionButton

    override val layoutId = R.layout.view_home

    override fun createPresenter(): NearbyPresenter {
        return getApplicationComponent().nearbyPresenter()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup): View {
        val view = super.onCreateView(inflater, container)
        setupMapView(view)
        setupLiveDataView(view)
        setupSearchFab(view)
        return view
    }

    private lateinit var bottomSheetBehavior: BottomSheetBehavior<ViewGroup>

    private fun setupMapView(view: View) {
        val nearbyMapRouter = getChildRouter(view.container_nearby_map)
        if (!nearbyMapRouter.hasRootController()) {
            nearbyMapController = NearbyMapController(Bundle.EMPTY)
            nearbyMapController.targetController = this
            nearbyMapRouter.setRoot(RouterTransaction.with(nearbyMapController))
        }
    }

    private fun setupLiveDataView(view: View) {
        bottomSheetBehavior = BottomSheetBehavior.from(view.container_bottom_sheet)
        bottomSheetBehavior.apply {
            isHideable = true
            peekHeight = 650
            state = BottomSheetBehavior.STATE_COLLAPSED
            setBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {

                private var oldState = BottomSheetBehavior.STATE_HIDDEN

                override fun onSlide(bottomSheet: View, slideOffset: Float) {
                    val dy = bottomSheet.top - view.view_nearby_root.height
//                    mapView.translationY = (dy / 2).toFloat()
//                    view.crosshair.setTranslationY(dy / 2)
                    if (dy >= -bottomSheetPeekHeightPx) {
                        view.search_fab.translationY = dy.toFloat()
                    }
                }

                @SuppressLint("SwitchIntDef")
                override fun onStateChanged(bottomSheet: View, newState: Int) {
                    Timber.d("BOTTOM SHEET $newState")
                    when (newState) {
                        BottomSheetBehavior.STATE_EXPANDED -> {
                            val bottomSheetRouter = getChildRouter(view.container_bottom_sheet)
                            val controller = FavouritesController(Bundle.EMPTY)
                            bottomSheetRouter.setRoot(RouterTransaction.with(controller)
                                .pushChangeHandler(FadeChangeHandler(500L))
                                .popChangeHandler(FadeChangeHandler(500L))
                            )
                        }
                        BottomSheetBehavior.STATE_COLLAPSED -> {
                            val bottomSheetRouter = getChildRouter(view.container_bottom_sheet)
                            val controller = NearbyLiveDataController(Bundle.EMPTY)
                            bottomSheetRouter.setRoot(RouterTransaction.with(controller)
                                .pushChangeHandler(FadeChangeHandler(500L))
                                .popChangeHandler(FadeChangeHandler(500L))
                            )
                        }
                    }
                }

            })
        }
        val bottomSheetRouter = getChildRouter(view.container_bottom_sheet)
        if (!bottomSheetRouter.hasRootController()) {
            nearbyLiveDataController = NearbyLiveDataController(Bundle.EMPTY)
//            nearbyLiveDataController.addBottomSheetListener(this@HomeController)
            nearbyLiveDataController.targetController = this
            bottomSheetRouter.setRoot(RouterTransaction.with(nearbyLiveDataController))
        }
    }

    private fun setupSearchFab(view: View) {
        searchFab = view.findViewById(R.id.search_fab)
        searchFab.setOnClickListener {
            val searchController = SearchController(Bundle.EMPTY)
            router.pushController(RouterTransaction
                .with(searchController)
                .pushChangeHandler(CircularRevealChangeHandler(searchFab, view.view_nearby_root))
                .popChangeHandler(CircularRevealChangeHandler(searchFab, view.view_nearby_root))
            )
        }
    }

    fun focusOnServiceLocation(serviceLocation: ServiceLocationUi?) {
        if (serviceLocation != null) {
            view?.toolbar?.backgroundTintList = ColorStateList.valueOf(serviceLocation.colourId)
        }
        (nearbyLiveDataController as NearbyLiveDataController).getLiveData(serviceLocation)
    }

    fun onLiveDataResized(measuredHeight: Int) {
        Timber.d("MEASURE measuredHeight = ${measuredHeight}px")
        val interpolator = AccelerateDecelerateInterpolator()
        val refreshInterval = 20L
        val refreshCount = 15L
        val totalRefreshTime = (refreshInterval * refreshCount).toFloat()
        val startingHeight = bottomSheetBehavior.peekHeight
        Timber.d("MEASURE startingHeight = ${startingHeight}px")

        val goUp = measuredHeight > startingHeight

        //https://stackoverflow.com/questions/47080589/dynamically-animate-bottomsheet-peekheight

        val animationDisposable = Observable.interval(refreshInterval, TimeUnit.MILLISECONDS)
            .take(refreshCount)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ count: Long ->
                if (goUp) {
                    val height = (startingHeight - (startingHeight - measuredHeight) *
                            interpolator.getInterpolation((count * refreshInterval) / totalRefreshTime)).toInt()
                    Timber.d("MEASURE going up $height px (${startingHeight + (startingHeight - measuredHeight)})")
                    if (height > measuredHeight) {
                        bottomSheetBehavior.peekHeight = startingHeight
                    } else {
                        bottomSheetBehavior.peekHeight = height
                    }

                } else {
                    val height = (startingHeight - (startingHeight - measuredHeight) *
                            interpolator.getInterpolation((count * refreshInterval) / totalRefreshTime)).toInt()
                    Timber.d("MEASURE going down $height px (${startingHeight - (startingHeight - measuredHeight)})")
                    if (height < measuredHeight) {
                        bottomSheetBehavior.peekHeight = measuredHeight
                    } else {
                        bottomSheetBehavior.peekHeight = height
                    }
                }
            }, { _: Throwable ->
                //do something here to reset peek height to original
            })
    }

    override fun onAttach(view: View) {
        super.onAttach(view)
        presenter.onViewAttached()
    }

    override fun onDetach(view: View) {
        presenter.onViewDetached()
        super.onDetach(view)
    }

    override fun onDestroyView(view: View) {
        presenter.onViewDestroyed()
        super.onDestroyView(view)
    }

    private var bottomSheetPeekHeightPx: Int = 0

    override fun onContextAvailable(context: Context) {
        super.onContextAvailable(context)
        bottomSheetPeekHeightPx = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            250F, context.resources.displayMetrics
        ).toInt()
    }

    override fun render(viewModel: NearbyViewModel) {

    }

}
