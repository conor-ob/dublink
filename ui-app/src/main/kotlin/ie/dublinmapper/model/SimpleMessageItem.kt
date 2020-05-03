package ie.dublinmapper.model

import android.widget.TextView
import android.widget.updateText
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import com.xwray.groupie.kotlinandroidextensions.Item
import ie.dublinmapper.ui.R

class SimpleMessageItem(
    private val message: String,
    id: Long
) : Item(id) {

    override fun getLayout() = R.layout.list_item_simple_message

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.findViewById<TextView>(R.id.message).apply {
            updateText(message)
        }
    }

    override fun equals(other: Any?): Boolean {
        if (other is SimpleMessageItem) {
            return other.message == message
        }
        return false
    }

    override fun hashCode(): Int {
        return message.hashCode()
    }
}
