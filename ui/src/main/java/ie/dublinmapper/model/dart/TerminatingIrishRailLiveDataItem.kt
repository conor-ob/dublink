package ie.dublinmapper.model.dart

import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import ie.dublinmapper.ui.R
import io.rtpi.api.IrishRailLiveData
import kotlinx.android.synthetic.main.list_item_live_data.*

class TerminatingIrishRailLiveDataItem(
    private val liveData: IrishRailLiveData,
    isEven: Boolean,
    isLast: Boolean
) : DartLiveDataItem(liveData, isEven, isLast) {

    override fun bind(viewHolder: ViewHolder, position: Int) {
        super.bind(viewHolder, position)
        viewHolder.title.text = String.format(viewHolder.itemView.resources.getString(R.string.from), liveData.origin)
    }

}
