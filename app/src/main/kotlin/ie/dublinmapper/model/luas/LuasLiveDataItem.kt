package ie.dublinmapper.model.luas

import android.content.res.ColorStateList
import androidx.core.content.ContextCompat
import com.xwray.groupie.Item
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import ie.dublinmapper.R
import ie.dublinmapper.model.LuasLiveDataUi
import ie.dublinmapper.model.livedata.LiveDataItem
import kotlinx.android.synthetic.main.list_item_live_data.*

class LuasLiveDataItem(
    private val liveData: LuasLiveDataUi,
    isEven: Boolean,
    isLast: Boolean
) : LiveDataItem(isEven, isLast) {

    override fun getLayout() = R.layout.list_item_live_data

    override fun bind(viewHolder: ViewHolder, position: Int) {
        super.bind(viewHolder, position)
        super.bindDueTimes(viewHolder, liveData.liveData.dueTime)
        val liveData = liveData.liveData
        viewHolder.title.text = liveData.destination
        viewHolder.subtitle.text = liveData.route
        viewHolder.operatorIconContainer.setImageResource(R.drawable.ic_tram)
        when {
            liveData.route.equals("Red", ignoreCase = true) -> viewHolder.operatorIconContainer.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(viewHolder.itemView.context, R.color.luasRed))
            liveData.route.equals("Green", ignoreCase = true) -> viewHolder.operatorIconContainer.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(viewHolder.itemView.context, R.color.luasGreen))
            else -> viewHolder.operatorIconContainer.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(viewHolder.itemView.context, R.color.luasPurple))
        }
    }

    override fun isSameAs(other: Item<*>?): Boolean {
        if (other is LuasLiveDataItem) {
            return liveData.liveData.customHash == other.liveData.liveData.customHash
        }
        return false
    }

    override fun equals(other: Any?): Boolean {
        if (other is LuasLiveDataItem) {
            return liveData.liveData == other.liveData.liveData
        }
        return false
    }

    override fun hashCode(): Int {
        return liveData.liveData.hashCode()
    }

}
