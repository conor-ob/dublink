package ie.dublinmapper.view.nearby

import android.os.Bundle
import android.preference.PreferenceManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import dagger.android.support.DaggerFragment
import ie.dublinmapper.R
import ie.dublinmapper.domain.model.ServiceLocation
import ie.dublinmapper.util.CollectionUtils
import ie.dublinmapper.util.Coordinate
import ie.dublinmapper.util.viewModelProvider
import kotlinx.android.synthetic.main.fragment_nearby.*
import timber.log.Timber
import javax.inject.Inject

class NearbyFragment : DaggerFragment(), OnMapReadyCallback {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    @Inject
    lateinit var mapMarkerManager: NearbyMapMarkerManager
    private val nearbyViewModel by lazy { viewModelProvider(viewModelFactory) as NearbyViewModel }
    private var googleMap: GoogleMap? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_nearby, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val mapFragment = childFragmentManager.findFragmentById(R.id.google_map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        search_fab.setOnClickListener {
            val action = NearbyFragmentDirections.onSearchPressed()
            findNavController().navigate(action)
        }

        nearbyViewModel.nearbyServiceLocations.observe(this, Observer {
            onNearbyServiceLocationsReceived(it)
        })
    }

    private fun onNearbyServiceLocationsReceived(serviceLocations: Collection<ServiceLocation>?) {
        if (CollectionUtils.isNullOrEmpty(serviceLocations)) {
            return
        }
//        debugIconAnchor(serviceLocations)
        mapMarkerManager.drawServiceLocations(serviceLocations!!)
    }

    //TODO check why this is forces onCameraIdle to be called indefinitely
    private fun debugIconAnchor(serviceLocations: Collection<ServiceLocation>) {
        val zoom = googleMap?.cameraPosition?.zoom
        val stop = serviceLocations.iterator().next().coordinate
        googleMap?.animateCamera(
            CameraUpdateFactory.newCameraPosition(
                CameraPosition.fromLatLngZoom(LatLng(stop.latitude, stop.longitude), zoom!!)
            )
        )
    }

    override fun onMapReady(googleMap: GoogleMap?) {
        if (view == null || googleMap == null) {
            return
        }
        this.googleMap = googleMap

        mapMarkerManager.attachGoogleMap(googleMap)
        googleMap.setOnCameraMoveListener(mapMarkerManager)

        googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(context, R.raw.map_style_no_transit_station))

        restoreCameraPosition(googleMap)

        googleMap.setOnCameraIdleListener {
            Timber.d("onCameraIdle")
            googleMap.cameraPosition.target?.apply {
                nearbyViewModel.onCameraMoved(Coordinate(latitude, longitude))
            }
        }
    }

    override fun onPause() {
        Timber.d("onPause()")
        saveCameraPosition()
        mapMarkerManager.cleanUp()
        super.onPause()
    }

    //TODO temporary
    private fun restoreCameraPosition(googleMap: GoogleMap) {
        val preferences = PreferenceManager.getDefaultSharedPreferences(this@NearbyFragment.context)
        val latitude = preferences.getFloat(resources.getString(R.string.pref_key_last_location_latitude), 53.344517f)
        val longitude = preferences.getFloat(resources.getString(R.string.pref_key_last_location_longitude), -6.260465f)
        val zoom = preferences.getFloat(resources.getString(R.string.pref_key_last_location_zoom), 16f)
        googleMap.moveCamera(
            CameraUpdateFactory.newCameraPosition(
                CameraPosition.fromLatLngZoom(LatLng(latitude.toDouble(), longitude.toDouble()), zoom
                )
            )
        )
    }

    //TODO temporary
    private fun saveCameraPosition() {
        googleMap?.cameraPosition?.apply {
            val preferences = PreferenceManager.getDefaultSharedPreferences(this@NearbyFragment.context)
            preferences.edit()
                .putFloat(this@NearbyFragment.resources.getString(R.string.pref_key_last_location_latitude), target.latitude.toFloat())
                .putFloat(this@NearbyFragment.resources.getString(R.string.pref_key_last_location_longitude), target.longitude.toFloat())
                .putFloat(this@NearbyFragment.resources.getString(R.string.pref_key_last_location_zoom), zoom)
                .apply()
        }
    }

}
