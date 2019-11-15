package ie.dublinmapper.model

import android.content.res.ColorStateList
import android.view.View
import androidx.appcompat.view.ContextThemeWrapper
import androidx.core.content.ContextCompat
import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import ie.dublinmapper.ui.R
import io.rtpi.api.ServiceLocation
import kotlinx.android.synthetic.main.list_item_service_location.*
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipDrawable
import io.rtpi.api.Operator
import io.rtpi.api.Route

private const val serviceLocationKey = "key_service_location"

abstract class ServiceLocationItem(
    serviceLocation: ServiceLocation
) : Item() {

    init {
        setServiceLocation(serviceLocation)
    }

    override fun getLayout() = R.layout.list_item_service_location

    protected fun bindIcon(viewHolder: ViewHolder, drawableId: Int, colourId: Int) {
//        viewHolder.serviceIconContainer.setImageResource(drawableId)
//        viewHolder.serviceIconContainer.backgroundTintList =
//            ColorStateList.valueOf(ContextCompat.getColor(viewHolder.itemView.context, colourId))
    }

    protected fun bindTitle(viewHolder: ViewHolder, title: String, subtitle: String) {
        viewHolder.title.text = title
        viewHolder.subtitle.text = subtitle
    }

    protected fun bindRoutes(viewHolder: ViewHolder, routes: List<Route>) {
        viewHolder.routes.removeAllViewsInLayout()
        for (route in routes) {
//            val chip = Chip(ContextThemeWrapper(viewHolder.itemView.context, R.style.ThinnerChip), null, 0)
            val chip = Chip(viewHolder.itemView.context)
            chip.setChipDrawable(ChipDrawable.createFromAttributes(viewHolder.itemView.context, null, 0, R.style.ThinnerChip))
            val (textColour, backgroundColour) = mapColour(route.operator, route.id)
            chip.text = route.id
            chip.setTextColor(ColorStateList.valueOf(viewHolder.itemView.resources.getColor(textColour)))
            chip.setChipBackgroundColorResource(backgroundColour)
//            chip.chipMinHeight = 0f
            viewHolder.routes.addView(chip)
        }
        viewHolder.routes.visibility = View.VISIBLE
    }

    private fun mapColour(operator: Operator, route: String): Pair<Int, Int> {
        return when (operator) {
            Operator.AIRCOACH -> Pair(R.color.white, R.color.aircoachOrange)
            Operator.BUS_EIREANN -> Pair(R.color.white, R.color.busEireannRed)
            Operator.COMMUTER -> Pair(R.color.white, R.color.commuterBlue)
            Operator.DART -> Pair(R.color.white, R.color.dartGreen)
            Operator.DUBLIN_BIKES -> Pair(R.color.white, R.color.dublinBikesTeal)
            Operator.DUBLIN_BUS -> Pair(R.color.text_primary, R.color.dublinBusYellow)
            Operator.GO_AHEAD -> Pair(R.color.white, R.color.goAheadBlue)
            Operator.INTERCITY -> Pair(R.color.text_primary, R.color.intercityYellow)
            Operator.LUAS -> {
                when (route) {
                    "Green", "Green Line" -> Pair(R.color.white, R.color.luasGreen)
                    "Red", "Red Line" -> Pair(R.color.white, R.color.luasRed)
                    else -> Pair(R.color.white, R.color.luasPurple)
                }
            }
        }
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
