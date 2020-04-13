package ie.dublinmapper.model

import android.content.res.ColorStateList
import android.graphics.Paint.STRIKE_THRU_TEXT_FLAG
import android.text.format.DateFormat
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import com.xwray.groupie.kotlinandroidextensions.Item
import ie.dublinmapper.domain.model.isDelayed
import ie.dublinmapper.domain.model.isLate
import ie.dublinmapper.domain.model.isOnTime
import ie.dublinmapper.domain.model.minutesDelayed
import ie.dublinmapper.domain.model.minutesLate
import ie.dublinmapper.ui.R
import ie.dublinmapper.util.ChipFactory
import io.rtpi.api.DockLiveData
import io.rtpi.api.PredictionLiveData
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import kotlinx.android.synthetic.main.list_item_live_data.destination
import kotlinx.android.synthetic.main.list_item_live_data.route
import kotlinx.android.synthetic.main.list_item_live_data.scheduledTime
import kotlinx.android.synthetic.main.list_item_live_data.status
import kotlinx.android.synthetic.main.list_item_live_data.waitTimeMinutes
import kotlinx.android.synthetic.main.list_item_live_data_dublin_bikes.bikes
import kotlinx.android.synthetic.main.list_item_live_data_dublin_bikes.bikesCount
import kotlinx.android.synthetic.main.list_item_live_data_dublin_bikes.docks
import kotlinx.android.synthetic.main.list_item_live_data_dublin_bikes.docksCount
import kotlinx.android.synthetic.main.list_item_live_data_grouped.groupedDestination
import kotlinx.android.synthetic.main.list_item_live_data_grouped.groupedRoute
import kotlinx.android.synthetic.main.list_item_live_data_grouped.groupedWaitTimeMinutes

private val format24h = DateTimeFormatter.ofPattern("HH:mm")
private val format12h = DateTimeFormatter.ofPattern("h:mm a")

class LiveDataItem(
    val liveData: PredictionLiveData // TODO private
) : Item() {

    override fun getLayout() = R.layout.list_item_live_data

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        bindRoute(viewHolder)
        bindDestination(viewHolder)
        bindStatus(viewHolder)
        bindScheduledTime(viewHolder)
        bindWaitTime(viewHolder)
    }

    private fun bindRoute(viewHolder: GroupieViewHolder) {
        viewHolder.route.text = " ${liveData.routeInfo.route} "
        val (textColour, backgroundColour) = ChipFactory.mapColour(liveData.operator, liveData.routeInfo.route)
        viewHolder.route.setTextColor(ColorStateList.valueOf(viewHolder.itemView.resources.getColor(textColour)))
        viewHolder.route.setChipBackgroundColorResource(backgroundColour)
    }

    private fun bindDestination(viewHolder: GroupieViewHolder) {
//        when {
//            isStarting -> viewHolder.destination.text = liveData.destination
//            isTerminating -> viewHolder.destination.text = "from ${liveData.origin}"
//            else -> viewHolder.destination.text = liveData.destination
//        }
        viewHolder.destination.text = liveData.routeInfo.destination
    }

    private fun bindStatus(viewHolder: GroupieViewHolder) {
        viewHolder.status.text = when {
            liveData.isLate() -> "Late ${liveData.minutesLate()} min"
            liveData.isDelayed() -> "Delayed ${liveData.minutesDelayed()} min"
            liveData.isOnTime() -> "On time"
            else -> "Scheduled"
        }
    }

    private fun bindScheduledTime(viewHolder: GroupieViewHolder) {
        val scheduledTime = liveData.prediction.scheduledDateTime
        if (DateFormat.is24HourFormat(viewHolder.itemView.context.applicationContext)) {
            viewHolder.scheduledTime.text = scheduledTime.toLocalTime().truncatedTo(ChronoUnit.MINUTES).format(format24h)
        } else {
            viewHolder.scheduledTime.text = scheduledTime.toLocalTime().truncatedTo(ChronoUnit.MINUTES).format(format12h)
        }

        if (liveData.isLate() || liveData.isDelayed()) {
            viewHolder.scheduledTime.paintFlags = viewHolder.scheduledTime.paintFlags or STRIKE_THRU_TEXT_FLAG
        } else {
            viewHolder.scheduledTime.paintFlags = viewHolder.scheduledTime.paintFlags and STRIKE_THRU_TEXT_FLAG.inv()
        }

//        when {
//            liveData.isLate() -> {
//                viewHolder.scheduledTime.setTextColor(viewHolder.itemView.resources.getColor(R.color.text_primary))
//                viewHolder.scheduledTime.paintFlags = viewHolder.scheduledTime.paintFlags or STRIKE_THRU_TEXT_FLAG
//            }
//            liveData.isDelayed() -> {
//                viewHolder.scheduledTime.setTextColor(viewHolder.itemView.resources.getColor(R.color.busEireannRed))
//                viewHolder.scheduledTime.paintFlags = viewHolder.scheduledTime.paintFlags and STRIKE_THRU_TEXT_FLAG.inv()
//            }
//            else -> {
//                viewHolder.scheduledTime.setTextColor(viewHolder.itemView.resources.getColor(R.color.text_primary))
//                viewHolder.scheduledTime.paintFlags = viewHolder.scheduledTime.paintFlags and STRIKE_THRU_TEXT_FLAG.inv()
//            }
//        }
    }

    private fun bindWaitTime(viewHolder: GroupieViewHolder) {
        viewHolder.waitTimeMinutes.text = when {
            liveData.prediction.waitTime.toMinutes() < 1L -> viewHolder.itemView.resources.getString(R.string.live_data_due)
            else -> {
                if (liveData.prediction.waitTime.toMinutes() >= 60L) {
                    val scheduledTime = liveData.prediction.scheduledDateTime
                    if (DateFormat.is24HourFormat(viewHolder.itemView.context.applicationContext)) {
                        scheduledTime.toLocalTime().truncatedTo(ChronoUnit.MINUTES).format(format24h)
                    } else {
                        scheduledTime.toLocalTime().truncatedTo(ChronoUnit.MINUTES).format(format12h)
                    }
                } else {
                    viewHolder.itemView.resources.getString(R.string.live_data_due_time, liveData.prediction.waitTime.toMinutes())
                }
//                val minutes = liveData.liveTime.waitTimeMinutes - (hours * 60)
//                when {
//                    hours == 0 -> viewHolder.itemView.resources.getString(R.string.live_data_due_time, minutes)
//                    minutes == 0 -> "$hours hr"
//                    else -> "$hours hr $minutes min"
//                }
            }
        }
    }

    override fun isSameAs(other: com.xwray.groupie.Item<*>): Boolean {
        if (other is LiveDataItem) {
            return liveData.operator == other.liveData.operator &&
                    liveData.routeInfo.route == other.liveData.routeInfo.route &&
                    liveData.routeInfo.destination == other.liveData.routeInfo.destination
        }
        return false
    }

    override fun equals(other: Any?): Boolean {
        if (other is LiveDataItem) {
            return isSameAs(other) && liveData.prediction.waitTime.toMinutes() == other.liveData.prediction.waitTime.toMinutes()
        }
        return false
    }

    override fun hashCode(): Int {
        return liveData.hashCode()
    }

    override fun toString(): String {
        return liveData.toString()
    }

//    private fun bindStartingLiveTime(viewHolder: ViewHolder) {
//        val scheduledDepartureTime = ZonedDateTime.parse(liveData.liveTime.scheduledDepartureTimestamp)
//            .toLocalTime()
//            .truncatedTo(ChronoUnit.MINUTES)
//
//        val expectedDepartureTime = ZonedDateTime.parse(liveData.liveTime.expectedDepartureTimestamp)
//            .toLocalTime()
//            .truncatedTo(ChronoUnit.MINUTES)
//
//        val scheduledDepartureTimestamp = if (DateFormat.is24HourFormat(viewHolder.itemView.context.applicationContext)) {
//            scheduledDepartureTime.format(format24h)
//        } else {
//            scheduledDepartureTime.format(format12h)
//        }
//
//        val isLate = liveData.liveTime.lateTimeMinutes > 1
//
//        val status = if (isLate) {
//            "Delayed ${liveData.liveTime.lateTimeMinutes} min"
//        } else {
//            null
//        }
//
//        val info = if (status != null) {
//            "Departing"
//        } else {
//            "On time"
//        }
//        viewHolder.information.text = "$info · " + scheduledDepartureTimestamp
//
//        if (status != null) {
//            viewHolder.status.text = status
//            viewHolder.status.visibility = View.VISIBLE
//        } else {
//            viewHolder.status.visibility = View.GONE
//        }
//    }
//
//    private fun bindTerminatingLiveTime(viewHolder: ViewHolder) {
//        val scheduledArrivalTime = ZonedDateTime.parse(liveData.liveTime.scheduledArrivalTimestamp)
//            .toLocalTime()
//            .truncatedTo(ChronoUnit.MINUTES)
//
//        val expectedArrivalTime = ZonedDateTime.parse(liveData.liveTime.expectedArrivalTimestamp)
//            .toLocalTime()
//            .truncatedTo(ChronoUnit.MINUTES)
//
//        val scheduledArrivalTimestamp = if (DateFormat.is24HourFormat(viewHolder.itemView.context.applicationContext)) {
//            scheduledArrivalTime.format(format24h)
//        } else {
//            scheduledArrivalTime.format(format12h)
//        }
//
//        val isLate = liveData.liveTime.lateTimeMinutes > 1
//
//        val status = if (isLate) {
//            "Delayed ${liveData.liveTime.lateTimeMinutes} min"
//        } else {
//            null
//        }
//
//        val info = if (status != null) {
//            "Terminating"
//        } else {
//            "On time"
//        }
//        viewHolder.information.text = "$info · " + scheduledArrivalTimestamp
//
//        if (status != null) {
//            viewHolder.status.text = status
//            viewHolder.status.visibility = View.VISIBLE
//        } else {
//            viewHolder.status.visibility = View.GONE
//        }
//    }
//
//    private fun bindDefaultLiveTime(viewHolder: ViewHolder) {
//        val scheduledArrivalTime = ZonedDateTime.parse(liveData.liveTime.scheduledArrivalTimestamp)
//            .toLocalTime()
//            .truncatedTo(ChronoUnit.MINUTES)
//
//        val expectedArrivalTime = ZonedDateTime.parse(liveData.liveTime.expectedArrivalTimestamp)
//            .toLocalTime()
//            .truncatedTo(ChronoUnit.MINUTES)
//
//        val scheduledArrivalTimestamp = if (DateFormat.is24HourFormat(viewHolder.itemView.context.applicationContext)) {
//            scheduledArrivalTime.format(format24h)
//        } else {
//            scheduledArrivalTime.format(format12h)
//        }
//
//        val isLate = liveData.liveTime.lateTimeMinutes > 1
//
//        val status = if (isLate) {
//            "Delayed ${liveData.liveTime.lateTimeMinutes} min"
//        } else {
//            null
//        }
//
//        val info = if (status != null) {
//            "Scheduled"
//        } else {
//            "On time"
//        }
//        viewHolder.information.text = "$info · " + scheduledArrivalTimestamp
//
//        if (status != null) {
//            viewHolder.status.text = status
//            viewHolder.status.visibility = View.VISIBLE
//        } else {
//            viewHolder.status.visibility = View.GONE
//        }
//    }
}

class GroupedLiveDataItem(
    private val liveData: List<PredictionLiveData>
) : Item() {

    override fun getLayout() = R.layout.list_item_live_data_grouped

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        bindRoute(viewHolder)
        bindDestination(viewHolder)
        bindWaitTime(viewHolder)
    }

    private fun bindRoute(viewHolder: GroupieViewHolder) {
        viewHolder.groupedRoute.text = " ${liveData.first().routeInfo.route} "
        val (textColour, backgroundColour) = ChipFactory.mapColour(liveData.first().operator, liveData.first().routeInfo.route)
        viewHolder.groupedRoute.setTextColor(ColorStateList.valueOf(viewHolder.itemView.resources.getColor(textColour)))
        viewHolder.groupedRoute.setChipBackgroundColorResource(backgroundColour)
    }

    private fun bindDestination(viewHolder: GroupieViewHolder) {
//        when {
//            isStarting -> viewHolder.destination.text = liveData.destination
//            isTerminating -> viewHolder.destination.text = "from ${liveData.origin}"
//            else -> viewHolder.destination.text = liveData.destination
//        }
        viewHolder.groupedDestination.text = liveData.first().routeInfo.destination
    }

    private fun bindWaitTime(viewHolder: GroupieViewHolder) {
        viewHolder.groupedWaitTimeMinutes.text = if (liveData.size == 1) {
            val minutes = liveData.first().prediction.waitTime.toMinutes()
            if (minutes <= 1L) {
                "Now"
            } else {
                "$minutes min"
            }
        } else {
            "${liveData.map {
                val minutes = it.prediction.waitTime.toMinutes()
                if (minutes <= 1L) {
                    "Now"
                } else {
                    minutes
                }
            }.joinToString(", ")} min"
        }
    }

    override fun isSameAs(other: com.xwray.groupie.Item<*>): Boolean {
        if (other is GroupedLiveDataItem) {
            return liveData.first().operator == other.liveData.first().operator &&
                    liveData.first().routeInfo.route == other.liveData.first().routeInfo.route &&
                    liveData.first().routeInfo.destination == other.liveData.first().routeInfo.destination
        }
        return false
    }

    override fun equals(other: Any?): Boolean {
        if (other is GroupedLiveDataItem) {
            if (liveData.size == other.liveData.size) {
                for (index in liveData.indices) {
                    if (liveData[index].prediction.waitTime.toMinutes() != other.liveData[index].prediction.waitTime.toMinutes()) {
                        return false
                    }
                }
            }
            return true
        }
        return false
    }

    override fun hashCode(): Int {
        return liveData.hashCode()
    }
}

class DublinBikesLiveDataItem(
    private val liveData: DockLiveData
) : Item() {

    override fun getLayout() = R.layout.list_item_live_data_dublin_bikes

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.bikesCount.text = if (liveData.availableBikes == 0) " No " else " ${liveData.availableBikes} "
        viewHolder.bikes.text = if (liveData.availableBikes == 1) "Bike" else "Bikes" // TODO plurals
        viewHolder.bikesCount.setTextColor(ColorStateList.valueOf(viewHolder.itemView.resources.getColor(android.R.color.white)))
        viewHolder.bikesCount.setChipBackgroundColorResource(getBackgroundColour(liveData.availableBikes))

        viewHolder.docksCount.text = if (liveData.availableDocks == 0) " No " else " ${liveData.availableDocks} "
        viewHolder.docks.text = if (liveData.availableDocks == 1) "Dock" else "Docks" // TODO plurals
        viewHolder.docksCount.setTextColor(ColorStateList.valueOf(viewHolder.itemView.resources.getColor(android.R.color.white)))
        viewHolder.docksCount.setChipBackgroundColorResource(getBackgroundColour(liveData.availableDocks))
    }

    private fun getBackgroundColour(amount: Int): Int {
        return when {
            amount < 3 -> R.color.luasRed
            amount < 6 -> R.color.aircoachOrange
            else -> R.color.dublinBikesTeal
        }
    }

    override fun isSameAs(other: com.xwray.groupie.Item<*>): Boolean {
        return equals(other)
    }

    override fun equals(other: Any?): Boolean {
        if (other is DublinBikesLiveDataItem) {
            return liveData == other.liveData
        }
        return false
    }

    override fun hashCode(): Int {
        return liveData.hashCode()
    }
}
