package io.dublink.search

import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import com.xwray.groupie.kotlinandroidextensions.Item
import io.dublink.search.R
import kotlinx.android.synthetic.main.list_item_search_no_results.view.*

class NoSearchResultsItem(
    id: Long,
    private val query: String
) : Item(id) {

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

    override fun equals(other: Any?) =
        if (other is NoSearchResultsItem) {
            query == other.query
        } else {
            false
        }

    override fun hashCode() = query.hashCode()
}
