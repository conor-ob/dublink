package ie.dublinmapper.model

import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import ie.dublinmapper.R
import kotlinx.android.synthetic.main.list_item_live_data_section_header.*

class HeaderItem(private val header: String) : Item() {

    override fun getLayout() = R.layout.list_item_live_data_section_header

    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.direction.text = header
    }

    override fun isSameAs(other: com.xwray.groupie.Item<*>?): Boolean {
        if (other is HeaderItem) {
            return header == other.header
        }
        return false
    }

    override fun equals(other: Any?): Boolean {
        if (other is HeaderItem) {
            return header == other.header
        }
        return false
    }

    override fun hashCode(): Int {
        return header.hashCode()
    }

}
