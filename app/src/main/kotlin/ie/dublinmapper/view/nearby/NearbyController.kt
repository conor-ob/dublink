package ie.dublinmapper.view.nearby

import android.content.Context
import android.os.Bundle
import android.preference.PreferenceManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.bluelinelabs.conductor.RouterTransaction
import com.bluelinelabs.conductor.changehandler.FadeChangeHandler
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.material.floatingactionbutton.FloatingActionButton
import ie.dublinmapper.MvpBaseController
import ie.dublinmapper.R
import ie.dublinmapper.domain.model.*
import ie.dublinmapper.util.CollectionUtils
import ie.dublinmapper.util.Coordinate
import ie.dublinmapper.view.search.SearchController
import timber.log.Timber

class NearbyController(
    args: Bundle
) : MvpBaseController<NearbyView, NearbyPresenter>(args), NearbyView, OnMapReadyCallback {

    private lateinit var googleMapView: GoogleMapView
    private lateinit var googleMap: GoogleMap
    private lateinit var swipeRefresh: SwipeRefreshLayout

    //TODO @Inject
    private lateinit var googleMapController: GoogleMapController

    override fun layoutId() = R.layout.view_nearby

    override fun createPresenter(): NearbyPresenter {
        return requireApplicationComponent().nearbyPresenter()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup): View {
        val view = super.onCreateView(inflater, container)
        setupSwipeRefresh(view)
        setupGoogleMap(view)
        setupSearchFab(view)
        return view
    }

    private fun setupSearchFab(view: View) {
        val searchFab: FloatingActionButton = view.findViewById(R.id.search_fab)
        searchFab.setOnClickListener {
            router.pushController(RouterTransaction
                .with(SearchController(Bundle.EMPTY))
                .pushChangeHandler(FadeChangeHandler(500L))
                .popChangeHandler(FadeChangeHandler(500L))
            )
        }
    }

    override fun onContextAvailable(context: Context) {
        super.onContextAvailable(context)
        googleMapController = GoogleMapController(context)
    }

    private fun setupSwipeRefresh(view: View) {
        swipeRefresh = view.findViewById(R.id.swipe_refresh)
        swipeRefresh.isEnabled = false
        swipeRefresh.setColorSchemeResources(R.color.dartGreen, R.color.dublinBikesTeal, R.color.dublinBusBlue, R.color.luasPurple)
        swipeRefresh.isRefreshing = true
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
            }
        }
    }

    override fun showServiceLocations(serviceLocations: Collection<ServiceLocation>) {
        if (CollectionUtils.isNullOrEmpty(serviceLocations)) {
            return
        }

        if (containsAllTypes(serviceLocations)) {
            swipeRefresh.isRefreshing = false
        }

//        debugIconAnchor(serviceLocations)
        googleMapController.drawServiceLocations(serviceLocations)
    }

    private fun containsAllTypes(serviceLocations: Collection<ServiceLocation>): Boolean {
        val test1 = serviceLocations.find { it is DartStation }
        val test2 = serviceLocations.find { it is DublinBikesDock }
        val test3 = serviceLocations.find { it is DublinBusStop }
        val test4 = serviceLocations.find { it is LuasStop }
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
