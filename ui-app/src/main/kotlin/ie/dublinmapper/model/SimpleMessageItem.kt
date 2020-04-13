package ie.dublinmapper.model

import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import com.xwray.groupie.kotlinandroidextensions.Item
import ie.dublinmapper.ui.R
import kotlinx.android.synthetic.main.list_item_simple_message.message

class SimpleMessageItem(private val message: String, private val index: Int) : Item() {

    override fun getLayout() = R.layout.list_item_simple_message

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.message.text = message
    }

    override fun isSameAs(other: com.xwray.groupie.Item<*>): Boolean {
        if (other is SimpleMessageItem) {
            return other.index == index
        }
        return false
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
