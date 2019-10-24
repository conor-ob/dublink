package ie.dublinmapper.model.dublinbikes

import android.content.res.ColorStateList
import androidx.core.content.ContextCompat
import com.xwray.groupie.Item
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import ie.dublinmapper.ui.R
import ie.dublinmapper.model.LiveDataItem
import io.rtpi.api.DublinBikesLiveData
import kotlinx.android.synthetic.main.list_item_live_data.*

class DublinBikesLiveDataItem(
    private val liveData: DublinBikesLiveData,
    private val isBike: Boolean,
    isEven: Boolean,
    isLast: Boolean
) : LiveDataItem(isEven, isLast) {

    override fun bind(viewHolder: ViewHolder, position: Int) {
        super.bind(viewHolder, position)
        if (isBike) {
            viewHolder.title.text = "Bikes"
            viewHolder.subtitle.text = liveData.bikes.toString()
        } else {
            viewHolder.title.text = "Docks"
            viewHolder.subtitle.text = liveData.docks.toString()
        }
        viewHolder.operatorIconContainer.setImageResource(R.drawable.ic_bike)
        viewHolder.operatorIconContainer.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(viewHolder.itemView.context, R.color.dublinBikesTeal))
    }

    override fun isSameAs(other: Item<*>?): Boolean {
        if (other is DublinBikesLiveDataItem) {
            return liveData.operator == other.liveData.operator &&
                    liveData.bikes == other.liveData.bikes &&
                    liveData.docks == other.liveData.docks
        }
        return false
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
