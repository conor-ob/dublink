package ie.dublinmapper.startup

import android.app.Application
import com.twitter.sdk.android.core.Twitter
import com.twitter.sdk.android.core.TwitterAuthConfig
import com.twitter.sdk.android.core.TwitterConfig
import ie.dublinmapper.BuildConfig

class TwitterStartupWorker : StartupWorker {

    override fun startup(application: Application) {
        val config = TwitterConfig.Builder(application)
            .twitterAuthConfig(
                TwitterAuthConfig(
                    BuildConfig.TWITTER_CONSUMER_KEY,
                    BuildConfig.TWITTER_CONSUMER_SECRET
                )
            ).build()
        Twitter.initialize(config)
    }
}
