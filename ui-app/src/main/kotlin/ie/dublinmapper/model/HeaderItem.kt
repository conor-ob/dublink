package ie.dublinmapper.model

import androidx.annotation.DrawableRes
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import com.xwray.groupie.kotlinandroidextensions.Item
import ie.dublinmapper.ui.R
import ie.dublinmapper.util.dipToPx
import kotlinx.android.synthetic.main.list_item_simple_message.*

class HeaderItem(
    private val message: String,
    @DrawableRes private val drawableId: Int?,
    private val index: Int
) : Item() {

    override fun getLayout() = R.layout.list_item_header

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.message.text = message
        if (drawableId != null) {
            viewHolder.message.setCompoundDrawablesRelativeWithIntrinsicBounds(drawableId, 0, 0, 0)
            viewHolder.message.compoundDrawablePadding = 12f.dipToPx(viewHolder.itemView.context).toInt()
        }
    }

    override fun isSameAs(other: com.xwray.groupie.Item<*>): Boolean {
        if (other is HeaderItem) {
            return other.index == index
        }
        return false
    }

    override fun equals(other: Any?): Boolean {
        if (other is HeaderItem) {
            return other.message == message
        }
        return false
    }

    override fun hashCode(): Int {
        return message.hashCode()
    }
}
