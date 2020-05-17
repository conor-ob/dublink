package io.dublink.iap

import android.widget.TextView
import android.widget.updateText
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import com.xwray.groupie.kotlinandroidextensions.Item

class FeatureItem(
    private val title: String,
    private val summary: String
) : Item() {

    override fun getLayout() = R.layout.list_item_feature

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.findViewById<TextView>(R.id.feature_title)?.apply {
            updateText(newText = title)
        }
        viewHolder.itemView.findViewById<TextView>(R.id.feature_summary)?.apply {
            updateText(newText = summary)
        }
    }
}
