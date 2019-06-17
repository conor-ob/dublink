package ie.dublinmapper.model.dart

import android.content.res.ColorStateList
import androidx.core.content.ContextCompat
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import ie.dublinmapper.R
import ie.dublinmapper.model.DartStationUi
import ie.dublinmapper.model.ServiceLocationItemX
import kotlinx.android.synthetic.main.list_item_service_location.*

class DartStationItem(
    val dartStation: DartStationUi,
    isEven: Boolean,
    isLast: Boolean
) : ServiceLocationItemX(isEven, isLast) {

    init {
        extras["serviceLocation"] = dartStation
    }

    override fun getLayout() = R.layout.list_item_service_location

    override fun bind(viewHolder: ViewHolder, position: Int) {
        super.bind(viewHolder, position)
        viewHolder.title.text = dartStation.name
        viewHolder.subtitle.text = dartStation.id
        viewHolder.serviceIconContainer.setImageResource(R.drawable.ic_train)
        viewHolder.serviceIconContainer.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(viewHolder.itemView.context, R.color.dartGreen))
    }

}
