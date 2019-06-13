package ie.dublinmapper.view.livedata

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.xwray.groupie.Section
import ie.dublinmapper.view.DublinMapperAdapter
import timber.log.Timber

class DartLiveDataItemDecoration : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        super.getItemOffsets(outRect, view, parent, state)
        val position = parent.getChildAdapterPosition(view)
        val groupAdapter = parent.adapter as DublinMapperAdapter
        for (group in groupAdapter.getGroups()) {
            if (group is Section) {
                for (i in 0 until group.groupCount) {
                    val item = group.getItem(i)
                }
            }
        }
        Timber.d("test")
    }

}