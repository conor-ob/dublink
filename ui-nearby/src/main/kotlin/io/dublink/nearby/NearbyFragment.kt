package io.dublink.nearby

import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.View
import android.view.ViewTreeObserver
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import io.dublink.DubLinkFragment
import io.dublink.DubLinkNavigator
import io.dublink.domain.service.PreferenceStore
import io.dublink.model.AbstractServiceLocationItem
import io.dublink.model.getServiceLocation
import io.dublink.viewModelProvider
import io.rtpi.api.Coordinate
import timber.log.Timber
import javax.inject.Inject

class NearbyFragment : DubLinkFragment(R.layout.fragment_nearby) {

    private val viewModel by lazy { viewModelProvider(viewModelFactory) as NearbyViewModel }

    @Inject lateinit var preferenceStore: PreferenceStore
    @Inject lateinit var nearbyMapper: NearbyMapper

    private var liveDataRecyclerView: RecyclerView? = null
    private var liveDataAdapter: GroupAdapter<GroupieViewHolder>? = null
    private var liveDataLayoutListener: ViewTreeObserver.OnGlobalLayoutListener? = null
    private var bottomSheetBehavior: BottomSheetBehavior<View>? = null

    private var mapView: MapView? = null
    private var googleMap: GoogleMap? = null

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel.observableState.observe(
            viewLifecycleOwner, Observer { state ->
                state?.let { renderState(state) }
            }
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupMapView(view, savedInstanceState)
        setupLiveDataView(view)
    }

    private fun setupMapView(view: View, savedInstanceState: Bundle?) {
        mapView = view.findViewById<MapView>(R.id.map_view).apply {
            onCreate(savedInstanceState)
            getMapAsync { googleMap ->
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
                }
                this@NearbyFragment.googleMap = googleMap
            }
        }
    }

    private fun setupLiveDataView(view: View) {
        bottomSheetBehavior = BottomSheetBehavior.from(view.findViewById(R.id.bottom_sheet))
        liveDataAdapter = GroupAdapter<GroupieViewHolder>().apply {
            setOnItemClickListener { item, _ ->
                if (item is AbstractServiceLocationItem) {
                    (activity as DubLinkNavigator).navigateToLiveData(
                        serviceLocation = item.getServiceLocation()
                    )
                }
            }
        }
        liveDataRecyclerView = view.findViewById<RecyclerView>(R.id.nearby_live_data).apply {
            adapter = liveDataAdapter
            setHasFixedSize(false)
            layoutManager = LinearLayoutManager(requireContext())
            if (viewTreeObserver != null && viewTreeObserver.isAlive) {
                liveDataLayoutListener = ViewTreeObserver.OnGlobalLayoutListener {
                    Timber.d("OnGlobalLayoutListener $height px")
                    if (lastHeight != height) {
                        onLiveDataResized(height)
                    }
                    lastHeight = height
                }
                viewTreeObserver.addOnGlobalLayoutListener(liveDataLayoutListener)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        mapView?.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapView?.onPause()
    }

    override fun onStart() {
        super.onStart()
        mapView?.onStart()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView?.onLowMemory()
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
        mapView?.onStop()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mapView?.onSaveInstanceState(outState)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        liveDataRecyclerView?.apply {
            if (viewTreeObserver != null && viewTreeObserver.isAlive) {
                viewTreeObserver.removeOnGlobalLayoutListener(liveDataLayoutListener)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView?.onDestroy()
    }

    private fun renderState(state: State) {
        if (state.focusedServiceLocation != null) {
            liveDataAdapter?.update(listOf(nearbyMapper.map(state.focusedServiceLocation, state.focusedServiceLocationLiveData)))
        }
    }

    private var lastHeight = 0
    private var lastAnimator: ObjectAnimator? = null

    private fun onLiveDataResized(measuredHeight: Int) {
        val startingHeight = bottomSheetBehavior?.peekHeight ?: 0
        if (measuredHeight == 0) {
            return
        } else if (measuredHeight == startingHeight) {
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
