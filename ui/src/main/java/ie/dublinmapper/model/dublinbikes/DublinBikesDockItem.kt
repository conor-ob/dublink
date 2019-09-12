package ie.dublinmapper.model.dublinbikes

import android.content.res.ColorStateList
import androidx.core.content.ContextCompat
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import ie.dublinmapper.ui.R
import ie.dublinmapper.domain.model.DublinBikesDock
import ie.dublinmapper.model.ServiceLocationItem
import kotlinx.android.synthetic.main.list_item_service_location.*

class DublinBikesDockItem(
    val dublinBikesDock: DublinBikesDock,
    isEven: Boolean,
    isLast: Boolean
) : ServiceLocationItem(isEven, isLast) {

    init {
        extras["serviceLocation"] = dublinBikesDock
    }

    override fun getLayout() = R.layout.list_item_service_location

    override fun bind(viewHolder: ViewHolder, position: Int) {
        super.bind(viewHolder, position)
        viewHolder.title.text = dublinBikesDock.name
        viewHolder.subtitle.text = "Bikes: ${dublinBikesDock.availableBikes}, Docks: ${dublinBikesDock.availableDocks}"
        viewHolder.serviceIconContainer.setImageResource(R.drawable.ic_bike)
        viewHolder.serviceIconContainer.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(viewHolder.itemView.context, R.color.dublinBikesTeal))
    }

}
