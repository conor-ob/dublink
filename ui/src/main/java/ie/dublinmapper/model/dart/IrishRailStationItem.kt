package ie.dublinmapper.model.dart

import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import ie.dublinmapper.ui.R
import ie.dublinmapper.model.ServiceLocationItem
import io.rtpi.api.IrishRailStation

class IrishRailStationItem(
    dartStation: IrishRailStation,
    isEven: Boolean,
    isLast: Boolean
) : ServiceLocationItem(dartStation, isEven, isLast) {

    override fun bind(viewHolder: ViewHolder, position: Int) {
        bindBackground(viewHolder, position)
        bindTitle(viewHolder, getServiceLocation().name, null)
        bindIcon(viewHolder, R.drawable.ic_train, R.color.dartGreen)
    }

}
