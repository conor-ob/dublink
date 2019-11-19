package ie.dublinmapper.model

import android.content.res.ColorStateList
import android.graphics.Paint
import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import ie.dublinmapper.ui.R
import io.rtpi.api.Operator
import io.rtpi.api.TimedLiveData
import kotlinx.android.synthetic.main.list_item_live_data.*
import org.threeten.bp.LocalTime
import org.threeten.bp.ZonedDateTime
import org.threeten.bp.format.DateTimeFormatter
import org.threeten.bp.temporal.ChronoUnit
import android.graphics.Paint.STRIKE_THRU_TEXT_FLAG
import android.text.format.DateFormat
import kotlinx.android.synthetic.main.list_item_live_data.view.*


private val format24h = DateTimeFormatter.ofPattern("HH:mm")
private val format12h = DateTimeFormatter.ofPattern("h:mm a")

abstract class LiveDataItem(
    protected val liveData: TimedLiveData //TODO private
) : Item() {

    override fun getLayout() = R.layout.list_item_live_data

    override fun bind(viewHolder: ViewHolder, position: Int) {
        bindRoute(viewHolder)
        bindDestination(viewHolder)
        bindStatus(viewHolder)
        bindScheduledTime(viewHolder)
        bindWaitTime(viewHolder)
    }

    private fun bindRoute(viewHolder: ViewHolder) {
        viewHolder.route.text = liveData.route
        val (textColour, backgroundColour) = mapColour(liveData.operator, liveData.route)
        viewHolder.route.setTextColor(ColorStateList.valueOf(viewHolder.itemView.resources.getColor(textColour)))
        viewHolder.route.setChipBackgroundColorResource(backgroundColour)
    }

    private fun bindDestination(viewHolder: ViewHolder) {
//        when {
//            isStarting -> viewHolder.destination.text = liveData.destination
//            isTerminating -> viewHolder.destination.text = "from ${liveData.origin}"
//            else -> viewHolder.destination.text = liveData.destination
//        }
        viewHolder.destination.text = liveData.destination
    }

    private fun bindStatus(viewHolder: ViewHolder) {
        viewHolder.status.text = "Scheduled"
//        val currentTime = parseTimestampNearestMinute(liveData.liveTime.currentTimestamp)
//
//        val scheduledTime = when {
//            isStarting -> parseTimestampNearestMinute(liveData.liveTime.scheduledDepartureTimestamp!!)
//            isTerminating -> parseTimestampNearestMinute(liveData.liveTime.scheduledArrivalTimestamp!!)
//            else -> parseTimestampNearestMinute(liveData.liveTime.scheduledArrivalTimestamp!!)
//        }
//
//        val hasScheduledTimePassed = scheduledTime.isBefore(currentTime)
//
//        val status = if (hasScheduledTimePassed && liveData.liveTime.lateTimeMinutes > 0) {
//            "Late ${liveData.liveTime.lateTimeMinutes} min"
//        } else if (!hasScheduledTimePassed && liveData.liveTime.lateTimeMinutes > 0) {
//            "Delayed ${liveData.liveTime.lateTimeMinutes} min"
//        } else {
//            "Scheduled"
//        }
//
//        viewHolder.status.text = status
    }

    private fun bindScheduledTime(viewHolder: ViewHolder) {
        val scheduledTime = parseTimestampNearestMinute(liveData.liveTime.scheduledTimestamp)
        if (DateFormat.is24HourFormat(viewHolder.itemView.context.applicationContext)) {
            viewHolder.scheduledTime.text = scheduledTime.format(format24h)
        } else {
            viewHolder.scheduledTime.text = scheduledTime.format(format12h)
        }
//        val currentTime = parseTimestampNearestMinute(liveData.liveTime.currentTimestamp)
//
//        val scheduledTime = when {
//            isStarting -> parseTimestampNearestMinute(liveData.liveTime.scheduledDepartureTimestamp!!)
//            isTerminating -> parseTimestampNearestMinute(liveData.liveTime.scheduledArrivalTimestamp!!)
//            else -> parseTimestampNearestMinute(liveData.liveTime.scheduledArrivalTimestamp!!)
//        }
//
//        val hasScheduledTimePassed = scheduledTime.isBefore(currentTime)
//
//        if (DateFormat.is24HourFormat(viewHolder.itemView.context.applicationContext)) {
//            viewHolder.scheduledTime.text = scheduledTime.format(format24h)
//        } else {
//            viewHolder.scheduledTime.text = scheduledTime.format(format12h)
//        }
//        if (hasScheduledTimePassed) {
//            viewHolder.scheduledTime.paintFlags = viewHolder.scheduledTime.paintFlags or STRIKE_THRU_TEXT_FLAG
//        } else {
//            viewHolder.scheduledTime.paintFlags = viewHolder.scheduledTime.paintFlags and STRIKE_THRU_TEXT_FLAG.inv()
//        }
    }

    private fun bindWaitTime(viewHolder: ViewHolder) {
        viewHolder.waitTimeMinutes.text = when {
            liveData.liveTime.waitTimeMinutes < 1 -> viewHolder.itemView.resources.getString(R.string.live_data_due)
            else -> viewHolder.itemView.resources.getString(R.string.live_data_due_time, liveData.liveTime.waitTimeMinutes)
        }
    }

    private fun parseTimestampNearestMinute(timestamp: String): LocalTime {
        return ZonedDateTime.parse(timestamp)
            .toLocalTime()
            .truncatedTo(ChronoUnit.MINUTES)
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

}
