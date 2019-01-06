package ie.dublinmapper.util

import android.content.Context
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.PolylineOptions
import com.google.maps.android.PolyUtil
import ie.dublinmapper.R

object RailLineDrawer {

    fun drawRailLines(googleMap: GoogleMap, context: Context) {
        val dartPolyLine0 = PolyUtil.decode(context.resources.getString(R.string.dart_poly_line_0))
        val dartPolyLine1 = PolyUtil.decode(context.resources.getString(R.string.dart_poly_line_1))
        val luasRedPolyLine0 = PolyUtil.decode(context.resources.getString(R.string.luas_red_poly_line_0))
        val luasRedPolyLine1 = PolyUtil.decode(context.resources.getString(R.string.luas_red_poly_line_1))
        val luasRedPolyLine2 = PolyUtil.decode(context.resources.getString(R.string.luas_red_poly_line_2))
        val luasGreenPolyLine0 = PolyUtil.decode(context.resources.getString(R.string.luas_green_poly_line_0))
        val luasGreenPolyLine1 = PolyUtil.decode(context.resources.getString(R.string.luas_green_poly_line_1))
        val luasGreenPolyLine2 = PolyUtil.decode(context.resources.getString(R.string.luas_green_poly_line_2))

        drawPolyLines(googleMap, context, dartPolyLine0, R.color.dartGreen)
        drawPolyLines(googleMap, context, dartPolyLine1, R.color.dartGreen)
        drawPolyLines(googleMap, context, luasRedPolyLine0, R.color.luasRed)
        drawPolyLines(googleMap, context, luasRedPolyLine1, R.color.luasRed)
        drawPolyLines(googleMap, context, luasRedPolyLine2, R.color.luasRed)
        drawPolyLines(googleMap, context, luasGreenPolyLine0, R.color.luasGreen)
        drawPolyLines(googleMap, context, luasGreenPolyLine1, R.color.luasGreen)
        drawPolyLines(googleMap, context, luasGreenPolyLine2, R.color.luasGreen)
    }

    private fun drawPolyLines(googleMap: GoogleMap, context: Context, polyLine: MutableList<LatLng>, colour: Int) {
        val polyLineOptions = PolylineOptions()
        polyLineOptions.width(8f)
        polyLineOptions.color(ContextCompat.getColor(context, colour))
        for (point1 in polyLine) {
            polyLineOptions.add(point1)
        }
        googleMap.addPolyline(polyLineOptions)
    }
    
}
