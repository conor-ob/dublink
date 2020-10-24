package io.dublink.nearby

import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.maps.android.PolyUtil
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import io.dublink.DubLinkFragment
import io.dublink.DubLinkNavigator
import io.dublink.domain.model.DubLinkServiceLocation
import io.dublink.domain.model.DubLinkStopLocation
import io.dublink.domain.service.PreferenceStore
import io.dublink.model.AbstractServiceLocationItem
import io.dublink.model.getServiceLocation
import io.dublink.nearby.util.AnimationUtils
import io.dublink.nearby.util.ImageUtils
import io.dublink.viewModelProvider
import io.rtpi.api.Coordinate
import io.rtpi.api.Operator
import io.rtpi.api.Service
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

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = requireNotNull(super.onCreateView(inflater, container, savedInstanceState))
        setupMapView(view, savedInstanceState)
        setupLiveDataView(view)
        return view
    }

    private fun setupMapView(view: View, savedInstanceState: Bundle?) {
        mapView = view.findViewById<MapView>(R.id.map_view).apply {
            onCreate(savedInstanceState)
            getMapAsync { googleMap ->
                googleMap.cameraPosition.target?.apply {
                    viewModel.dispatch(Action.OnMapMoveFinished(Coordinate(latitude = latitude, longitude = longitude)))
                }
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
                googleMap.setOnMarkerClickListener { marker ->
                    for (entry in mapMarkers.entries) {
                        if (entry.value == marker) {
                            val serviceLocation = entry.key
                            viewModel.dispatch(Action.GetLiveData(serviceLocation))
                            googleMap.animateCamera(
                                CameraUpdateFactory.newCameraPosition(
                                    CameraPosition.fromLatLngZoom(
                                        LatLng(serviceLocation.coordinate.latitude, serviceLocation.coordinate.longitude), googleMap.cameraPosition.zoom
                                    )
                                ),
                                300,
                                object : GoogleMap.CancelableCallback {

                                    override fun onCancel() {
                                    }

                                    override fun onFinish() {
                                    }
                                }
                            )
                            return@setOnMarkerClickListener true
                        }
                    }
                    return@setOnMarkerClickListener false
                }
                this@NearbyFragment.googleMap = googleMap
                drawRailLines()
            }
        }
    }

    private fun setupLiveDataView(view: View) {
        bottomSheetBehavior = BottomSheetBehavior.from(view.findViewById(R.id.bottom_sheet))
        bottomSheetBehavior?.peekHeight = preferenceStore.getPeekHeight()
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
        for (entry in mapMarkers.entries) {
            entry.value.remove()
        }
        bottomSheetBehavior?.apply {
            preferenceStore.setPeekHeight(peekHeight)
        }
        mapMarkers.clear()
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView?.onDestroy()
    }

    // TODO synchronized map ?
    private val mapMarkers = mutableMapOf<DubLinkServiceLocation, Marker>()

    private fun renderState(state: State) {
        renderMapMarkers(state)
        renderLiveData(state)
    }

    private fun renderMapMarkers(state: State) {
//        for (serviceLocation in state.nearbyServiceLocations.take(1)) {
//            val marker = requireNotNull(googleMap).addMarker(
//                MarkerOptions()
//                    .position(LatLng(serviceLocation.coordinate.latitude, serviceLocation.coordinate.longitude))
//                    .title(serviceLocation.defaultName)
//                    .snippet(serviceLocation.service.fullName)
//            )
//            marker.tag = serviceLocation
//            mapMarkers[serviceLocation] = marker
//        }

        addNewMarkers(state.nearbyServiceLocations)
        removeOldMarkers(state.nearbyServiceLocations)
    }

    private fun addNewMarkers(serviceLocations: Collection<DubLinkServiceLocation>) {
        for (serviceLocation in serviceLocations) {
            Timber.d("GoogleMap serviceLocation")
            if (mapMarkers[serviceLocation] == null) {
                Timber.d("GoogleMap trying to creating new marker")
                newMarker(serviceLocation)?.apply {
                    Timber.d("GoogleMap creating new marker")
                    this.tag = serviceLocation
                    mapMarkers[serviceLocation] = this
                    AnimationUtils.fadeInMarker(this)
                }
            }
        }
    }

    private fun newMarker(serviceLocation: DubLinkServiceLocation): Marker? {
        return googleMap?.addMarker(
            MarkerOptions()
                .position(LatLng(serviceLocation.coordinate.latitude, serviceLocation.coordinate.longitude))
//                .title(serviceLocation.defaultName)
//                .snippet(serviceLocation.service.fullName)
                .icon(newIcon(serviceLocation))
                .anchor(
                    when (serviceLocation.service) {
                        Service.IRISH_RAIL -> 0.5f
                        Service.LUAS -> 0.5f
                        else -> 0.5f
                    },
                    when (serviceLocation.service) {
                        Service.IRISH_RAIL -> 0.5f
                        Service.LUAS -> 0.5f
                        else -> 0.5f
                    }
                )
        )
    }

    private fun newIcon(serviceLocation: DubLinkServiceLocation): BitmapDescriptor {
        val draw = when (serviceLocation.service) {
            Service.AIRCOACH -> throw RuntimeException()
            Service.BUS_EIREANN -> R.drawable.ic_map_marker_buseireann_dot
            Service.DUBLIN_BIKES -> R.drawable.ic_map_marker_dublinbikes_dot
            Service.DUBLIN_BUS -> {
                val routes = (serviceLocation as DubLinkStopLocation).stopLocation.routeGroups.groupBy { it.operator }
                val dbRoutes = routes[Operator.DUBLIN_BUS]?.size ?: 0
                val gaRoutes = routes[Operator.GO_AHEAD]?.size ?: 0
                if (dbRoutes >= gaRoutes) {
                    R.drawable.ic_map_marker_dublinbus_dot
                } else {
                    R.drawable.ic_map_marker_goahead_dot
                }
            }
            Service.IRISH_RAIL -> {
                R.drawable.ic_map_marker_irishrail
//                val routes = (serviceLocation as DubLinkStopLocation).stopLocation.routeGroups.groupBy { it.operator }
//                if (routes[Operator.DART] != null) {
//                    R.drawable.ic_map_marker_irishrail_dart_dot
//                } else {
//                    val icRoutes = routes[Operator.INTERCITY]?.size ?: 0
//                    val coRoutes = routes[Operator.COMMUTER]?.size ?: 0
//                    if (coRoutes >= icRoutes) {
//                        R.drawable.ic_map_marker_irishrail_commuter_dot
//                    } else {
//                        R.drawable.ic_map_marker_irishrail_intercity_dot
//                    }
//                }
            }
            Service.LUAS -> {
                R.drawable.ic_map_marker_luas
//                val routes = (serviceLocation as DubLinkStopLocation).stopLocation.routeGroups.flatMap { it.routes }
//                val redRoutes = routes.filter { "red".equals(it, ignoreCase = true) }.size
//                val greenRoutes = routes.filter { "green".equals(it, ignoreCase = true) }.size
//                if (greenRoutes >= redRoutes) {
//                    R.drawable.ic_map_marker_luas_green_dot
//                } else {
//                    R.drawable.ic_map_marker_luas_red_dot
//                }
            }
        }
        return ImageUtils.drawableToBitmap(requireContext(), draw)
    }

    private fun removeOldMarkers(serviceLocations: Collection<DubLinkServiceLocation>) {
        val iterator = mapMarkers.entries.iterator()
        while (iterator.hasNext()) {
            val entry = iterator.next()
            if (!serviceLocations.contains(entry.key)) {
                AnimationUtils.fadeOutMarker(entry.value)
                iterator.remove()
            }
        }
    }

    private fun renderLiveData(state: State) {
        if (state.focusedServiceLocation != null) {
            liveDataAdapter?.update(
                listOf(
                    nearbyMapper.map(
                        state.focusedServiceLocation,
                        state.focusedServiceLocationLiveData
                    )
                )
            )
            if (bottomSheetBehavior?.state != BottomSheetBehavior.STATE_COLLAPSED) {
                bottomSheetBehavior?.state = BottomSheetBehavior.STATE_COLLAPSED
            }
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

    private fun drawRailLines() {
        val dartPolyLine0 = PolyUtil.decode(requireContext().resources.getString(R.string.dart_poly_line_0))
        val dartPolyLine1 = PolyUtil.decode(requireContext().resources.getString(R.string.dart_poly_line_1))
        val luasRedPolyLine0 = PolyUtil.decode(requireContext().resources.getString(R.string.luas_red_poly_line_0))
        val luasRedPolyLine1 = PolyUtil.decode(requireContext().resources.getString(R.string.luas_red_poly_line_1))
        val luasRedPolyLine2 = PolyUtil.decode(requireContext().resources.getString(R.string.luas_red_poly_line_2))
        val luasGreenPolyLine0 = PolyUtil.decode(requireContext().resources.getString(R.string.luas_green_poly_line_0))
        val luasGreenPolyLine1 = PolyUtil.decode(requireContext().resources.getString(R.string.luas_green_poly_line_1))
        val luasGreenPolyLine2 = PolyUtil.decode(requireContext().resources.getString(R.string.luas_green_poly_line_2))

        drawPolyLines(dartPolyLine0, R.color.dart_brand1)
        drawPolyLines(dartPolyLine1, R.color.dart_brand1)
        drawPolyLines(luasRedPolyLine0, R.color.luas_red_brand1)
        drawPolyLines(luasRedPolyLine1, R.color.luas_red_brand1)
        drawPolyLines(luasRedPolyLine2, R.color.luas_red_brand1)
        drawPolyLines(luasGreenPolyLine0, R.color.luas_green_brand1)
        drawPolyLines(luasGreenPolyLine1, R.color.luas_green_brand1)
        drawPolyLines(luasGreenPolyLine2, R.color.luas_green_brand1)
    }

    private fun drawPolyLines(polyLine: MutableList<LatLng>, colour: Int) {
        val polyLineOptions = PolylineOptions()
        polyLineOptions.width(8f)
        polyLineOptions.color(ContextCompat.getColor(requireContext(), colour))
        for (point1 in polyLine) {
            polyLineOptions.add(point1)
        }
        googleMap?.addPolyline(polyLineOptions)
    }
}
