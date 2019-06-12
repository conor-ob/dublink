package ie.dublinmapper.view.nearby.map

import android.content.Context
import android.os.Bundle
import android.preference.PreferenceManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import ie.dublinmapper.view.MvpBaseController
import ie.dublinmapper.R
import ie.dublinmapper.domain.model.ServiceLocation
import ie.dublinmapper.util.CollectionUtils
import ie.dublinmapper.util.Coordinate
import ie.dublinmapper.util.getApplicationComponent
import ie.dublinmapper.util.requireContext
import ie.dublinmapper.view.nearby.HomeController
import kotlinx.android.synthetic.main.view_nearby_map.view.*
import timber.log.Timber

class NearbyMapController(
    args: Bundle
) : MvpBaseController<NearbyMapView, NearbyMapPresenter>(args), NearbyMapView, OnMapReadyCallback {

    private lateinit var googleMapView: GoogleMapView
    private lateinit var googleMap: GoogleMap
    //TODO @Inject
    private lateinit var googleMapController: GoogleMapController

    override val styleId = R.color.luasPurple

    override val layoutId = R.layout.view_nearby_map

    override fun createPresenter(): NearbyMapPresenter {
        return getApplicationComponent().nearbyMapPresenter()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup): View {
        val view = super.onCreateView(inflater, container)
        setupSwipeRefresh(view)
        setupGoogleMap(view)
        return view
    }

    private fun setupSwipeRefresh(view: View) {
        view.swipe_refresh.isEnabled = false
        view.swipe_refresh.setColorSchemeResources(
            R.color.dartGreen,
            R.color.dublinBikesTeal,
            R.color.goAheadBlue,
            R.color.luasPurple
        )
    }

    private fun setupGoogleMap(view: View) {
        googleMapView = view.findViewById(R.id.google_map_view)
        googleMapView.onCreate(Bundle.EMPTY)
        googleMapView.onStart()
        googleMapView.getMapAsync(this)
    }

    override fun onContextAvailable(context: Context) {
        super.onContextAvailable(context)
        googleMapController = GoogleMapController(context)
    }

    override fun onAttach(view: View) {
        super.onAttach(view)
        googleMapView.onResume()
    }

    override fun onDetach(view: View) {
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
//                val serviceLocation = googleMapController.getServiceLocation(Coordinate(latitude, longitude))
//                if (serviceLocation != null) {
//                    nearbyLiveDataController.focusOnServiceLocation(serviceLocation)
//                    if (LocationUtils.haversineDistance(Coordinate(latitude, longitude), serviceLocation.coordinate) > 10.0) {
//                        googleMap.animateCamera(
//                            CameraUpdateFactory.newCameraPosition(
//                                CameraPosition.Builder()
//                                    .target(LatLng(serviceLocation.coordinate.latitude, serviceLocation.coordinate.longitude))
//                                    .zoom(googleMap.cameraPosition.zoom)
//                                    .build()))
//                    }
//                }
            }
        }
        presenter.onMapReady()
    }

    override fun render(viewModel: NearbyMapViewModel) {
        view?.swipe_refresh?.isRefreshing = viewModel.isLoading
        if (viewModel.serviceLocation != null) {
//            googleMap.animateCamera(
//                CameraUpdateFactory.newCameraPosition(
//                    CameraPosition.Builder()
//                        .target(
//                            LatLng(
//                                viewModel.serviceLocation.coordinate.latitude,
//                                viewModel.serviceLocation.coordinate.longitude
//                            )
//                        )
//                        .zoom(googleMap.cameraPosition.zoom)
//                        .build()
//                )
//            )
        }
        if (viewModel.isMapReady && CollectionUtils.isNotNullOrEmpty(viewModel.serviceLocations)) {
            //        debugIconAnchor(serviceLocations)
            googleMapController.drawServiceLocations(viewModel.serviceLocations)
        }
        (targetController as HomeController).focusOnServiceLocation(viewModel.serviceLocation)
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
        val latitude = preferences.getFloat(
            requireContext().resources.getString(R.string.pref_key_last_location_latitude),
            53.344517f
        )
        val longitude = preferences.getFloat(
            requireContext().resources.getString(R.string.pref_key_last_location_longitude),
            -6.260465f
        )
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
                .putFloat(
                    requireContext().resources.getString(R.string.pref_key_last_location_latitude),
                    target.latitude.toFloat()
                )
                .putFloat(
                    requireContext().resources.getString(R.string.pref_key_last_location_longitude),
                    target.longitude.toFloat()
                )
                .putFloat(requireContext().resources.getString(R.string.pref_key_last_location_zoom), zoom)
                .apply()
        }
    }

}
