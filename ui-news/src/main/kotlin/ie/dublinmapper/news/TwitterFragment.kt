package ie.dublinmapper.news

import android.content.res.Configuration
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.twitter.sdk.android.tweetui.TweetTimelineRecyclerViewAdapter
import com.twitter.sdk.android.tweetui.TwitterListTimeline
import ie.dublinmapper.DublinMapperFragment
import kotlinx.android.synthetic.main.fragment_twitter.*

class TwitterFragment : DublinMapperFragment(R.layout.fragment_twitter) {

    private lateinit var args: TwitterArgs

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        args = fromBundle(requireArguments())

        val timeline = TwitterListTimeline.Builder()
            .slugWithOwnerScreenName(args.listName, "dublinbuspal")
            .includeRetweets(java.lang.Boolean.TRUE)
            .build()
        val adapter = TweetTimelineRecyclerViewAdapter.Builder(context)
            .setTimeline(timeline)
            .setViewStyle(getViewStyle())
            .build()

        twitterFeed.adapter = adapter
        twitterFeed.setHasFixedSize(true)
        twitterFeed.layoutManager = LinearLayoutManager(requireContext())
    }

    private fun getViewStyle(): Int {
        return when (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) {
            Configuration.UI_MODE_NIGHT_NO -> R.style.tw__TweetLightStyle
            Configuration.UI_MODE_NIGHT_YES -> R.style.TwitterFeed
            else -> R.style.tw__TweetLightStyle
        }
    }

    companion object {

        private const val listNameKey = "listName"

        data class TwitterArgs(
            val listName: String
        )

        fun toBundle(
            listName: String
        ) = Bundle().apply {
            putString(listNameKey, listName)
        }

        fun fromBundle(
            bundle: Bundle
        ) = TwitterArgs(
            listName = requireNotNull(bundle.getString(listNameKey))
        )
    }
}
