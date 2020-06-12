package io.dublink.favourites.edit

import android.view.MotionEvent
import android.widget.ImageView
import androidx.recyclerview.widget.ItemTouchHelper
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import io.dublink.domain.model.DubLinkDockLocation
import io.dublink.favourites.R
import io.dublink.model.DockLocationItem
import io.dublink.model.SimpleServiceLocationItem

class EditableDockLocationItem(
    serviceLocation: DubLinkDockLocation,
    walkDistance: Double?,
    private val itemTouchHelper: ItemTouchHelper
) : SimpleServiceLocationItem(serviceLocation, walkDistance) {

    override fun getLayout() = R.layout.list_item_editable_dock_location

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        super.bind(viewHolder, position)
        viewHolder.itemView.findViewById<ImageView>(R.id.reorder)?.apply {
            setOnTouchListener { v, event ->
                if (event.actionMasked == MotionEvent.ACTION_DOWN) {
                    itemTouchHelper.startDrag(viewHolder)
                }
                return@setOnTouchListener false
            }
        }
    }
}
