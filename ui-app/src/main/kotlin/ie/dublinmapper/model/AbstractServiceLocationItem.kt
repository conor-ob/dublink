package ie.dublinmapper.model

import android.content.res.ColorStateList
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import com.xwray.groupie.kotlinandroidextensions.Item
import ie.dublinmapper.domain.model.DubLinkServiceLocation
import ie.dublinmapper.domain.util.formatDistance
import ie.dublinmapper.ui.R
import io.rtpi.api.Service
import java.util.Objects

abstract class AbstractServiceLocationItem(
    private val serviceLocation: DubLinkServiceLocation,
    private val icon: Int,
    private val walkDistance: Double?
) : Item() {

    init {
        extras[serviceKey] = serviceLocation.service
        extras[locationIdKey] = serviceLocation.id
        extras[serviceLocationKey] = serviceLocation
    }

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        bindIcon(viewHolder)
        bindTitle(viewHolder)
    }

    private fun bindIcon(viewHolder: GroupieViewHolder) {
        viewHolder.itemView.findViewById<ImageView>(R.id.serviceIconContainer).apply {
            setImageResource(icon)
            imageTintList =
                ColorStateList.valueOf(ContextCompat.getColor(viewHolder.itemView.context, android.R.color.white))
            backgroundTintList =
                ColorStateList.valueOf(ContextCompat.getColor(viewHolder.itemView.context, R.color.grey_700))
        }
    }

    private fun bindTitle(viewHolder: GroupieViewHolder) {
        viewHolder.itemView.findViewById<TextView>(R.id.title).apply {
            text = serviceLocation.name
        }
        viewHolder.itemView.findViewById<TextView>(R.id.subtitle).apply {
            text = when (val service = serviceLocation.service) {
                Service.BUS_EIREANN,
                Service.DUBLIN_BUS -> "${service.fullName} (${serviceLocation.id})"
                else -> service.fullName
            }
        }
        if (walkDistance != null) {
            viewHolder.itemView.findViewById<TextView>(R.id.walkTime).apply {
                text = walkDistance.formatDistance()
                visibility = View.VISIBLE
            }
        } else {
            viewHolder.itemView.findViewById<TextView>(R.id.walkTime).apply {
                visibility = View.GONE
            }
        }
    }

    override fun isSameAs(other: com.xwray.groupie.Item<*>): Boolean {
        if (other is AbstractServiceLocationItem) {
            return serviceLocation.service == other.serviceLocation.service &&
                serviceLocation.id == other.serviceLocation.id
        }
        return false
    }

    override fun equals(other: Any?): Boolean {
        if (other is AbstractServiceLocationItem) {
            return serviceLocation == other.serviceLocation &&
                walkDistance == other.walkDistance
        }
        return false
    }

    override fun hashCode(): Int {
        return Objects.hash(serviceLocation, walkDistance)
    }
}

private const val serviceKey = "service"
private const val locationIdKey = "locationId"
private const val serviceLocationKey = "serviceLocation"
private const val searchCandidateKey = "searchCandidate"

fun AbstractServiceLocationItem.getService(): Service = extras[serviceKey] as Service
fun AbstractServiceLocationItem.getLocationId(): String = extras[locationIdKey] as String
fun AbstractServiceLocationItem.getServiceLocation(): DubLinkServiceLocation = extras[serviceLocationKey] as DubLinkServiceLocation

fun AbstractServiceLocationItem.setSearchCandidate() {
    extras[searchCandidateKey] = true
}

fun AbstractServiceLocationItem.isSearchCandidate(): Boolean {
    return extras[searchCandidateKey] as? Boolean ?: false
}
