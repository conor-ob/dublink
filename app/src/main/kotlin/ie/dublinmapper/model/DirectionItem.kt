package ie.dublinmapper.model

import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import ie.dublinmapper.R
import kotlinx.android.synthetic.main.list_item_direction.*

class DirectionItem(private val direction: String) : Item() {

    override fun getLayout() = R.layout.list_item_direction

    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.direction.text = direction
    }

}
