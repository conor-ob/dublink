package io.dublink.model

import android.content.res.ColorStateList
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.updateText
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import com.xwray.groupie.kotlinandroidextensions.Item
import io.dublink.domain.model.DubLinkServiceLocation
import io.dublink.domain.util.formatDistance
import io.dublink.ui.R
import io.rtpi.api.Service
import java.util.Objects

abstract class AbstractServiceLocationItem(
    private val serviceLocation: DubLinkServiceLocation,
    private val walkDistance: Double?
) : Item() {

    init {
        extras[serviceLocationKey] = serviceLocation
    }

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        bindIcon(viewHolder)
        bindTitle(viewHolder)
    }

    private fun bindIcon(viewHolder: GroupieViewHolder) {
        viewHolder.itemView.findViewById<ImageView>(R.id.serviceIconContainer).apply {
            setImageResource(getIcon())
            imageTintList =
                ColorStateList.valueOf(ContextCompat.getColor(viewHolder.itemView.context, android.R.color.white))
            backgroundTintList =
                ColorStateList.valueOf(ContextCompat.getColor(viewHolder.itemView.context, R.color.grey_700))
        }
    }

    @DrawableRes
    private fun getIcon(): Int =
        when (serviceLocation.service) {
            Service.AIRCOACH,
            Service.BUS_EIREANN,
            Service.DUBLIN_BUS -> R.drawable.ic_bus
            Service.DUBLIN_BIKES -> R.drawable.ic_bike
            Service.IRISH_RAIL -> R.drawable.ic_train
            Service.LUAS -> R.drawable.ic_tram
        }

    private fun bindTitle(viewHolder: GroupieViewHolder) {
        viewHolder.itemView.findViewById<TextView>(R.id.title).apply {
            updateText(serviceLocation.name)
        }
        viewHolder.itemView.findViewById<TextView>(R.id.subtitle).apply {
            updateText(
                newText = when (val service = serviceLocation.service) {
                    Service.BUS_EIREANN,
                    Service.DUBLIN_BUS -> "${service.fullName} (${serviceLocation.id})"
                    else -> service.fullName
                }
            )
        }
        if (walkDistance != null) {
            viewHolder.itemView.findViewById<TextView>(R.id.walkTime).apply {
                updateText(walkDistance.formatDistance())
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

private const val serviceLocationKey = "serviceLocation"
private const val searchCandidateKey = "searchCandidate"

fun AbstractServiceLocationItem.getServiceLocation(): DubLinkServiceLocation = extras[serviceLocationKey] as DubLinkServiceLocation

fun AbstractServiceLocationItem.setSearchCandidate() {
    extras[searchCandidateKey] = true
}

fun AbstractServiceLocationItem.isSearchCandidate(): Boolean {
    return extras[searchCandidateKey] as? Boolean ?: false
}
