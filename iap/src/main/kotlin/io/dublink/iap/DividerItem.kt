package io.dublink.iap

import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import com.xwray.groupie.kotlinandroidextensions.Item

class DividerItem : Item() {

    override fun getLayout() = R.layout.list_item_divider

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
    }
}
