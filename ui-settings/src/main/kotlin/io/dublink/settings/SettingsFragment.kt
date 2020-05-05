package io.dublink.settings

import android.os.Bundle
import android.view.View
import androidx.fragment.app.commitNow
import androidx.navigation.fragment.findNavController
import io.dublink.DubLinkFragment
import kotlinx.android.synthetic.main.fragment_settings.*

class SettingsFragment : DubLinkFragment(R.layout.fragment_settings) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        settings_toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
        childFragmentManager.commitNow {
            replace(R.id.settings_container, PreferencesFragment())
        }
    }
}
