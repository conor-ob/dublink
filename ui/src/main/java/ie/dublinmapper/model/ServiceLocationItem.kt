package ie.dublinmapper.model

import android.content.res.ColorStateList
import android.view.View
import androidx.core.content.ContextCompat
import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import ie.dublinmapper.ui.R
import kotlinx.android.synthetic.main.list_item_service_location.*

abstract class ServiceLocationItem(
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

    fun bindTitle(viewHolder: ViewHolder, title: String, subtitle: String?) {
        if (subtitle == null) {
            viewHolder.mainTitle.text = title
            viewHolder.doubleTitleContainer.visibility = View.GONE
            viewHolder.singleTitleContainer.visibility = View.VISIBLE
        } else {
            viewHolder.title.text = title
            viewHolder.subtitle.text = subtitle
            viewHolder.singleTitleContainer.visibility = View.GONE
            viewHolder.doubleTitleContainer.visibility = View.VISIBLE
        }
    }

}
