package ie.dublinmapper.view.nearby

import android.content.Context
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.*
import com.google.maps.android.PolyUtil
import ie.dublinmapper.R
import ie.dublinmapper.domain.model.ServiceLocation
import ie.dublinmapper.util.AnimationUtils
import ie.dublinmapper.util.ImageUtils
import ie.dublinmapper.util.Operator
import timber.log.Timber
import java.util.*

class GoogleMapController(
    private val context: Context
) : GoogleMap.OnCameraMoveListener {

    private val iconFactory = IconFactory()
    private lateinit var googleMap: GoogleMap
    private var previousZoom = 0.0f

    private val mapMarkers = Collections.synchronizedMap(mutableMapOf<ServiceLocation, Marker>())
    private val mapTextMarkers = Collections.synchronizedMap(mutableMapOf<ServiceLocation, Marker>())

    fun attachGoogleMap(googleMap: GoogleMap) {
        this.googleMap = googleMap
        this.previousZoom = googleMap.cameraPosition.zoom
//        drawRailLines()
    }

    fun drawServiceLocations(serviceLocations: Collection<ServiceLocation>) {
        Timber.d("drawServiceLocations()")
        addNewMarkers(serviceLocations)
        removeOldMarkers(serviceLocations)
        redrawMarkers()
    }

    private fun addNewMarkers(serviceLocations: Collection<ServiceLocation>) {
        for (serviceLocation in serviceLocations) {
            if (mapMarkers[serviceLocation] == null) {
                val marker = newMarker(serviceLocation)
                mapMarkers[serviceLocation] = marker
                AnimationUtils.fadeInMarker(marker)
            }
        }
        for (serviceLocation in serviceLocations) {
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

    private fun redrawMarkers() {
        val currentZoom = googleMap.cameraPosition.zoom
        for (entry in mapMarkers) {
            if (!entry.value.isVisible) {
                entry.value.setIcon(iconFactory.getIcon(entry.key, currentZoom))
                val anchor = iconFactory.getIconAnchor(entry.key, currentZoom)
                entry.value.setAnchor(anchor.first, anchor.second)
                entry.value.isVisible = iconFactory.getIconVisibility(entry.key, googleMap.cameraPosition.zoom)
            }
        }
        for (entry in mapTextMarkers) {
            if (!entry.value.isVisible) {
                entry.value.setIcon(iconFactory.getTextIcon(entry.key, currentZoom))
                val anchor = iconFactory.getTextIconAnchor(entry.key, currentZoom)
                entry.value.setAnchor(anchor.first, anchor.second)
                entry.value.isVisible = iconFactory.getTextIconVisibility(entry.key, googleMap.cameraPosition.zoom)
            }
        }
    }

    private fun newMarker(serviceLocation: ServiceLocation): Marker {
        return googleMap.addMarker(iconFactory.newMarkerOptions(serviceLocation))
    }

    private fun newTextMarker(serviceLocation: ServiceLocation): Marker {
        return googleMap.addMarker(iconFactory.newTextMarkerOptions(serviceLocation))
    }

    fun cleanUp() {
        Timber.d("cleanUp()")
        mapMarkers.values.forEach { it.remove() }
        mapMarkers.clear()
        mapTextMarkers.values.forEach { it.remove() }
        mapTextMarkers.clear()
        previousZoom = 0.0f
    }

    override fun onCameraMove() {
        val currentZoom = googleMap.cameraPosition.zoom
        Timber.d("onCameraMove() zoom=$currentZoom")

        for (entry in mapMarkers) {
            val previous = iconFactory.getIconId(entry.key, previousZoom)
            val current = iconFactory.getIconId(entry.key, currentZoom)
            if (previous != current) {
                entry.value.isVisible = false
            }
        }

        for (entry in mapTextMarkers) {
            val previous = iconFactory.getIconId(entry.key, previousZoom)
            val current = iconFactory.getIconId(entry.key, currentZoom)
            if (previous != current) {
                entry.value.isVisible = false
            }
        }

        previousZoom = currentZoom
    }

    inner class IconFactory {

        private val icons: Map<EnumSet<Operator>, TreeMap<Float, IconOptions>> by lazy {
            val icons = mutableMapOf<EnumSet<Operator>, TreeMap<Float, IconOptions>>()
            val dartIcons = TreeMap<Float, IconOptions>()
            dartIcons[0.0f] = IconOptions(
                icon = ImageUtils.drawableToBitmap(context, R.drawable.ic_map_marker_dart_1),
                iconAnchor = Pair(0.5f, 0.5f),
                textIconAnchor = Pair(0.5f, -0.7f),
                iconVisibility = true,
                textIconVisibility = true,
                textIconRenderer = { context: Context, serviceLocation: ServiceLocation -> GoogleMapIconRenderers.defaultText(context, serviceLocation) }
            )
            dartIcons[16.6f] = IconOptions(
                icon = ImageUtils.drawableToBitmap(context, R.drawable.ic_map_marker_dart_0),
                iconAnchor = Pair(0.5f, 0.8f),
                textIconAnchor = Pair(0.5f, -0.5f),
                iconVisibility = true,
                textIconVisibility = true,
                textIconRenderer = { context: Context, serviceLocation: ServiceLocation -> GoogleMapIconRenderers.defaultText(context, serviceLocation) }
            )
            val dublinBikesIcons = TreeMap<Float, IconOptions>()
            dublinBikesIcons[0.0f] = IconOptions(
                icon = ImageUtils.drawableToBitmap(context, R.drawable.ic_map_marker_dublinbikes_x),
                iconAnchor = Pair(0.5f, 0.5f),
                textIconAnchor = Pair(0.5f, -0.7f),
                iconVisibility = false,
                textIconVisibility = false,
                textIconRenderer = { context: Context, serviceLocation: ServiceLocation -> GoogleMapIconRenderers.defaultText(context, serviceLocation) }
            )
            dublinBikesIcons[16.0f] = IconOptions(
                icon = ImageUtils.drawableToBitmap(context, R.drawable.ic_map_marker_dublinbikes_x),
                iconAnchor = Pair(0.5f, 0.9f),
                textIconAnchor = Pair(0.5f, 2.15f),
                iconVisibility = true,
                textIconVisibility = true,
                textIconRenderer = { context: Context, serviceLocation: ServiceLocation -> GoogleMapIconRenderers.dublinBikesText(context, serviceLocation) }
            )
            val dublinBusIcons = TreeMap<Float, IconOptions>()
            dublinBusIcons[0.0f] = IconOptions(
                icon = ImageUtils.drawableToBitmap(context, R.drawable.ic_map_marker_dublinbus_x),
                iconAnchor = Pair(0.5f, 0.5f),
                textIconAnchor = Pair(0.5f, -0.7f),
                iconVisibility = true,
                textIconVisibility = false,
                textIconRenderer = { context: Context, serviceLocation: ServiceLocation -> GoogleMapIconRenderers.defaultText(context, serviceLocation) }
            )
            dublinBusIcons[15.4f] = IconOptions(
                icon = ImageUtils.drawableToBitmap(context, R.drawable.ic_map_marker_dublinbus_x1),
                iconAnchor = Pair(0.5f, 0.5f),
                textIconAnchor = Pair(0.5f, -0.7f),
                iconVisibility = true,
                textIconVisibility = false,
                textIconRenderer = { context: Context, serviceLocation: ServiceLocation -> GoogleMapIconRenderers.defaultText(context, serviceLocation) }
            )
            dublinBusIcons[17.97f] = IconOptions(
                icon = ImageUtils.drawableToBitmap(context, R.drawable.ic_map_marker_dublinbus_xx),
                iconAnchor = Pair(0.5f, 0.5f),
                textIconAnchor = Pair(0.5f, -0.7f),
                iconVisibility = false,
                textIconVisibility = true,
                textIconRenderer = { context: Context, serviceLocation: ServiceLocation -> GoogleMapIconRenderers.dublinBusText(context, serviceLocation) }
            )
            val luasIcons = TreeMap<Float, IconOptions>()
            luasIcons[0.0f] = IconOptions(
                icon = ImageUtils.drawableToBitmap(context, R.drawable.ic_map_marker_luas_1),
                iconAnchor = Pair(0.5f, 0.5f),
                textIconAnchor = Pair(0.5f, -0.7f),
                iconVisibility = true,
                textIconVisibility = true,
                textIconRenderer = { context: Context, serviceLocation: ServiceLocation -> GoogleMapIconRenderers.defaultText(context, serviceLocation) }
            )
            luasIcons[16.6f] = IconOptions(
                icon = ImageUtils.drawableToBitmap(context, R.drawable.ic_map_marker_luas_0),
                iconAnchor = Pair(0.5f, 0.7f),
                textIconAnchor = Pair(0.5f, -0.4f),
                iconVisibility = true,
                textIconVisibility = true,
                textIconRenderer = { context: Context, serviceLocation: ServiceLocation -> GoogleMapIconRenderers.defaultText(context, serviceLocation) }
            )
            icons[Operator.dart()] = dartIcons
            icons[Operator.dublinBikes()] = dublinBikesIcons
            icons[Operator.dublinBus()] = dublinBusIcons
            icons[Operator.luas()] = luasIcons
            return@lazy icons
        }

        fun getIconId(serviceLocation: ServiceLocation, zoom: Float): UUID {
            return icons[serviceLocation.operators]!!.floorEntry(zoom).value.id
        }

        fun getIcon(serviceLocation: ServiceLocation, zoom: Float): BitmapDescriptor {
            return icons[serviceLocation.operators]!!.floorEntry(zoom).value.icon
        }

        fun getIconAnchor(serviceLocation: ServiceLocation, zoom: Float): Pair<Float, Float> {
            return icons[serviceLocation.operators]!!.floorEntry(zoom).value.iconAnchor
        }

        fun getIconVisibility(serviceLocation: ServiceLocation, zoom: Float): Boolean {
            return icons[serviceLocation.operators]!!.floorEntry(zoom).value.iconVisibility
        }

        //TODO this needs to handle zoom
//        private val textIconCache = mutableMapOf<Pair<String, Float>, BitmapDescriptor>()

        fun getTextIcon(serviceLocation: ServiceLocation, zoom: Float): BitmapDescriptor {
//            var textIcon = textIconCache[Pair(serviceLocation.mapIconText, zoom)]
//            if (textIcon == null) {
//                val renderer = getTextIconRenderer(serviceLocation, zoom)
//                textIcon = renderer.invoke(context, serviceLocation)
//                textIconCache[PairserviceLocation.mapIconText] = textIcon
//            }
//            return textIcon
            val renderer = getTextIconRenderer(serviceLocation, zoom)
            return renderer.invoke(context, serviceLocation)
        }

        fun getTextIconAnchor(serviceLocation: ServiceLocation, zoom: Float): Pair<Float, Float> {
            return icons[serviceLocation.operators]!!.floorEntry(zoom).value.textIconAnchor
        }

        fun getTextIconVisibility(serviceLocation: ServiceLocation, zoom: Float): Boolean {
            return icons[serviceLocation.operators]!!.floorEntry(zoom).value.textIconVisibility
        }

        private fun getTextIconRenderer(serviceLocation: ServiceLocation, zoom: Float): (Context, ServiceLocation) -> BitmapDescriptor {
            return icons[serviceLocation.operators]!!.floorEntry(zoom).value.textIconRenderer
        }

        fun newMarkerOptions(serviceLocation: ServiceLocation): MarkerOptions {
            Timber.d("newMarkerOptions")
            val currentZoom = googleMap.cameraPosition.zoom
            val anchor = getIconAnchor(serviceLocation, currentZoom)
            return MarkerOptions()
                .position(LatLng(serviceLocation.coordinate.latitude, serviceLocation.coordinate.longitude))
                .anchor(anchor.first, anchor.second)
                .icon(getIcon(serviceLocation, currentZoom))
                .visible(getIconVisibility(serviceLocation, currentZoom))
        }

        fun newTextMarkerOptions(serviceLocation: ServiceLocation): MarkerOptions {
            Timber.d("newTextMarkerOptions for ServiceLocation[${serviceLocation.name}]")

            val currentZoom = googleMap.cameraPosition.zoom
            val iconAnchor = getTextIconAnchor(serviceLocation, currentZoom)
            return MarkerOptions()
                .position(LatLng(serviceLocation.coordinate.latitude, serviceLocation.coordinate.longitude))
                .anchor(iconAnchor.first, iconAnchor.second)
                .icon(getTextIcon(serviceLocation, currentZoom))
                .visible(getTextIconVisibility(serviceLocation, currentZoom))
        }

    }

    data class IconOptions(
        val id: UUID = UUID.randomUUID(),
        val icon: BitmapDescriptor,
        val iconAnchor: Pair<Float, Float>,
        val textIconAnchor: Pair<Float, Float>,
        val iconVisibility: Boolean,
        val textIconVisibility: Boolean,
        val textIconRenderer: (Context, ServiceLocation) -> BitmapDescriptor
    )

    private fun drawRailLines() {
        val dartPolyLine0 = PolyUtil.decode(context.resources.getString(R.string.dart_poly_line_0))
        val dartPolyLine1 = PolyUtil.decode(context.resources.getString(R.string.dart_poly_line_1))
        val luasRedPolyLine0 = PolyUtil.decode(context.resources.getString(R.string.luas_red_poly_line_0))
        val luasRedPolyLine1 = PolyUtil.decode(context.resources.getString(R.string.luas_red_poly_line_1))
        val luasRedPolyLine2 = PolyUtil.decode(context.resources.getString(R.string.luas_red_poly_line_2))
        val luasGreenPolyLine0 = PolyUtil.decode(context.resources.getString(R.string.luas_green_poly_line_0))
        val luasGreenPolyLine1 = PolyUtil.decode(context.resources.getString(R.string.luas_green_poly_line_1))
        val luasGreenPolyLine2 = PolyUtil.decode(context.resources.getString(R.string.luas_green_poly_line_2))

        drawPolyLines(dartPolyLine0, R.color.dartGreen)
        drawPolyLines(dartPolyLine1, R.color.dartGreen)
        drawPolyLines(luasRedPolyLine0, R.color.luasRed)
        drawPolyLines(luasRedPolyLine1, R.color.luasRed)
        drawPolyLines(luasRedPolyLine2, R.color.luasRed)
        drawPolyLines(luasGreenPolyLine0, R.color.luasGreen)
        drawPolyLines(luasGreenPolyLine1, R.color.luasGreen)
        drawPolyLines(luasGreenPolyLine2, R.color.luasGreen)
    }

    private fun drawPolyLines(polyLine: MutableList<LatLng>, colour: Int) {
        val polyLineOptions = PolylineOptions()
        polyLineOptions.width(8f)
        polyLineOptions.color(ContextCompat.getColor(context, colour))
        for (point1 in polyLine) {
            polyLineOptions.add(point1)
        }
        googleMap.addPolyline(polyLineOptions)
    }

}