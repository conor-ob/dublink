package io.dublink.livedata

import android.widget.TextView
import android.widget.updateText
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import io.dublink.model.AbstractCommonItem
import io.dublink.ui.R

class NoLiveDataItem(
    private val message: String,
    id: Long
) : AbstractCommonItem(id) {

    override fun getLayout() = R.layout.list_item_no_live_data

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.findViewById<TextView>(R.id.no_live_data_message).apply {
            updateText(newText = message)
        }
    }
}
