package ie.dublinmapper.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import ie.dublinmapper.R
import ie.dublinmapper.domain.model.ServiceLocation
import ie.dublinmapper.domain.model.dart.DartStation
import ie.dublinmapper.domain.model.dublinbikes.DublinBikesDock
import ie.dublinmapper.domain.model.dublinbus.DublinBusStop
import ie.dublinmapper.domain.model.luas.LuasStop
import timber.log.Timber

object ImageUtils {

    private val bitmapCache = HashMap<Int, BitmapDescriptor>()

    fun drawableToBitmap(context: Context, drawableId: Int) : BitmapDescriptor {
        val cachedValue = bitmapCache[drawableId]
        if (cachedValue != null) {
            Timber.d("Fetching cached Bitmap for drawable[$drawableId]")
            return cachedValue
        }
        Timber.d("Creating new Bitmap for drawable[$drawableId]")
        val vectorDrawable = context.getDrawable(drawableId)
        vectorDrawable!!.setBounds(0, 0, vectorDrawable.intrinsicWidth, vectorDrawable.intrinsicHeight)
        val bitmap = Bitmap.createBitmap(vectorDrawable.intrinsicWidth, vectorDrawable.intrinsicHeight, Bitmap.Config.ARGB_8888)
        vectorDrawable.draw(Canvas(bitmap))
        val bitmapDescriptor = BitmapDescriptorFactory.fromBitmap(bitmap)
        bitmapCache[drawableId] = bitmapDescriptor
        return bitmapDescriptor
    }

    fun drawableResourceIdFromServiceLocation(serviceLocation: ServiceLocation): Int {
        return when (serviceLocation) {
            is DartStation -> R.drawable.ic_map_marker_dart_1
            is DublinBikesDock -> R.drawable.ic_map_marker_dublin_bikes
            is DublinBusStop -> R.drawable.ic_map_marker_dublin_bus
            is LuasStop -> R.drawable.ic_map_marker_luas
            else -> throw IllegalStateException()
        }
    }

}
