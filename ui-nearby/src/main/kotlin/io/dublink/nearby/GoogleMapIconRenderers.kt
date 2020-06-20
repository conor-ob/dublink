package io.dublink.nearby

import android.content.Context
import android.graphics.*
import android.util.TypedValue
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import io.dublink.domain.model.DubLinkServiceLocation
import timber.log.Timber

object GoogleMapIconRenderers {

    fun defaultText(context: Context, serviceLocation: DubLinkServiceLocation): BitmapDescriptor {
        Timber.d("defaultText for ServiceLocation[${serviceLocation.name}]")
        val px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 14f, context.resources.displayMetrics)
        val stkPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        stkPaint.textSize = px
        stkPaint.textAlign = Paint.Align.LEFT
        stkPaint.typeface = Typeface.create("sans-serif-condensed", Typeface.BOLD)
        stkPaint.style = Paint.Style.STROKE
        stkPaint.strokeWidth = 5f
        stkPaint.color = Color.WHITE
        val baseline = -stkPaint.ascent() // ascent() is negative
        val width = (stkPaint.measureText(serviceLocation.defaultName) + 0.5f).toInt()// round
        val height = (baseline + stkPaint.descent() + 0.5f).toInt()
        val image = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(image)
        canvas.drawText(serviceLocation.defaultName, 0f, baseline, stkPaint)

        val fillPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        fillPaint.textSize = px
        fillPaint.color = ContextCompat.getColor(context, R.color.color_on_surface)
        fillPaint.textAlign = Paint.Align.LEFT
        fillPaint.typeface = Typeface.create("sans-serif-condensed", Typeface.NORMAL)
        canvas.drawText(serviceLocation.defaultName, 0f, baseline, fillPaint)
        return BitmapDescriptorFactory.fromBitmap(image)
    }

    fun dublinBikesText(context: Context, serviceLocation: DubLinkServiceLocation): BitmapDescriptor {
        Timber.d("defaultText for ServiceLocation[${serviceLocation.name}]")
        val px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 12f, context.resources.displayMetrics)
        val stkPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        stkPaint.textSize = px
        stkPaint.textAlign = Paint.Align.LEFT
        stkPaint.typeface = Typeface.create("sans-serif-condensed", Typeface.NORMAL)
        stkPaint.color = ContextCompat.getColor(context, R.color.color_on_surface)
        val baseline = -stkPaint.ascent() // ascent() is negative
        val width = (stkPaint.measureText(serviceLocation.defaultName) + 0.5f).toInt()// round
        val height = (baseline + stkPaint.descent() + 0.5f).toInt()
        val image = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(image)
        canvas.drawText(serviceLocation.defaultName, 0f, baseline, stkPaint)
        return BitmapDescriptorFactory.fromBitmap(image)
    }

    fun dublinBusText(context: Context, serviceLocation: DubLinkServiceLocation): BitmapDescriptor {
        Timber.d("defaultText for ServiceLocation[${serviceLocation.name}]")
        val px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 14f, context.resources.displayMetrics)
        val stkPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        stkPaint.textSize = px
        stkPaint.textAlign = Paint.Align.LEFT
        stkPaint.typeface = Typeface.create("sans-serif-condensed", Typeface.NORMAL)
        stkPaint.color = ContextCompat.getColor(context, R.color.color_on_surface)
        val baseline = -stkPaint.ascent() // ascent() is negative
        val width = (stkPaint.measureText(serviceLocation.defaultName) + 0.5f).toInt()// round
        val height = (baseline + stkPaint.descent() + 1.5f).toInt()
        val image = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(image)

        val paint = Paint()
        paint.style = Paint.Style.FILL
        paint.color = ContextCompat.getColor(context, R.color.dublink_blue)
        paint.isAntiAlias = true

        val rectF = RectF(
            0.toFloat(), // left
            0.toFloat(), // top
            canvas.width.toFloat() , // right
            canvas.height.toFloat() // bottom
        )

        canvas.drawRoundRect(rectF, 25f, 25f, paint)
        canvas.drawText(serviceLocation.defaultName, 0f, baseline, stkPaint)
        return BitmapDescriptorFactory.fromBitmap(image)
    }

}
