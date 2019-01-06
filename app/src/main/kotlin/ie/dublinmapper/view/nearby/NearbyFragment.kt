package ie.dublinmapper.view.nearby

import android.graphics.*
import android.graphics.Paint.ANTI_ALIAS_FLAG
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import dagger.android.support.DaggerFragment
import ie.dublinmapper.R
import ie.dublinmapper.domain.model.ServiceLocation
import ie.dublinmapper.domain.model.dart.DartStation
import ie.dublinmapper.util.*
import kotlinx.android.synthetic.main.fragment_nearby.*
import timber.log.Timber
import java.util.*
import javax.inject.Inject

class NearbyFragment : DaggerFragment(), OnMapReadyCallback {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private val nearbyViewModel by lazy { viewModelProvider(viewModelFactory) as NearbyViewModel }
    private var googleMap: GoogleMap? = null
    private lateinit var mapMarkerManager: MapMarkerManager

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

    private fun onNearbyServiceLocationsReceived(serviceLocations: List<ServiceLocation>?) {
        if (serviceLocations == null) {
            return
        }
        debugIconAnchor(serviceLocations)
        mapMarkerManager.drawServiceLocations(serviceLocations)
    }

    private fun debugIconAnchor(serviceLocations: List<ServiceLocation>) {
        val zoom = googleMap?.cameraPosition?.zoom
        val stop = serviceLocations[0].coordinate
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

        mapMarkerManager = MapMarkerManager(googleMap.cameraPosition.zoom)
        googleMap.setOnCameraMoveListener(mapMarkerManager)

        googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(context, R.raw.map_style_no_transit_station))

        restoreCameraPosition(googleMap)

        googleMap.setOnCameraIdleListener {
            googleMap.cameraPosition.target?.apply {
                nearbyViewModel.onCameraMoved(Coordinate(latitude, longitude))
            }
        }

        RailLineDrawer.drawRailLines(googleMap, requireContext())
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

    inner class MapMarkerManager(
        initialZoom: Float
    ) : GoogleMap.OnCameraMoveListener {

        private val dartIcons: TreeMap<Float, BitmapDescriptor> by lazy {
            val map = TreeMap<Float, BitmapDescriptor>()
            map[0f] = ImageUtils.drawableToBitmap(requireContext(), R.drawable.ic_map_marker_dart_0)
            map[16.6f] = ImageUtils.drawableToBitmap(requireContext(), R.drawable.ic_map_marker_dart_1)
            return@lazy map
        }
        private val dartIconAnchors: TreeMap<Float, Pair<Float, Float>> by lazy {
            val map = TreeMap<Float, Pair<Float, Float>>()
            map[0f] = Pair(0.5f, 0.5f)
            map[16.6f] = Pair(0.5f, 0.9f)
            return@lazy map
        }
        private val dartIconVisibility: TreeMap<Float, Boolean> by lazy {
            val map = TreeMap<Float, Boolean>()
            map[0f] = false
            map[12f] = true
            return@lazy map
        }
        private val dartTextIconAnchors: TreeMap<Float, Pair<Float, Float>> by lazy {
            val map = TreeMap<Float, Pair<Float, Float>>()
            map[0f] = Pair(0.5f, -0.7f)
//            map[16.6f] = Pair(0.5f, -0.7f)
            return@lazy map
        }
        private val dartTextIconVisibility: TreeMap<Float, Boolean> by lazy {
            val map = TreeMap<Float, Boolean>()
            map[0f] = true
            map[12f] = true
            return@lazy map
        }

        private val mapMarkers = Collections.synchronizedMap(mutableMapOf<ServiceLocation, Marker>())
        private val mapTextMarkers = Collections.synchronizedMap(mutableMapOf<ServiceLocation, Marker>())
        private var previousZoom = initialZoom

        fun drawServiceLocations(serviceLocations: List<ServiceLocation>) {
            addNewMarkers(serviceLocations)
            removeOldMarkers(serviceLocations)
        }

        private fun addNewMarkers(serviceLocations: Collection<ServiceLocation>) {
            for (serviceLocation in serviceLocations) {
                if (mapMarkers[serviceLocation] == null) {
                    val marker = newMarker(serviceLocation)
                    mapMarkers[serviceLocation] = marker
                    AnimationUtils.fadeInMarker(marker)
                }
                if (mapTextMarkers[serviceLocation] == null) {
                    val marker = newTextMarker(serviceLocation)
                    mapTextMarkers[serviceLocation] = marker
                    AnimationUtils.fadeInMarker(marker)
                }
            }
        }

        private fun removeOldMarkers(serviceLocations: Collection<ServiceLocation>) {
            val iterator = mapMarkers.entries.iterator()
            while (iterator.hasNext()) {
                val entry = iterator.next()
                if (!serviceLocations.contains(entry.key)) {
                    AnimationUtils.fadeOutMarker(entry.value)
                    iterator.remove()
                }
            }
            val textIterator = mapTextMarkers.entries.iterator()
            while (textIterator.hasNext()) {
                val entry = textIterator.next()
                if (!serviceLocations.contains(entry.key)) {
                    AnimationUtils.fadeOutMarker(entry.value)
                    textIterator.remove()
                }
            }
        }

        private fun newMarker(serviceLocation: ServiceLocation): Marker {
            googleMap?.let {
                val currentZoom = it.cameraPosition.zoom
                val anchor = dartIconAnchors.floorEntry(currentZoom).value
                return it.addMarker(
                    MarkerOptions()
                        .position(LatLng(serviceLocation.coordinate.latitude, serviceLocation.coordinate.longitude))
                        .anchor(anchor.first, anchor.second)
                        .title(serviceLocation.name)
                        .icon(ImageUtils.drawableToBitmap(requireContext(), ImageUtils.drawableResourceIdFromServiceLocation(serviceLocation)))
                )
            }
        }

        private fun newTextMarker(serviceLocation: ServiceLocation): Marker {
            val px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 14f, resources.displayMetrics)
            val stkPaint = Paint(ANTI_ALIAS_FLAG)
            stkPaint.textSize = px
            stkPaint.textAlign = Paint.Align.LEFT
            stkPaint.typeface = Typeface.create("sans-serif-condensed", Typeface.BOLD)
            stkPaint.style = Paint.Style.STROKE
            stkPaint.strokeWidth = 5f
            stkPaint.color = Color.WHITE
            val baseline = -stkPaint.ascent() // ascent() is negative
            val width = (stkPaint.measureText(serviceLocation.name) + 0.5f).toInt()// round
            val height = (baseline + stkPaint.descent() + 0.5f).toInt()
            val image = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(image)
            canvas.drawText(serviceLocation.name, 0f, baseline, stkPaint)

            val fillPaint = Paint(ANTI_ALIAS_FLAG)
            fillPaint.textSize = px
            fillPaint.color = ContextCompat.getColor(requireContext(), R.color.text_secondary)
            fillPaint.textAlign = Paint.Align.LEFT
            fillPaint.typeface = Typeface.create("sans-serif-condensed", Typeface.NORMAL)
            canvas.drawText(serviceLocation.name, 0f, baseline, fillPaint)

            googleMap?.let {
                val currentZoom = it.cameraPosition.zoom
                val anchor = dartTextIconAnchors.floorEntry(currentZoom).value
                return it.addMarker(
                    MarkerOptions()
                        .position(LatLng(serviceLocation.coordinate.latitude, serviceLocation.coordinate.longitude))
                        .anchor(anchor.first, anchor.second)
                        .icon(BitmapDescriptorFactory.fromBitmap(image))
                )
            }
        }

        fun cleanUp() {
//        Timber.d("cleanUp()")
//        mapMarkers.values.forEach { it.remove() }
//        mapMarkers.clear()
        }

        override fun onCameraMove() {
            Timber.d("onCameraMove()")
            googleMap?.let {
                val currentZoom = it.cameraPosition.zoom
                for (entry in mapMarkers) {
                    if (entry.key is DartStation) {
                        entry.value.setIcon(dartIcons.floorEntry(currentZoom).value)
                        val anchor = dartIconAnchors.floorEntry(currentZoom).value
                        entry.value.setAnchor(anchor.first, anchor.second)
                        entry.value.isVisible = dartIconVisibility.floorEntry(currentZoom).value
                    }
                }
                for (entry in mapTextMarkers) {
                    if (entry.key is DartStation) {
                        val anchor = dartTextIconAnchors.floorEntry(currentZoom).value
                        entry.value.setAnchor(anchor.first, anchor.second)
                        entry.value.isVisible = dartTextIconVisibility.floorEntry(currentZoom).value
                    }
                }
                previousZoom = currentZoom
            }
        }

    }

}
