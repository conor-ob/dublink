package io.dublink.nearby

import android.content.Context
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import com.google.maps.android.PolyUtil
import io.dublink.domain.model.DubLinkDockLocation
import io.dublink.domain.model.DubLinkServiceLocation
import io.dublink.domain.model.DubLinkStopLocation
import io.dublink.nearby.util.AnimationUtils
import io.dublink.nearby.util.ImageUtils
import io.rtpi.api.Coordinate
import io.rtpi.api.Operator
import timber.log.Timber
import java.util.TreeMap
import java.util.UUID

class GoogleMapController(
    private val context: Context
) : GoogleMap.OnCameraMoveListener {

    private val iconFactory = IconFactory()
    private lateinit var googleMap: GoogleMap
    private var previousZoom = 0.0f

    // TODO synchronized map ?
    private val mapMarkers = mutableMapOf<DubLinkServiceLocation, Marker>()
    private val mapTextMarkers = mutableMapOf<DubLinkServiceLocation, Marker>()

    fun getServiceLocation(marker: Marker): DubLinkServiceLocation? {
        for (entry in mapMarkers.entries) {
            if (entry.value == marker) {
                return entry.key
            }
        }
        return null
//
//        val nearest = TreeMap<Double, ServiceLocationUi>()
//        for (serviceLocation in mapMarkers.keys) {
//            nearest[LocationUtils.haversineDistance(serviceLocation.coordinate, coordinate)] = serviceLocation
//        }
//        if (nearest.isEmpty()) {
//            return null
//        }
//        val closest = nearest.iterator().next()
//        if (closest.key < 25.0) {
//            return closest.value
//        }
//        return null
    }

    fun attachGoogleMap(googleMap: GoogleMap) {
        this.googleMap = googleMap
        this.previousZoom = googleMap.cameraPosition.zoom
        drawRailLines()
    }

    fun drawServiceLocations(serviceLocations: Collection<DubLinkServiceLocation>) {
        Timber.d("drawServiceLocations()")
        addNewMarkers(serviceLocations)
        removeOldMarkers(serviceLocations)
        redrawMarkers()
    }

    private fun addNewMarkers(serviceLocations: Collection<DubLinkServiceLocation>) {
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

    private fun removeOldMarkers(serviceLocations: Collection<DubLinkServiceLocation>) {
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

    fun redrawMarkers() {
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

    private fun newMarker(serviceLocation: DubLinkServiceLocation): Marker {
        return googleMap.addMarker(iconFactory.newMarkerOptions(serviceLocation))
    }

    private fun newTextMarker(serviceLocation: DubLinkServiceLocation): Marker {
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

        private val icons: Map<Set<Operator>, TreeMap<Float, IconOptions>> by lazy {
            val icons = mutableMapOf<Set<Operator>, TreeMap<Float, IconOptions>>()
            val aircoachIcons = TreeMap<Float, IconOptions>()
            val busEireannIcons = TreeMap<Float, IconOptions>()
            busEireannIcons[0.0f] = IconOptions(
                icon = ImageUtils.drawableToBitmap(context, R.drawable.ic_map_marker_buseireann_dot),
                iconAnchor = Pair(0.5f, 0.5f),
                textIconAnchor = Pair(0.5f, -0.7f),
                iconVisibility = true,
                textIconVisibility = false,
                textIconRenderer = { context: Context, serviceLocation: DubLinkServiceLocation ->
                    GoogleMapIconRenderers.defaultText(
                        context,
                        serviceLocation
                    )
                }
            )
            busEireannIcons[15.0f] = IconOptions(
                icon = ImageUtils.drawableToBitmap(context, R.drawable.ic_map_marker_bus_eireann),
                iconAnchor = Pair(0.5f, 0.5f),
                textIconAnchor = Pair(0.5f, -0.7f),
                iconVisibility = true,
                textIconVisibility = false,
                textIconRenderer = { context: Context, serviceLocation: DubLinkServiceLocation ->
                    GoogleMapIconRenderers.defaultText(
                        context,
                        serviceLocation
                    )
                }
            )
            val dartIcons = TreeMap<Float, IconOptions>()
            dartIcons[0.0f] = IconOptions(
                icon = ImageUtils.drawableToBitmap(context, R.drawable.ic_map_marker_dart_1),
                iconAnchor = Pair(0.5f, 0.5f),
                textIconAnchor = Pair(0.5f, -0.7f),
                iconVisibility = true,
                textIconVisibility = true,
                textIconRenderer = { context: Context, serviceLocation: DubLinkServiceLocation ->
                    GoogleMapIconRenderers.defaultText(
                        context,
                        serviceLocation
                    )
                }
            )
            dartIcons[16.6f] = IconOptions(
                icon = ImageUtils.drawableToBitmap(context, R.drawable.ic_map_marker_dart_0),
                iconAnchor = Pair(0.5f, 0.8f),
                textIconAnchor = Pair(0.5f, -0.5f),
                iconVisibility = true,
                textIconVisibility = true,
                textIconRenderer = { context: Context, serviceLocation: DubLinkServiceLocation ->
                    GoogleMapIconRenderers.defaultText(
                        context,
                        serviceLocation
                    )
                }
            )
            val dublinBikesIcons = TreeMap<Float, IconOptions>()
            dublinBikesIcons[0.0f] = IconOptions(
                icon = ImageUtils.drawableToBitmap(context, R.drawable.ic_map_marker_dublinbikes_dot),
                iconAnchor = Pair(0.5f, 0.5f),
                textIconAnchor = Pair(0.5f, -0.7f),
                iconVisibility = true,
                textIconVisibility = false,
                textIconRenderer = { context: Context, serviceLocation: DubLinkServiceLocation ->
                    GoogleMapIconRenderers.defaultText(
                        context,
                        serviceLocation
                    )
                }
            )
            dublinBikesIcons[16.0f] = IconOptions(
                icon = ImageUtils.drawableToBitmap(context, R.drawable.ic_map_marker_dublinbikes),
                iconAnchor = Pair(0.5f, 0.9f),
                textIconAnchor = Pair(0.5f, 2.15f),
                iconVisibility = true,
                textIconVisibility = true,
                textIconRenderer = { context: Context, serviceLocation: DubLinkServiceLocation ->
                    GoogleMapIconRenderers.dublinBikesText(
                        context,
                        serviceLocation
                    )
                }
            )
            val dublinBusIcons = TreeMap<Float, IconOptions>()
            dublinBusIcons[0.0f] = IconOptions(
//                icon = ImageUtils.drawableToBitmap(context, R.drawable.ic_map_marker_dublinbus_x),
                icon = ImageUtils.drawableToBitmap(context, R.drawable.ic_map_marker_dublinbus_dot),
                iconAnchor = Pair(0.5f, 0.5f),
                textIconAnchor = Pair(0.5f, -0.7f),
                iconVisibility = true,
                textIconVisibility = false,
                textIconRenderer = { context: Context, serviceLocation: DubLinkServiceLocation ->
                    GoogleMapIconRenderers.defaultText(
                        context,
                        serviceLocation
                    )
                }
            )
            dublinBusIcons[15.4f] = IconOptions(
                icon = ImageUtils.drawableToBitmap(context, R.drawable.ic_map_marker_dublinbus_x1),
                iconAnchor = Pair(0.5f, 0.5f),
                textIconAnchor = Pair(0.5f, -0.7f),
                iconVisibility = true,
                textIconVisibility = false,
                textIconRenderer = { context: Context, serviceLocation: DubLinkServiceLocation ->
                    GoogleMapIconRenderers.defaultText(
                        context,
                        serviceLocation
                    )
                }
            )
            dublinBusIcons[17.97f] = IconOptions(
                icon = ImageUtils.drawableToBitmap(context, R.drawable.ic_map_marker_dublinbus_xx),
                iconAnchor = Pair(0.5f, 0.5f),
                textIconAnchor = Pair(0.5f, -0.7f),
                iconVisibility = false,
                textIconVisibility = true,
                textIconRenderer = { context: Context, serviceLocation: DubLinkServiceLocation ->
                    GoogleMapIconRenderers.dublinBusText(
                        context,
                        serviceLocation
                    )
                }
            )
            val luasIcons = TreeMap<Float, IconOptions>()
            luasIcons[0.0f] = IconOptions(
                icon = ImageUtils.drawableToBitmap(context, R.drawable.ic_map_marker_luas_1),
                iconAnchor = Pair(0.5f, 0.5f),
                textIconAnchor = Pair(0.5f, -0.7f),
                iconVisibility = true,
                textIconVisibility = true,
                textIconRenderer = { context: Context, serviceLocation: DubLinkServiceLocation ->
                    GoogleMapIconRenderers.defaultText(
                        context,
                        serviceLocation
                    )
                }
            )
            luasIcons[16.6f] = IconOptions(
                icon = ImageUtils.drawableToBitmap(context, R.drawable.ic_map_marker_luas_0),
                iconAnchor = Pair(0.5f, 0.7f),
                textIconAnchor = Pair(0.5f, -0.4f),
                iconVisibility = true,
                textIconVisibility = true,
                textIconRenderer = { context: Context, serviceLocation: DubLinkServiceLocation ->
                    GoogleMapIconRenderers.defaultText(
                        context,
                        serviceLocation
                    )
                }
            )
            icons[setOf(Operator.BUS_EIREANN)] = busEireannIcons
            icons[setOf(Operator.DART)] = dartIcons
            icons[setOf(Operator.COMMUTER, Operator.DART)] = dartIcons
            icons[setOf(Operator.COMMUTER, Operator.DART, Operator.INTERCITY)] = dartIcons
            icons[setOf(Operator.DUBLIN_BIKES)] = dublinBikesIcons
            icons[setOf(Operator.DUBLIN_BUS)] = dublinBusIcons
            icons[setOf(Operator.GO_AHEAD)] = dublinBusIcons
            icons[setOf(Operator.DUBLIN_BUS, Operator.GO_AHEAD)] = dublinBusIcons
            icons[setOf(Operator.LUAS)] = luasIcons
            return@lazy icons
        }

        fun getIconId(serviceLocation: DubLinkServiceLocation, zoom: Float): UUID {
            return icons[serviceLocation.service.operators]!!.floorEntry(zoom).value.serviceId
        }

        fun getIcon(serviceLocation: DubLinkServiceLocation, zoom: Float): BitmapDescriptor {
            return icons[serviceLocation.service.operators]!!.floorEntry(zoom).value.icon
        }

        fun getIconAnchor(serviceLocation: DubLinkServiceLocation, zoom: Float): Pair<Float, Float> {
            return icons[serviceLocation.service.operators]!!.floorEntry(zoom).value.iconAnchor
        }

        fun getIconVisibility(serviceLocation: DubLinkServiceLocation, zoom: Float): Boolean {
            return icons[serviceLocation.service.operators]!!.floorEntry(zoom).value.iconVisibility
        }

        //TODO this needs to handle zoom
//        private val textIconCache = mutableMapOf<Pair<String, Float>, BitmapDescriptor>()

        fun getTextIcon(serviceLocation: DubLinkServiceLocation, zoom: Float): BitmapDescriptor {
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

        fun getTextIconAnchor(serviceLocation: DubLinkServiceLocation, zoom: Float): Pair<Float, Float> {
            return icons[serviceLocation.service.operators]!!.floorEntry(zoom).value.textIconAnchor
        }

        fun getTextIconVisibility(serviceLocation: DubLinkServiceLocation, zoom: Float): Boolean {
            return icons[serviceLocation.service.operators]!!.floorEntry(zoom).value.textIconVisibility
        }

        private fun getTextIconRenderer(
            serviceLocation: DubLinkServiceLocation,
            zoom: Float
        ): (Context, DubLinkServiceLocation) -> BitmapDescriptor {
            return icons[serviceLocation.service.operators]!!.floorEntry(zoom).value.textIconRenderer
        }

        fun newMarkerOptions(serviceLocation: DubLinkServiceLocation): MarkerOptions {
            Timber.d("newMarkerOptions")
            val currentZoom = googleMap.cameraPosition.zoom
            val anchor = getIconAnchor(serviceLocation, currentZoom)
            return MarkerOptions()
                .position(LatLng(serviceLocation.coordinate.latitude, serviceLocation.coordinate.longitude))
                .anchor(anchor.first, anchor.second)
                .icon(getIcon(serviceLocation, currentZoom))
                .visible(getIconVisibility(serviceLocation, currentZoom))
                .title("paodjfa")
        }

        fun newTextMarkerOptions(serviceLocation: DubLinkServiceLocation): MarkerOptions {
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
        val serviceId: UUID = UUID.randomUUID(),
        val icon: BitmapDescriptor,
        val iconAnchor: Pair<Float, Float>,
        val textIconAnchor: Pair<Float, Float>,
        val iconVisibility: Boolean,
        val textIconVisibility: Boolean,
        val textIconRenderer: (Context, DubLinkServiceLocation) -> BitmapDescriptor
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

        drawPolyLines(dartPolyLine0, R.color.dart_brand)
        drawPolyLines(dartPolyLine1, R.color.dart_brand)
        drawPolyLines(luasRedPolyLine0, R.color.luas_red_brand)
        drawPolyLines(luasRedPolyLine1, R.color.luas_red_brand)
        drawPolyLines(luasRedPolyLine2, R.color.luas_red_brand)
        drawPolyLines(luasGreenPolyLine0, R.color.luas_green_brand)
        drawPolyLines(luasGreenPolyLine1, R.color.luas_green_brand)
        drawPolyLines(luasGreenPolyLine2, R.color.luas_green_brand)
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
