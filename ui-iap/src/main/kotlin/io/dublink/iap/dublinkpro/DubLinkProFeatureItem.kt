package io.dublink.iap.dublinkpro

import android.view.View
import android.widget.TextView
import android.widget.updateText
import com.google.android.material.button.MaterialButton
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import com.xwray.groupie.kotlinandroidextensions.Item
import io.dublink.iap.R

class DubLinkProFeatureItem(
    private val title: String,
    private val summary: String,
    private val tryItListener: TryItListener? = null
) : Item() {

    override fun getLayout() = R.layout.list_item_feature

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.findViewById<TextView>(R.id.feature_title)?.apply {
            updateText(newText = title)
        }
        viewHolder.itemView.findViewById<TextView>(R.id.feature_summary)?.apply {
            updateText(newText = summary)
        }

        if (tryItListener != null) {
            viewHolder.itemView.findViewById<MaterialButton>(R.id.try_it_button)?.apply {
                visibility = View.VISIBLE
                setOnClickListener {
                    tryItListener.onTryItClicked()
                }
            }
        }
    }
}

interface TryItListener {
    fun onTryItClicked()
}
