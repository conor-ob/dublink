package ie.dublinmapper.view.nearby

import android.content.Context
import android.graphics.*
import android.util.TypedValue
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

class NearbyMapMarkerManager(
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
        drawRailLines()
    }

    fun drawServiceLocations(serviceLocations: List<ServiceLocation>) {
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
                entry.value.isVisible = true
            }
        }
        for (entry in mapTextMarkers) {
            if (!entry.value.isVisible) {
                entry.value.setIcon(iconFactory.getTextIcon(entry.key, currentZoom))
                val anchor = iconFactory.getTextIconAnchor(entry.key, currentZoom)
                entry.value.setAnchor(anchor.first, anchor.second)
                entry.value.isVisible = true
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
//        Timber.d("cleanUp()")
//        mapMarkers.values.forEach { it.remove() }
//        mapMarkers.clear()
    }

    override fun onCameraMove() {
        Timber.d("onCameraMove()")
        val currentZoom = googleMap.cameraPosition.zoom

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

        private val icons: Map<Operator, TreeMap<Float, IconOptions>> by lazy {
            val icons = mutableMapOf<Operator, TreeMap<Float, IconOptions>>()
            val dartIcons = TreeMap<Float, IconOptions>()
            dartIcons[0.0f] = IconOptions(
                id = 0, //TODO find a way to automate this - they must be uniquw
                icon = ImageUtils.drawableToBitmap(context, R.drawable.ic_map_marker_dart_0),
                iconAnchor = Pair(0.5f, 0.5f),
                textIconAnchor = Pair(0.5f, -0.7f),
                textIconVisibility = true
            )
            dartIcons[16.6f] = IconOptions(
                id = 1,
                icon = ImageUtils.drawableToBitmap(context, R.drawable.ic_map_marker_dart_1),
                iconAnchor = Pair(0.5f, 0.9f),
                textIconAnchor = Pair(0.5f, -0.7f),
                textIconVisibility = true
            )
            icons[Operator.DART] = dartIcons
            return@lazy icons
        }

        fun getIconId(serviceLocation: ServiceLocation, zoom: Float): Int {
            return icons[serviceLocation.operator]!!.floorEntry(zoom).value.id
        }

        fun getIcon(serviceLocation: ServiceLocation, zoom: Float): BitmapDescriptor {
            return icons[serviceLocation.operator]!!.floorEntry(zoom).value.icon
        }

        fun getIconAnchor(serviceLocation: ServiceLocation, zoom: Float): Pair<Float, Float> {
            return icons[serviceLocation.operator]!!.floorEntry(zoom).value.iconAnchor
        }

        private val textIconCache = mutableMapOf<String, BitmapDescriptor>()

        fun getTextIcon(serviceLocation: ServiceLocation, zoom: Float): BitmapDescriptor {
            var textIcon = textIconCache[serviceLocation.name]
            if (textIcon == null) {
                textIcon = newTextIcon(serviceLocation)
                textIconCache[serviceLocation.name] = textIcon
            }
            return textIcon
        }

        fun getTextIconAnchor(serviceLocation: ServiceLocation, zoom: Float): Pair<Float, Float> {
            return icons[serviceLocation.operator]!!.floorEntry(zoom).value.textIconAnchor
        }

        fun getTextIconVisibility(serviceLocation: ServiceLocation, zoom: Float): Boolean {
            return icons[serviceLocation.operator]!!.floorEntry(zoom).value.textIconVisibility
        }

        fun newMarkerOptions(serviceLocation: ServiceLocation): MarkerOptions {
            Timber.d("newMarkerOptions")
            val currentZoom = googleMap.cameraPosition.zoom
            val anchor = getIconAnchor(serviceLocation, currentZoom)
            return MarkerOptions()
                .position(LatLng(serviceLocation.coordinate.latitude, serviceLocation.coordinate.longitude))
                .anchor(anchor.first, anchor.second)
                .icon(getIcon(serviceLocation, currentZoom))
        }

        fun newTextMarkerOptions(serviceLocation: ServiceLocation): MarkerOptions {
            Timber.d("newTextMarkerOptions for ServiceLocation[${serviceLocation.name}]")

            val currentZoom = googleMap.cameraPosition.zoom
            val iconAnchor = getTextIconAnchor(serviceLocation, currentZoom)
            return MarkerOptions()
                .position(LatLng(serviceLocation.coordinate.latitude, serviceLocation.coordinate.longitude))
                .anchor(iconAnchor.first, iconAnchor.second)
                .icon(getTextIcon(serviceLocation, currentZoom))
        }

        private fun newTextIcon(serviceLocation: ServiceLocation): BitmapDescriptor {
            Timber.d("newTextIcon for ServiceLocation[${serviceLocation.name}]")
            val px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 14f, context.resources.displayMetrics)
            val stkPaint = Paint(Paint.ANTI_ALIAS_FLAG)
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

            val fillPaint = Paint(Paint.ANTI_ALIAS_FLAG)
            fillPaint.textSize = px
            fillPaint.color = ContextCompat.getColor(context, R.color.text_secondary)
            fillPaint.textAlign = Paint.Align.LEFT
            fillPaint.typeface = Typeface.create("sans-serif-condensed", Typeface.NORMAL)
            canvas.drawText(serviceLocation.name, 0f, baseline, fillPaint)
            return BitmapDescriptorFactory.fromBitmap(image)
        }

    }

    data class IconOptions(
        val id: Int,
        val icon: BitmapDescriptor,
        val iconAnchor: Pair<Float, Float>,
        val textIconAnchor: Pair<Float, Float>,
        val textIconVisibility: Boolean
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
