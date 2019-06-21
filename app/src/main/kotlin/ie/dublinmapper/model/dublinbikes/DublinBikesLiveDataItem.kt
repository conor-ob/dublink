package ie.dublinmapper.model.dublinbikes

import android.content.res.ColorStateList
import androidx.core.content.ContextCompat
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import ie.dublinmapper.R
import ie.dublinmapper.domain.model.DublinBikesLiveData
import ie.dublinmapper.model.livedata.LiveDataItem
import kotlinx.android.synthetic.main.list_item_live_data.*

class DublinBikesLiveDataItem(
    private val liveData: DublinBikesLiveData,
    private val isBike: Boolean,
    isEven: Boolean,
    isLast: Boolean
) : LiveDataItem(isEven, isLast) {

    override fun getLayout() = R.layout.list_item_live_data

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

}
