package io.dublink.settings

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreference
import com.nodesagency.logviewer.LogViewerActivity
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasAndroidInjector
import dagger.android.support.AndroidSupportInjection
import io.dublink.domain.service.AppConfig
import io.dublink.domain.service.PermissionChecker
import javax.inject.Inject

private const val sortByLocationRequestCode = 42069

class PreferencesFragment : PreferenceFragmentCompat(), HasAndroidInjector {

    @Inject
    lateinit var androidInjector: DispatchingAndroidInjector<Any>
    @Inject
    lateinit var themeRepository: ThemeRepository
    @Inject
    lateinit var appConfig: AppConfig
    @Inject
    lateinit var permissionChecker: PermissionChecker

    override fun onAttach(context: Context) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences, rootKey)
        setAppVersionPreference()
        bindListeners()
    }

    private fun setAppVersionPreference() {
        val appVersionPreference = findPreference(getString(R.string.preference_key_app_version)) as Preference?
        appVersionPreference?.summary = appConfig.appVersion()
    }

    private fun bindListeners() {
        val preferredThemePreference = findPreference<ListPreference>(getString(R.string.preference_key_preferred_theme))
        preferredThemePreference?.setOnPreferenceChangeListener { _, newValue ->
            themeRepository.setTheme(newValue as String)
            return@setOnPreferenceChangeListener true
        }
        val sortByLocationPreference = findPreference<SwitchPreference>(getString(R.string.preference_key_favourites_sort_by_location))
        sortByLocationPreference?.setOnPreferenceChangeListener { preference, newValue ->
            if (newValue is Boolean) {
                if (newValue) {
                    if (permissionChecker.isLocationPermissionGranted()) {
                        return@setOnPreferenceChangeListener true
                    } else {
                        requestLocationPermissions()
                    }
                } else {
                    return@setOnPreferenceChangeListener true
                }
            }
            return@setOnPreferenceChangeListener false
        }
        if (BuildConfig.DEBUG) {
            findPreference<Preference>(getString(R.string.preference_key_app_version))?.apply {
                setOnPreferenceClickListener {
                    LogViewerActivity
                        .createIntent(activity!!.applicationContext)
                        .let { startActivity(it) }
                    return@setOnPreferenceClickListener true
                }
            }
        }
    }

    private fun requestLocationPermissions() {
        requestPermissions(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ),
            sortByLocationRequestCode
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == sortByLocationRequestCode) {
            if (grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
                val sortByLocationPreference = findPreference<SwitchPreference>(getString(R.string.preference_key_favourites_sort_by_location))
                sortByLocationPreference?.isChecked = true
            }
        }
    }

    override fun androidInjector() = androidInjector
}
