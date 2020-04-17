package ie.dublinmapper.search

import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import com.xwray.groupie.kotlinandroidextensions.Item
import kotlinx.android.synthetic.main.list_item_search_no_results.view.*

class NoSearchResultsItem(
    private val query: String,
    private val index: Int
) : Item() {

    override fun getLayout() = R.layout.list_item_search_no_results

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        val text = viewHolder.itemView.context.getString(R.string.message_search_no_results, query)
        val startIndex = text.indexOf("\"") + 1
        val endIndex = text.lastIndexOf("\"")
        viewHolder.itemView.list_item_search_no_results_message.text = SpannableString(text).apply {
            setSpan(
                ForegroundColorSpan(
                    viewHolder.itemView.context.getColor(R.color.color_primary)
                ),
                startIndex, endIndex,
                Spannable.SPAN_EXCLUSIVE_INCLUSIVE
            )
        }
    }

    override fun isSameAs(other: com.xwray.groupie.Item<*>): Boolean {
        if (other is NoSearchResultsItem) {
            return other.index == index
        }
        return false
    }

    override fun equals(other: Any?): Boolean {
        if (other is NoSearchResultsItem) {
            return other.query == query
        }
        return false
    }

    override fun hashCode(): Int {
        return query.hashCode()
    }
}
