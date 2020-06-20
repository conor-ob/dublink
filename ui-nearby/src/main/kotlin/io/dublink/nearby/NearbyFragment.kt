package io.dublink.nearby

import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.FrameLayout
import android.widget.RelativeLayout
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import io.dublink.DubLinkFragment
import io.dublink.domain.service.PreferenceStore
import io.dublink.domain.service.RxScheduler
import io.dublink.viewModelProvider
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import io.rtpi.api.Coordinate
import kotlinx.android.synthetic.main.fragment_nearby.view.*
import timber.log.Timber
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import kotlin.math.abs

class NearbyFragment : DubLinkFragment(R.layout.fragment_nearby) {

    private val viewModel by lazy { viewModelProvider(viewModelFactory) as NearbyViewModel }
    @Inject lateinit var preferenceStore: PreferenceStore
    @Inject lateinit var rxScheduler: RxScheduler
    @Inject lateinit var nearbyMapper: NearbyMapper
    private lateinit var googleMapController: GoogleMapController
    private var adapter: GroupAdapter<GroupieViewHolder>? = null

    private var bottomSheetBehavior: BottomSheetBehavior<View>? = null

    private var rootView: View? = null
    private var googleMapView: GoogleMapView? = null
    private var googleMap: GoogleMap? = null

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel.observableState.observe(
            viewLifecycleOwner, Observer { state ->
                state?.let { renderState(state) }
            }
        )
    }

    private fun getGoogleMapView(): GoogleMapView? = rootView?.findViewById(R.id.google_map_view)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        this.rootView = view
        bottomSheetBehavior = BottomSheetBehavior.from(view.findViewById<FrameLayout>(R.id.bottom_sheet))
        googleMapController = GoogleMapController(requireContext())
        getGoogleMapView()?.onCreate(savedInstanceState)
        getGoogleMapView()?.getMapAsync { googleMap ->
            googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(requireContext(), R.raw.google_map_style))
            val (coordinate, zoom) = preferenceStore.getLastNearbyMapLocation()
            googleMap.moveCamera(
                CameraUpdateFactory.newCameraPosition(
                    CameraPosition.fromLatLngZoom(
                        LatLng(coordinate.latitude, coordinate.longitude), zoom
                    )
                )
            )
            googleMap.setOnCameraIdleListener {
                googleMap.cameraPosition.target?.apply {
                    viewModel.dispatch(Action.OnMapMoveFinished(Coordinate(latitude = latitude, longitude = longitude)))
                }
                googleMapController.redrawMarkers()
            }
            googleMap.setOnCameraMoveListener(googleMapController)
            googleMap.setOnMarkerClickListener { marker ->
                val serviceLocation = googleMapController.getServiceLocation(marker)
                if (serviceLocation != null) {
                    viewModel.dispatch(Action.GetLiveData(serviceLocation))
                    googleMap.animateCamera(
                        CameraUpdateFactory.newCameraPosition(
                            CameraPosition.fromLatLngZoom(
                                LatLng(serviceLocation.coordinate.latitude, serviceLocation.coordinate.longitude), googleMap.cameraPosition.zoom
                            )
                        )
                    )
                }
                return@setOnMarkerClickListener true
            }
            googleMapController.attachGoogleMap(googleMap)
            this.googleMap = googleMap
        }

        adapter = GroupAdapter()
        view.nearby_live_data.adapter = adapter
        view.nearby_live_data.setHasFixedSize(false)
        view.nearby_live_data.layoutManager = LinearLayoutManager(requireContext())
    }

    override fun onResume() {
        super.onResume()
        getGoogleMapView()?.onResume()
    }

    override fun onPause() {
        super.onPause()
        getGoogleMapView()?.onPause()
    }

    override fun onStart() {
        super.onStart()
        getGoogleMapView()?.onStart()
    }

    override fun onStop() {
        super.onStop()
        googleMap?.cameraPosition?.apply{
            preferenceStore.setLastNearbyMapLocation(
                coordinate = Coordinate(
                    latitude = target.latitude,
                    longitude = target.longitude
                ),
                zoom = zoom
            )
        }
        getGoogleMapView()?.onStop()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        getGoogleMapView()?.onSaveInstanceState(outState)
    }

    override fun onDestroy() {
        super.onDestroy()
        getGoogleMapView()?.onDestroy()
    }

    private var lastHeight = 0

    private fun renderState(state: State) {
        googleMapController.drawServiceLocations(state.nearbyServiceLocations)
        if (state.focusedServiceLocation != null) {
            adapter?.update(listOf(nearbyMapper.map(state.focusedServiceLocation, state.focusedServiceLocationLiveData)))
        }
        rootView?.findViewById<RecyclerView>(R.id.nearby_live_data)?.let {
            if (it.viewTreeObserver != null && it.viewTreeObserver.isAlive) {
                it.viewTreeObserver.addOnGlobalLayoutListener {
                    if (lastHeight != it.height) {
                        Timber.d("MEASURE height: ${it.height} px")
//                        if (state.focusedServiceLocationLiveData != null) {
                            onLiveDataResized(it.height)
//                        }
                    }
                    lastHeight = it.height
                }
            }
        }
    }

    var lastAnimator: ObjectAnimator? = null

    fun onLiveDataResized(measuredHeight: Int) {
        if (measuredHeight == 0) {
            return
        }
        val startingHeight = bottomSheetBehavior?.peekHeight ?: 0
        if (measuredHeight == startingHeight) {
            return
        }

        lastAnimator?.cancel()
        lastAnimator = ObjectAnimator.ofInt(bottomSheetBehavior, "peekHeight", measuredHeight)
            .apply {
                duration = 300
                start()
            }
    }
}
