package io.dublink.model

import android.content.res.ColorStateList
import android.graphics.Paint.STRIKE_THRU_TEXT_FLAG
import android.text.format.DateFormat
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import com.xwray.groupie.kotlinandroidextensions.Item
import io.dublink.domain.model.isDelayed
import io.dublink.domain.model.isLate
import io.dublink.domain.model.isOnTime
import io.dublink.domain.model.minutesDelayed
import io.dublink.domain.model.minutesLate
import io.dublink.ui.R
import io.dublink.util.ChipFactory
import io.rtpi.api.PredictionLiveData
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import kotlinx.android.synthetic.main.list_item_live_data.destination
import kotlinx.android.synthetic.main.list_item_live_data.route
import kotlinx.android.synthetic.main.list_item_live_data.scheduledTime
import kotlinx.android.synthetic.main.list_item_live_data.status
import kotlinx.android.synthetic.main.list_item_live_data.waitTimeMinutes

private val format24h = DateTimeFormatter.ofPattern("HH:mm")
private val format12h = DateTimeFormatter.ofPattern("h:mm a")

class LiveDataItem(
    private val liveData: PredictionLiveData
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
            viewHolder.scheduledTime.text = scheduledTime.toLocalTime().truncatedTo(ChronoUnit.MINUTES).format(
                format24h
            )
        } else {
            viewHolder.scheduledTime.text = scheduledTime.toLocalTime().truncatedTo(ChronoUnit.MINUTES).format(
                format12h
            )
        }

        if (liveData.isLate() || liveData.isDelayed()) {
            viewHolder.scheduledTime.paintFlags = viewHolder.scheduledTime.paintFlags or STRIKE_THRU_TEXT_FLAG
        } else {
            viewHolder.scheduledTime.paintFlags = viewHolder.scheduledTime.paintFlags and STRIKE_THRU_TEXT_FLAG.inv()
        }
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
}
