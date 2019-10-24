package ie.dublinmapper.model

import android.content.res.ColorStateList
import android.view.View
import androidx.core.content.ContextCompat
import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import ie.dublinmapper.ui.R
import io.rtpi.api.ServiceLocation
import kotlinx.android.synthetic.main.list_item_service_location.*

private const val serviceLocationKey = "key_service_location"

abstract class ServiceLocationItem(
    serviceLocation: ServiceLocation,
    private val isEven: Boolean,
    private val isLast: Boolean
) : Item() {

    init {
        setServiceLocation(serviceLocation)
    }

    override fun getLayout() = R.layout.list_item_service_location

    protected fun bindBackground(viewHolder: ViewHolder, position: Int) {
        if (isLast) {
            viewHolder.rootView.background = viewHolder.itemView.context.getDrawable(R.drawable.shape_rounded_bottom_corners)
        } else {
            viewHolder.rootView.background = viewHolder.itemView.context.getDrawable(R.drawable.shape_rectangle)
        }
        if (isEven) {
            viewHolder.rootView.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(viewHolder.itemView.context, R.color.white))
        } else {
            viewHolder.rootView.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(viewHolder.itemView.context, R.color.grey_100))
        }
    }

    protected fun bindTitle(viewHolder: ViewHolder, title: String, subtitle: String?) {
        if (subtitle == null) {
            viewHolder.mainTitle.text = title
            viewHolder.doubleTitleContainer.visibility = View.GONE
            viewHolder.singleTitleContainer.visibility = View.VISIBLE
        } else {
            viewHolder.title.text = title
            viewHolder.subtitle.text = subtitle
            viewHolder.singleTitleContainer.visibility = View.GONE
            viewHolder.doubleTitleContainer.visibility = View.VISIBLE
        }
    }

    protected fun bindIcon(viewHolder: ViewHolder, drawableId: Int, colourId: Int) {
        viewHolder.serviceIconContainer.setImageResource(drawableId)
        viewHolder.serviceIconContainer.backgroundTintList =
            ColorStateList.valueOf(ContextCompat.getColor(viewHolder.itemView.context, colourId))
    }

    private fun setServiceLocation(serviceLocation: ServiceLocation) {
        extras[serviceLocationKey] = serviceLocation
    }

    protected fun getServiceLocation(): ServiceLocation {
        return extras[serviceLocationKey] as ServiceLocation
    }

    override fun isSameAs(other: com.xwray.groupie.Item<*>?): Boolean {
        return equals(other)
    }

    override fun equals(other: Any?): Boolean {
        if (other is ServiceLocationItem) {
            return getServiceLocation() == other.getServiceLocation()
        }
        return false
    }

    override fun hashCode(): Int {
        return getServiceLocation().hashCode()
    }

}

fun com.xwray.groupie.Item<com.xwray.groupie.ViewHolder>.getServiceLocation(): ServiceLocation {
    return extras[serviceLocationKey] as ServiceLocation
}
