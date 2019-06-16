package ie.dublinmapper.model.livedata

import android.content.res.ColorStateList
import androidx.core.content.ContextCompat
import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import ie.dublinmapper.R
import kotlinx.android.synthetic.main.list_item_live_data.*

abstract class LiveDataItem(
    private val isEven: Boolean,
    private val isLast: Boolean
) : Item() {

    override fun bind(viewHolder: ViewHolder, position: Int) {
        if (isLast) {
            viewHolder.rootView.background = viewHolder.itemView.context.getDrawable(R.drawable.shape_rounded_bottom_corners)
        } else {
            viewHolder.rootView.background = viewHolder.itemView.context.getDrawable(R.drawable.shape_rectangle)
        }
        if (isEven) {
            viewHolder.rootView.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(viewHolder.itemView.context, R.color.white))
        } else {
            viewHolder.rootView.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(viewHolder.itemView.context, R.color.grey_100))
        }
    }

}
