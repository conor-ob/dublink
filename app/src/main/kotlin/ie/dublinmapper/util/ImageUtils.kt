package ie.dublinmapper.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
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

}
