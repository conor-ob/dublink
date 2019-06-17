package ie.dublinmapper.model.dublinbus

import android.content.res.ColorStateList
import androidx.core.content.ContextCompat
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import ie.dublinmapper.R
import ie.dublinmapper.model.DublinBusStopUi
import ie.dublinmapper.model.ServiceLocationItemX
import kotlinx.android.synthetic.main.list_item_service_location.*

class DublinBusStopItem(
    private val dublinBusStop: DublinBusStopUi,
    isEven: Boolean,
    isLast: Boolean
) : ServiceLocationItemX(isEven, isLast) {

    init {
        extras["serviceLocation"] = dublinBusStop
    }

    override fun getLayout() = R.layout.list_item_service_location

    override fun bind(viewHolder: ViewHolder, position: Int) {
        super.bind(viewHolder, position)
        viewHolder.title.text = dublinBusStop.name
        viewHolder.subtitle.text = viewHolder.itemView.context.getString(R.string.stop_number, dublinBusStop.id)
        viewHolder.serviceIconContainer.setImageResource(R.drawable.ic_bus)
        viewHolder.serviceIconContainer.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(viewHolder.itemView.context, R.color.dublinBusYellow))
    }

}
