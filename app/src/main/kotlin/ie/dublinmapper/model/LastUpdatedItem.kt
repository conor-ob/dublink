package ie.dublinmapper.model

import android.view.View
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.widget.TextView
import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import ie.dublinmapper.R
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.list_item_live_data_last_updated.*
import java.util.concurrent.TimeUnit

class LastUpdatedItem(private val lastUpdated: Long) : Item() {

    private val subscriptions = CompositeDisposable()
    private lateinit var textView: TextView
    private lateinit var textViewText: String
    private val fadeIn = AlphaAnimation(0.0f, 1.0f).apply {
        duration = 3500L
    }
    private val fadeOut = AlphaAnimation(1.0f, 0.0f).apply {
        duration = 3500L
    }

    init {
        fadeIn.setAnimationListener(object : Animation.AnimationListener {

            override fun onAnimationStart(animation: Animation?) {
                textView.visibility = View.VISIBLE
                textView.text = textViewText
            }

            override fun onAnimationEnd(animation: Animation?) {
                textView.startAnimation(fadeOut)
            }

            override fun onAnimationRepeat(animation: Animation?) {

            }

        })
        fadeOut.setAnimationListener(object : Animation.AnimationListener {

            override fun onAnimationStart(animation: Animation?) {

            }

            override fun onAnimationEnd(animation: Animation?) {
                textView.visibility = View.GONE
            }

            override fun onAnimationRepeat(animation: Animation?) {

            }

        })
    }

    override fun getLayout() = R.layout.list_item_live_data_last_updated

    override fun bind(viewHolder: ViewHolder, position: Int) {
        textView = viewHolder.lastUpdated
        subscriptions.add(Observable.interval(0L, 15L, TimeUnit.SECONDS)
            .doOnNext {
                val now = System.currentTimeMillis()
                val seconds = TimeUnit.MILLISECONDS.toSeconds(now - lastUpdated)
                textViewText = when {
                    seconds < 5L -> viewHolder.itemView.context.getString(R.string.live_data_just_updated)
                    seconds >= 60L -> {
                        val minutes = TimeUnit.SECONDS.toMinutes(seconds)
                        viewHolder.itemView.context.resources.getQuantityString(R.plurals.live_data_last_updated_minutes, minutes.toInt(), minutes.toInt())
                    }
                    else -> viewHolder.itemView.context.getString(R.string.live_data_last_updated_seconds, seconds)
                }
                viewHolder.lastUpdated.startAnimation(fadeIn)
            }
            .subscribe()
        )
    }

    //TODO
//    override fun unbind(holder: ViewHolder) {
//        Timber.d("unbind")
//        subscriptions.clear()
//        subscriptions.dispose()
//        super.unbind(holder)
//    }

    override fun isSameAs(other: com.xwray.groupie.Item<*>?): Boolean {
        if (other is LastUpdatedItem) {
            return id == other.id
        }
        return false
    }

    override fun equals(other: Any?): Boolean {
        if (other is LastUpdatedItem) {
            return id == other.id
        }
        return false
    }

    override fun hashCode(): Int {
        return id.toInt()
    }

}
