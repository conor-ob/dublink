package ie.dublinmapper.view.nearby

import android.content.Context
import android.os.Bundle
import android.preference.PreferenceManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.bluelinelabs.conductor.RouterTransaction
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.floatingactionbutton.FloatingActionButton
import ie.dublinmapper.MvpBaseController
import ie.dublinmapper.R
import ie.dublinmapper.domain.model.*
import ie.dublinmapper.model.ServiceLocationUi
import ie.dublinmapper.util.*
import ie.dublinmapper.view.nearby.livedata.NearbyLiveDataController
import ie.dublinmapper.view.search.SearchController
import timber.log.Timber

class NearbyController(
    args: Bundle
) : MvpBaseController<NearbyView, NearbyPresenter>(args), NearbyView, OnMapReadyCallback {

    private lateinit var googleMapView: GoogleMapView
    private lateinit var googleMap: GoogleMap
    private lateinit var swipeRefresh: SwipeRefreshLayout
    private lateinit var searchFab: FloatingActionButton
    private lateinit var rootView: View

    private lateinit var nearbyLiveDataController: NearbyLiveDataController

    //TODO @Inject
    private lateinit var googleMapController: GoogleMapController

    override val layoutId = R.layout.view_nearby

    override fun createPresenter(): NearbyPresenter {
        return getApplicationComponent().nearbyPresenter()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup): View {
        val view = super.onCreateView(inflater, container)
        rootView = view.findViewById(R.id.view_root)
        setupSwipeRefresh(view)
        setupGoogleMap(view)
        setupSearchFab(view)
        setupLiveDataView(view)
        return view
    }

    private fun setupLiveDataView(view: View) {
        val liveDataView: ViewGroup = view.findViewById(R.id.live_data_view)
        val bottomSheetBehavior = BottomSheetBehavior.from(liveDataView)
        bottomSheetBehavior.apply {
            isHideable = true
            peekHeight = 600
            state = BottomSheetBehavior.STATE_COLLAPSED
        }
        val liveDataRouter = getChildRouter(liveDataView)
        if (!liveDataRouter.hasRootController()) {
            nearbyLiveDataController = NearbyLiveDataController(Bundle.EMPTY)
            liveDataRouter.setRoot(RouterTransaction.with(nearbyLiveDataController))
        }
    }

    private fun setupSearchFab(view: View) {
        searchFab = view.findViewById(R.id.search_fab)
        searchFab.setOnClickListener {
            val searchController = SearchController(Bundle.EMPTY)
//            val changeHandler = SimpleSwapChangeHandler()
            router.pushController(RouterTransaction
                .with(searchController)
                .pushChangeHandler(CircularRevealChangeHandler(searchFab, rootView))
                .popChangeHandler(CircularRevealChangeHandler(searchFab, rootView))
            )
        }
    }

    override fun onContextAvailable(context: Context) {
        super.onContextAvailable(context)
        googleMapController = GoogleMapController(context)
    }

    override fun showLoading() {
        swipeRefresh.isRefreshing = true
    }

    private fun setupSwipeRefresh(view: View) {
        swipeRefresh = view.findViewById(R.id.swipe_refresh)
        swipeRefresh.isEnabled = false
        swipeRefresh.setColorSchemeResources(R.color.dartGreen, R.color.dublinBikesTeal, R.color.dublinBusBlue, R.color.luasPurple)
    }

    private fun setupGoogleMap(view: View) {
        googleMapView = view.findViewById(R.id.google_map_view)
        googleMapView.onCreate(Bundle.EMPTY)
        googleMapView.onStart()
        googleMapView.getMapAsync(this)
    }

    override fun onAttach(view: View) {
        super.onAttach(view)
        googleMapView.onResume()
        presenter.onViewAttached()
    }

    override fun onDetach(view: View) {
        presenter.onViewDetached()
        saveCameraPosition()
        googleMapController.cleanUp()
        googleMapView.onPause()
        super.onDetach(view)
    }

    override fun onDestroyView(view: View) {
        googleMapView.onStop()
        googleMapView.onDestroy()
        super.onDestroyView(view)
    }

    override fun onMapReady(googleMap: GoogleMap?) {
        if (view == null || googleMap == null) {
            return
        }
        this.googleMap = googleMap
        googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(requireContext(), R.raw.google_map_style))
        restoreCameraPosition(googleMap)
        googleMapController.attachGoogleMap(googleMap)
        googleMap.setOnCameraMoveListener(googleMapController)
        googleMap.setOnCameraIdleListener {
            Timber.d("onCameraIdle")
            googleMap.cameraPosition.target?.apply {
                presenter.onCameraMoved(Coordinate(latitude, longitude))
                val serviceLocation = googleMapController.getServiceLocation(Coordinate(latitude, longitude))
                if (serviceLocation != null) {
                    nearbyLiveDataController.focusOnServiceLocation(serviceLocation)
                    if (LocationUtils.haversineDistance(Coordinate(latitude, longitude), serviceLocation.coordinate) > 10.0) {
                        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(CameraPosition.Builder()
                            .target(LatLng(serviceLocation.coordinate.latitude, serviceLocation.coordinate.longitude))
                            .zoom(googleMap.cameraPosition.zoom)
                            .build()))
                    }
                }
            }
        }
    }

    override fun showServiceLocations(serviceLocations: Collection<ServiceLocationUi>) {
        if (CollectionUtils.isNullOrEmpty(serviceLocations)) {
            return
        }

        if (containsAllTypes(serviceLocations)) {
            swipeRefresh.isRefreshing = false
        }

//        debugIconAnchor(serviceLocations)
        googleMapController.drawServiceLocations(serviceLocations)
    }

    private fun containsAllTypes(serviceLocations: Collection<ServiceLocationUi>): Boolean {
        val test1 = serviceLocations.find { it.serviceLocation is DartStation }
        val test2 = serviceLocations.find { it.serviceLocation is DublinBikesDock }
        val test3 = serviceLocations.find { it.serviceLocation is DublinBusStop }
        val test4 = serviceLocations.find { it.serviceLocation is LuasStop }
        return test1 != null && test2 != null && test3 != null && test4 != null
    }

    //TODO check why this is forces onCameraIdle to be called indefinitely
    private fun debugIconAnchor(serviceLocations: Collection<ServiceLocation>) {
        val zoom = googleMap.cameraPosition?.zoom
        val stop = serviceLocations.iterator().next().coordinate
        googleMap.animateCamera(
            CameraUpdateFactory.newCameraPosition(
                CameraPosition.fromLatLngZoom(LatLng(stop.latitude, stop.longitude), zoom!!)
            )
        )
    }

    //TODO temporary
    private fun restoreCameraPosition(googleMap: GoogleMap) {
        val preferences = PreferenceManager.getDefaultSharedPreferences(requireContext())
        val latitude = preferences.getFloat(requireContext().resources.getString(R.string.pref_key_last_location_latitude), 53.344517f)
        val longitude = preferences.getFloat(requireContext().resources.getString(R.string.pref_key_last_location_longitude), -6.260465f)
        val zoom = preferences.getFloat(requireContext().resources.getString(R.string.pref_key_last_location_zoom), 16f)
        googleMap.moveCamera(
            CameraUpdateFactory.newCameraPosition(
                CameraPosition.fromLatLngZoom(
                    LatLng(latitude.toDouble(), longitude.toDouble()), zoom
                )
            )
        )
    }

    //TODO temporary
    private fun saveCameraPosition() {
        googleMap.cameraPosition?.apply {
            val preferences = PreferenceManager.getDefaultSharedPreferences(requireContext())
            preferences.edit()
                .putFloat(requireContext().resources.getString(R.string.pref_key_last_location_latitude), target.latitude.toFloat())
                .putFloat(requireContext().resources.getString(R.string.pref_key_last_location_longitude), target.longitude.toFloat())
                .putFloat(requireContext().resources.getString(R.string.pref_key_last_location_zoom), zoom)
                .apply()
        }
    }

}
