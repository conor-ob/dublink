package ie.dublinmapper.model

import android.content.res.ColorStateList
import android.util.TypedValue
import android.view.View
import androidx.core.content.ContextCompat
import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import ie.dublinmapper.ui.R
import kotlinx.android.synthetic.main.list_item_service_location.*
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipDrawable
import ie.dublinmapper.domain.model.getName
import io.rtpi.api.*
import kotlin.math.round

private const val serviceLocationKey = "key_service_location"
private const val searchCandidateKey = "key_service_candidate"

fun ServiceLocationItem.setSearchCandidate() {
    extras[searchCandidateKey] = true
}

fun ServiceLocationItem.isSearchCandidate(): Boolean {
    return extras[searchCandidateKey] as? Boolean ?: false
}

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
        viewHolder.serviceIconContainer.setImageResource(icon)
        viewHolder.serviceIconContainer.backgroundTintList =
            ColorStateList.valueOf(ContextCompat.getColor(viewHolder.itemView.context, R.color.colorIconBackground))
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
        if (serviceLocation is ServiceLocationRoutes) {
            viewHolder.rootViewBikes.visibility = View.GONE
            if (routes.isNullOrEmpty()) {
                viewHolder.routes.visibility = View.GONE
                viewHolder.routesDivider.visibility = View.GONE
            } else {
                val dip = 4f
                val px = TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP,
                    dip,
                    viewHolder.itemView.resources.displayMetrics
                )
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
                    chip.elevation = px
//            chip.chipMinHeight = 0f
                    viewHolder.routes.addView(chip)
                }
                viewHolder.routes.visibility = View.VISIBLE
                viewHolder.routesDivider.visibility = View.VISIBLE
            }
        } else if (serviceLocation is DublinBikesDock) {
            viewHolder.routes.visibility = View.GONE
            if (routes == null) {
                viewHolder.routesDivider.visibility = View.GONE
                viewHolder.rootViewBikes.visibility = View.GONE
            } else {
                viewHolder.routesDivider.visibility = View.VISIBLE
                viewHolder.rootViewBikes.visibility = View.VISIBLE
                viewHolder.bikesCount.text = if (serviceLocation.availableBikes == 0) " No " else " ${serviceLocation.availableBikes} "
                viewHolder.bikes.text = if (serviceLocation.availableBikes == 1) "Bike" else "Bikes" //TODO plurals
                viewHolder.bikesCount.setTextColor(ColorStateList.valueOf(viewHolder.itemView.resources.getColor(R.color.white)))
                viewHolder.bikesCount.setChipBackgroundColorResource(getBackgroundColour(serviceLocation.availableBikes))

                viewHolder.docksCount.text = if (serviceLocation.availableDocks == 0) " No " else " ${serviceLocation.availableDocks} "
                viewHolder.docks.text = if (serviceLocation.availableDocks == 1) "Dock" else "Docks" //TODO plurals
                viewHolder.docksCount.setTextColor(ColorStateList.valueOf(viewHolder.itemView.resources.getColor(R.color.white)))
                viewHolder.docksCount.setChipBackgroundColorResource(getBackgroundColour(serviceLocation.availableDocks))
            }
        }
    }

    private fun getBackgroundColour(amount: Int): Int {
        return when {
            amount < 3 -> R.color.luasRed
            amount < 6 -> R.color.aircoachOrange
            else -> R.color.dublinBikesTeal
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