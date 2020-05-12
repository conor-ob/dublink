package io.dublink.model

import android.content.res.ColorStateList
import android.widget.TextView
import android.widget.updateText
import androidx.annotation.DrawableRes
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import com.xwray.groupie.kotlinandroidextensions.Item
import io.dublink.ui.R
import io.dublink.util.dipToPx

class HeaderErrorItem(
    private val message: String,
    @DrawableRes private val drawableId: Int,
    private val index: Int
) : Item() {

    override fun getLayout() = R.layout.list_item_header_error

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.findViewById<TextView>(R.id.message).apply {
            updateText(newText = message)
            setCompoundDrawablesRelativeWithIntrinsicBounds(drawableId, 0, 0, 0)
            compoundDrawablePadding = 12f.dipToPx(viewHolder.itemView.context).toInt()
            compoundDrawableTintList = ColorStateList.valueOf(viewHolder.itemView.context.getColor(R.color.color_error))
            setTextColor(ColorStateList.valueOf(viewHolder.itemView.context.getColor(R.color.color_error)))
        }
    }

    override fun isSameAs(other: com.xwray.groupie.Item<*>): Boolean {
        if (other is HeaderErrorItem) {
            return other.index == index
        }
        return false
    }

    override fun equals(other: Any?): Boolean {
        if (other is HeaderErrorItem) {
            return other.message == message
        }
        return false
    }

    override fun hashCode(): Int {
        return message.hashCode()
    }
}
