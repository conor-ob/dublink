package ie.dublinmapper.model.buseireann

import android.content.res.ColorStateList
import androidx.core.content.ContextCompat
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import ie.dublinmapper.R
import ie.dublinmapper.domain.model.BusEireannStop
import ie.dublinmapper.model.ServiceLocationItem
import kotlinx.android.synthetic.main.list_item_service_location.*

class BusEireannStopItem(
    private val busEireannStop: BusEireannStop,
    isEven: Boolean,
    isLast: Boolean
) : ServiceLocationItem(isEven, isLast) {

    init {
        extras["serviceLocation"] = busEireannStop
    }

    override fun getLayout() = R.layout.list_item_service_location

    override fun bind(viewHolder: ViewHolder, position: Int) {
        super.bind(viewHolder, position)
        viewHolder.title.text = busEireannStop.name
        viewHolder.subtitle.text = viewHolder.itemView.context.getString(R.string.stop_number, busEireannStop.id)
        viewHolder.serviceIconContainer.setImageResource(R.drawable.ic_bus)
        viewHolder.serviceIconContainer.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(viewHolder.itemView.context, R.color.busEireannRed))
    }

}
