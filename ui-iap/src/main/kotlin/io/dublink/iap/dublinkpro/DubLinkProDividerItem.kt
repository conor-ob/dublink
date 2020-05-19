package io.dublink.iap.dublinkpro

import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import com.xwray.groupie.kotlinandroidextensions.Item
import io.dublink.iap.R

class DubLinkProDividerItem : Item() {

    override fun getLayout() = R.layout.list_item_divider

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
    }
}
