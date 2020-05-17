package io.dublink.settings

import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.commitNow
import androidx.navigation.fragment.findNavController
import com.google.android.material.chip.Chip
import io.dublink.DubLinkFragment
import io.dublink.domain.service.PreferenceStore
import io.dublink.setVisibility
import javax.inject.Inject

class SettingsFragment : DubLinkFragment(R.layout.fragment_settings) {

    @Inject
    lateinit var preferenceStore: PreferenceStore

    private val preferencesFragment by lazy { PreferencesFragment() }

    private lateinit var dublinkProBadge: Chip

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.findViewById<Toolbar>(R.id.settings_toolbar).apply {
            setNavigationOnClickListener {
                findNavController().navigateUp()
            }
        }
        dublinkProBadge = view.findViewById(R.id.dublink_pro_badge)
        childFragmentManager.commitNow {
            replace(R.id.settings_container, preferencesFragment)
        }
    }

    override fun onResume() {
        super.onResume()
        dublinkProBadge.setVisibility(isVisible = preferenceStore.isDubLinkProEnabled())
    }
}
