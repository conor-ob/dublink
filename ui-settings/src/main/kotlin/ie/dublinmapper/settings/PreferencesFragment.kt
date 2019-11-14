package ie.dublinmapper.settings

import android.content.Context
import android.os.Build
import android.os.Bundle
import androidx.preference.ListPreference
import androidx.preference.PreferenceFragmentCompat
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasAndroidInjector
import dagger.android.support.AndroidSupportInjection
import javax.inject.Inject

class PreferencesFragment : PreferenceFragmentCompat(), HasAndroidInjector {

@Inject
    lateinit var androidInjector: DispatchingAndroidInjector<Any>
    @Inject
    lateinit var themeRepository: ThemeRepository

    override fun onAttach(context: Context) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            setPreferencesFromResource(R.xml.preferences, rootKey)
        } else {
            setPreferencesFromResource(R.xml.preferences_legacy, rootKey)
        }
        bindListeners()
    }

    private fun bindListeners() {
        val preferredThemePreference = findPreference<ListPreference>(getString(R.string.preference_preferred_theme))
        preferredThemePreference?.setOnPreferenceChangeListener { _, newValue ->
            themeRepository.setTheme(newValue as String)
            return@setOnPreferenceChangeListener true
        }
    }

    override fun androidInjector() = androidInjector

}
