package ie.dublinmapper.model.aircoach

import android.content.res.ColorStateList
import androidx.core.content.ContextCompat
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import ie.dublinmapper.R
import ie.dublinmapper.model.AircoachStopUi
import ie.dublinmapper.model.ServiceLocationItemX
import kotlinx.android.synthetic.main.list_item_service_location.*

class AircoachStopItem(
    private val aircoachStop: AircoachStopUi,
    isEven: Boolean,
    isLast: Boolean
) : ServiceLocationItemX(isEven, isLast) {

    init {
        extras["serviceLocation"] = aircoachStop
    }

    override fun getLayout() = R.layout.list_item_service_location

    override fun bind(viewHolder: ViewHolder, position: Int) {
        super.bind(viewHolder, position)
        viewHolder.title.text = aircoachStop.name
        viewHolder.subtitle.text = viewHolder.itemView.context.getString(R.string.stop_number, aircoachStop.id)
        viewHolder.serviceIconContainer.setImageResource(R.drawable.ic_bus)
        viewHolder.serviceIconContainer.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(viewHolder.itemView.context, R.color.aircoachOrange))
    }

}
