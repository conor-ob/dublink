package ie.dublinmapper.util

import android.content.Context
import android.util.TypedValue

fun Float.dipToPx(context: Context): Float = TypedValue.applyDimension(
    TypedValue.COMPLEX_UNIT_DIP,
    this,
    context.resources.displayMetrics
)
