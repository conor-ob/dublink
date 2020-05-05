package io.dublink.search

import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import io.dublink.model.AbstractCommonItem
import kotlinx.android.synthetic.main.list_item_no_location.view.*

class NoLocationItem(
    id: Long,
    private val clickListener: OnEnableLocationClickedListener
) : AbstractCommonItem(id) {

    override fun getLayout() = R.layout.list_item_no_location

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.search_button_enable_location.setOnClickListener {
            clickListener.onEnableLocationClicked()
        }
    }
}

interface OnEnableLocationClickedListener {
    fun onEnableLocationClicked()
}
