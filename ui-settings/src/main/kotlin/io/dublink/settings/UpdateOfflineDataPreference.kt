package io.dublink.settings

import android.content.Context
import android.util.AttributeSet
import android.view.ViewGroup
import android.widget.ImageView
import androidx.preference.Preference
import androidx.preference.PreferenceViewHolder

class UpdateOfflineDataPreference : Preference {

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    private lateinit var imageView: ImageView

    override fun onBindViewHolder(holder: PreferenceViewHolder) {
        super.onBindViewHolder(holder)
        val viewGroup = holder.itemView.findViewById(android.R.id.widget_frame) as ViewGroup?
        if (viewGroup != null) {
            val count = holder.itemView.child
            for (i in 0 until count) {
                val child = viewGroup.getChildAt(i)
                if (child is ImageView) {
                    imageView = child
                    break
                }
            }
        }
    }
}
