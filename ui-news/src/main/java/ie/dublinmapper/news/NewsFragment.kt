package ie.dublinmapper.news

import android.content.pm.PackageManager
import android.content.res.Configuration
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.twitter.sdk.android.core.Twitter
import com.twitter.sdk.android.core.TwitterAuthConfig
import com.twitter.sdk.android.core.TwitterConfig
import com.twitter.sdk.android.tweetui.TweetTimelineRecyclerViewAdapter
import com.twitter.sdk.android.tweetui.TwitterListTimeline
import ie.dublinmapper.ui.DublinMapperFragment
import kotlinx.android.synthetic.main.fragment_news.*

class NewsFragment : DublinMapperFragment(R.layout.fragment_news) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val appInfo = requireContext().applicationContext.packageManager.getApplicationInfo(
            requireContext().applicationContext.packageName, PackageManager.GET_META_DATA
        )
        val config = TwitterConfig.Builder(requireContext().applicationContext)
            .twitterAuthConfig(
                TwitterAuthConfig(
                    appInfo.metaData.getString("com.twitter.sdk.android.CONSUMER_KEY"),
                    appInfo.metaData.getString("com.twitter.sdk.android.CONSUMER_SECRET")
                )
            ).build()
        Twitter.initialize(config)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val timeline = TwitterListTimeline.Builder()
            .slugWithOwnerScreenName("dublin-bus", "dublinbuspal")
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

}
