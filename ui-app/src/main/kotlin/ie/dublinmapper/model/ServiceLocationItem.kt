package ie.dublinmapper.model

import android.content.res.ColorStateList
import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import com.xwray.groupie.kotlinandroidextensions.Item
import ie.dublinmapper.domain.model.DubLinkDockLocation
import ie.dublinmapper.domain.model.DubLinkServiceLocation
import ie.dublinmapper.domain.model.DubLinkStopLocation
import ie.dublinmapper.ui.R
import ie.dublinmapper.util.ChipFactory
import io.rtpi.api.Service
import java.util.Objects
import kotlin.math.round
import kotlinx.android.synthetic.main.list_item_service_location.bikes
import kotlinx.android.synthetic.main.list_item_service_location.bikesCount
import kotlinx.android.synthetic.main.list_item_service_location.docks
import kotlinx.android.synthetic.main.list_item_service_location.docksCount
import kotlinx.android.synthetic.main.list_item_service_location.rootViewBikes
import kotlinx.android.synthetic.main.list_item_service_location.routes
import kotlinx.android.synthetic.main.list_item_service_location.routesDivider
import kotlinx.android.synthetic.main.list_item_service_location.serviceIconContainer
import kotlinx.android.synthetic.main.list_item_service_location.subtitle
import kotlinx.android.synthetic.main.list_item_service_location.title
import kotlinx.android.synthetic.main.list_item_service_location.walkTime

private const val serviceLocationKey = "key_service_location"
private const val searchCandidateKey = "key_service_candidate"

fun ServiceLocationItem.setSearchCandidate() {
    extras[searchCandidateKey] = true
}

fun ServiceLocationItem.isSearchCandidate(): Boolean {
    return extras[searchCandidateKey] as? Boolean ?: false
}

class ServiceLocationItem(
    private val serviceLocation: DubLinkServiceLocation,
    private val icon: Int,
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

    override fun getDragDirs(): Int {
        return ItemTouchHelper.UP or ItemTouchHelper.DOWN
    }

    private fun bindIcon(viewHolder: GroupieViewHolder) {
        viewHolder.serviceIconContainer.setImageResource(icon)
        viewHolder.serviceIconContainer.imageTintList =
            ColorStateList.valueOf(ContextCompat.getColor(viewHolder.itemView.context, android.R.color.white))
        viewHolder.serviceIconContainer.backgroundTintList =
            ColorStateList.valueOf(ContextCompat.getColor(viewHolder.itemView.context, R.color.grey_700))
    }

    private fun bindTitle(viewHolder: GroupieViewHolder) {
        viewHolder.title.text = getServiceLocation().name
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
        if (serviceLocation is DubLinkStopLocation) {
            viewHolder.rootViewBikes.visibility = View.GONE
            viewHolder.routes.removeAllViewsInLayout()
            val activeFilters = if (serviceLocation.isFavourite) {
                serviceLocation.filters.filter { it.isActive }
            } else {
                serviceLocation.filters
            }
            for (filter in activeFilters) {
                viewHolder.routes.addView(ChipFactory.newChip(viewHolder.itemView.context, filter))
            }
            viewHolder.routes.visibility = View.VISIBLE
            viewHolder.routesDivider.visibility = View.VISIBLE
        } else if (serviceLocation is DubLinkDockLocation) {
            viewHolder.routes.visibility = View.GONE
            viewHolder.routesDivider.visibility = View.VISIBLE
            viewHolder.rootViewBikes.visibility = View.VISIBLE
            viewHolder.bikesCount.text = if (serviceLocation.availableBikes == 0) " No " else " ${serviceLocation.availableBikes} "
            viewHolder.bikes.text = if (serviceLocation.availableBikes == 1) "Bike" else "Bikes" // TODO plurals
            viewHolder.bikesCount.setTextColor(ColorStateList.valueOf(viewHolder.itemView.resources.getColor(android.R.color.white)))
            viewHolder.bikesCount.setChipBackgroundColorResource(getBackgroundColour(serviceLocation.availableBikes))

            viewHolder.docksCount.text = if (serviceLocation.availableDocks == 0) " No " else " ${serviceLocation.availableDocks} "
            viewHolder.docks.text = if (serviceLocation.availableDocks == 1) "Dock" else "Docks" // TODO plurals
            viewHolder.docksCount.setTextColor(ColorStateList.valueOf(viewHolder.itemView.resources.getColor(android.R.color.white)))
            viewHolder.docksCount.setChipBackgroundColorResource(getBackgroundColour(serviceLocation.availableDocks))
        }
    }

    private fun getBackgroundColour(amount: Int): Int {
        return when {
            amount < 3 -> R.color.luasRed
            amount < 6 -> R.color.aircoachOrange
            else -> R.color.dublinBikesTeal
        }
    }

    private fun setServiceLocation(serviceLocation: DubLinkServiceLocation) {
        extras[serviceLocationKey] = serviceLocation
    }

    protected fun getServiceLocation(): DubLinkServiceLocation {
        return extras[serviceLocationKey] as DubLinkServiceLocation
    }

//    override fun isSameAs(other: com.xwray.groupie.Item<*>?): Boolean {
//        return equals(other)
//    }

    override fun isSameAs(other: com.xwray.groupie.Item<*>): Boolean {
        if (other is ServiceLocationItem) {
            return getServiceLocation().id == other.getServiceLocation().id &&
                    getServiceLocation().service == other.getServiceLocation().service
        }
        return false
    }

    override fun equals(other: Any?): Boolean {
        if (other is ServiceLocationItem) {
            return getServiceLocation() == other.getServiceLocation() &&
                walkDistance == other.walkDistance
        }
        return false
    }

    override fun hashCode(): Int {
        return Objects.hash(getServiceLocation(), walkDistance)
    }
}

fun com.xwray.groupie.Item<com.xwray.groupie.GroupieViewHolder>.extractServiceLocation(): DubLinkServiceLocation? {
    return extras[serviceLocationKey] as? DubLinkServiceLocation
}

fun ServiceLocationItem.extractServiceLocation(): DubLinkServiceLocation? {
    return extras[serviceLocationKey] as? DubLinkServiceLocation
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
