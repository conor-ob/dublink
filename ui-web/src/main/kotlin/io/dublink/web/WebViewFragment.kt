package io.dublink.web

import android.os.Bundle
import android.view.View
import android.webkit.WebView
import androidx.appcompat.widget.Toolbar
import io.dublink.DubLinkFragment

class WebViewFragment : DubLinkFragment(R.layout.fragment_web_view) {

    private lateinit var args: WebViewArgs
    private lateinit var toolbar: Toolbar
    private lateinit var webView: WebView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        args = fromBundle(requireArguments())
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        toolbar = view.findViewById(R.id.web_view_toolbar)
        webView = view.findViewById(R.id.web_view)
        toolbar.setNavigationOnClickListener { activity?.onBackPressed() }
        toolbar.title = args.title
        webView.loadUrl(args.url)
    }

    companion object {

        private const val titleKey = "title"
        private const val urlKey = "url"

        data class WebViewArgs(
            val title: String,
            val url: String
        )

        fun toBundle(
            title: String,
            url: String
        ) = Bundle().apply {
            putString(titleKey, title)
            putString(urlKey, url)
        }

        private fun fromBundle(
            bundle: Bundle
        ) = WebViewArgs(
            title = requireNotNull(bundle.getString(titleKey)),
            url = requireNotNull(bundle.getString(urlKey))
        )
    }
}
