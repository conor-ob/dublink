package io.dublink.settings

import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.commitNow
import androidx.navigation.fragment.findNavController
import io.dublink.DubLinkFragment

class SettingsFragment : DubLinkFragment(R.layout.fragment_settings) {

    private val preferencesFragment by lazy { PreferencesFragment() }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.findViewById<Toolbar>(R.id.settings_toolbar).apply {
            setNavigationOnClickListener {
                findNavController().navigateUp()
            }
        }
        childFragmentManager.commitNow {
            replace(R.id.settings_container, preferencesFragment)
        }
    }
}
