package ie.dublinmapper.model

import android.content.res.ColorStateList
import android.view.View
import androidx.core.content.ContextCompat
import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import ie.dublinmapper.ui.R
import io.rtpi.api.ServiceLocation
import kotlinx.android.synthetic.main.list_item_service_location.*
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipDrawable
import ie.dublinmapper.domain.model.getName
import io.rtpi.api.Operator
import io.rtpi.api.Route
import io.rtpi.api.Service
import kotlin.math.round

private const val serviceLocationKey = "key_service_location"

class ServiceLocationItem(
    private val serviceLocation: ServiceLocation,
    private val icon: Int,
    private val routes: List<Route>?,
    private val walkDistance: Double?
) : Item() {

    init {
        setServiceLocation(serviceLocation)
    }

    override fun getLayout() = R.layout.list_item_service_location

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        bindTitle(viewHolder)
        bindIcon(viewHolder)
        bindRoutes(viewHolder)
    }

    private fun bindIcon(viewHolder: GroupieViewHolder) {
        if (icon == null) {
            viewHolder.iconLayout.visibility = View.GONE
            viewHolder.iconPadding.visibility = View.GONE
        } else {
            viewHolder.serviceIconContainer.setImageResource(icon)
            viewHolder.serviceIconContainer.backgroundTintList =
                ColorStateList.valueOf(ContextCompat.getColor(viewHolder.itemView.context, R.color.grey_850))
            viewHolder.iconLayout.visibility = View.VISIBLE
            viewHolder.iconPadding.visibility = View.VISIBLE
        }
    }

    private fun bindTitle(viewHolder: GroupieViewHolder) {
        viewHolder.title.text = getServiceLocation().getName()
        viewHolder.subtitle.text = when (val service = serviceLocation.service) {
            Service.BUS_EIREANN,
            Service.DUBLIN_BUS -> "${service.fullName} (${serviceLocation.id})"
            else -> service.fullName
        }
        if (walkDistance != null) {
            viewHolder.walkTime.text = walkDistance.formatDistance()
            viewHolder.walkTime.visibility = View.VISIBLE
        } else {
            viewHolder.walkTime.visibility = View.GONE
        }
    }

    private fun bindRoutes(viewHolder: GroupieViewHolder) {
        if (routes.isNullOrEmpty()) {
            viewHolder.routesLayout.visibility = View.GONE
        } else {
            viewHolder.routes.removeAllViewsInLayout()
            for (route in routes) {
//            val chip = Chip(ContextThemeWrapper(viewHolder.itemView.context, R.style.ThinnerChip), null, 0)
                val chip = Chip(viewHolder.itemView.context)
                chip.setChipDrawable(ChipDrawable.createFromAttributes(viewHolder.itemView.context, null, 0, R.style.ThinnerChip))
                val (textColour, backgroundColour) = mapColour(route.operator, route.id)
                chip.text = " ${route.id} "
                chip.setTextAppearanceResource(R.style.SmallerText)
                chip.setTextColor(ColorStateList.valueOf(viewHolder.itemView.resources.getColor(textColour)))
                chip.setChipBackgroundColorResource(backgroundColour)
//            chip.chipMinHeight = 0f
                viewHolder.routes.addView(chip)
            }
            viewHolder.routesLayout.visibility = View.VISIBLE
        }
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

//    override fun isSameAs(other: com.xwray.groupie.Item<*>?): Boolean {
//        return equals(other)
//    }

    override fun isSameAs(other: com.xwray.groupie.Item<*>): Boolean {
        if (other is ServiceLocationItem) {
            return getServiceLocation().id == other.getServiceLocation().id
                    && getServiceLocation().service == other.getServiceLocation().service
        }
        return false
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

fun com.xwray.groupie.Item<com.xwray.groupie.GroupieViewHolder>.extractServiceLocation(): ServiceLocation? {
    return extras[serviceLocationKey] as? ServiceLocation
}

private fun Double.formatDistance(): String {
    return when {
        this <= 1.0 -> "1m"
        this < 1_000.0 -> "${toInt()}m"
        this < 10_000.0 -> {
            val rounded = (this / 1_000.0).round(1)
            if (rounded == 1.0) {
                "1km"
            } else {
                "${rounded}km"
            }
        }
        else -> "${(this / 1_000.0).toInt()}km"
    }
}

private fun Double.round(decimals: Int): Double {
    var multiplier = 1.0
    repeat(decimals) { multiplier *= 10 }
    return round(this * multiplier) / multiplier
}