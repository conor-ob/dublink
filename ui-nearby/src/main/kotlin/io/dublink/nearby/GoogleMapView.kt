package io.dublink.nearby

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import com.google.android.gms.maps.GoogleMapOptions
import com.google.android.gms.maps.MapView

class GoogleMapView : MapView {

    private var mapIsTouched = false

    constructor(context: Context) : super(context)

    constructor(context: Context, attributeSet: AttributeSet) : super(context, attributeSet)

    constructor(context: Context, attributeSet: AttributeSet, i: Int) : super(context, attributeSet, i)

    constructor(context: Context, googleMapOptions: GoogleMapOptions) : super(context, googleMapOptions)

    override fun dispatchTouchEvent(event: MotionEvent?): Boolean {
        when (event?.action) {
            MotionEvent.ACTION_DOWN -> mapIsTouched = true
            MotionEvent.ACTION_UP -> mapIsTouched = false
        }
        return super.dispatchTouchEvent(event)
    }

    fun mapIsTouched() = mapIsTouched
}
