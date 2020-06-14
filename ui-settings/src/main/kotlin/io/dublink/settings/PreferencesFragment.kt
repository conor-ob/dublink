package io.dublink.settings

import android.Manifest
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreference
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasAndroidInjector
import dagger.android.support.AndroidSupportInjection
import io.dublink.DubLinkNavigator
import io.dublink.domain.service.AppConfig
import io.dublink.domain.service.PermissionChecker
import io.dublink.domain.service.PreferenceStore
import io.dublink.domain.service.ThemeService
import javax.inject.Inject

private const val sortByLocationRequestCode = 42069

class PreferencesFragment : PreferenceFragmentCompat(), HasAndroidInjector {

    @Inject
    lateinit var androidInjector: DispatchingAndroidInjector<Any>
    @Inject
    lateinit var themeService: ThemeService
    @Inject
    lateinit var appConfig: AppConfig
    @Inject
    lateinit var permissionChecker: PermissionChecker
    @Inject
    lateinit var preferenceStore: PreferenceStore

    override fun onAttach(context: Context) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(
            if (preferenceStore.isDubLinkProEnabled()) {
                R.xml.preferences_pro
            } else {
                R.xml.preferences
            },
            rootKey
        )
        setAppVersionPreference()
        bindListeners()
    }

    override fun onResume() {
        super.onResume()
        if (preferenceStore.isDubLinkProEnabled()) {
            val upgradePreference = findPreference<Preference>(getString(R.string.preference_key_dublink_pro_upgrade))
            if (upgradePreference != null) {
                // user has just purchased pro so rebuild the screen
                onCreatePreferences(null, null)
            }
        }
    }

    private fun setAppVersionPreference() {
        val appVersionPreference = findPreference(getString(R.string.preference_key_app_version)) as Preference?
        appVersionPreference?.summary = appConfig.appVersion()
    }

    private fun bindListeners() {
        findPreference<Preference>(getString(R.string.preference_key_dublink_pro_upgrade))?.apply {
            setOnPreferenceClickListener {
                (activity as DubLinkNavigator).navigateToIap()
                return@setOnPreferenceClickListener true
            }
        }
        val preferredThemePreference = findPreference<ListPreference>(getString(R.string.preference_key_preferred_theme))
        preferredThemePreference?.setOnPreferenceChangeListener { _, newValue ->
            themeService.setTheme(newValue as String)
            return@setOnPreferenceChangeListener true
        }
        val sortByLocationPreference = findPreference<SwitchPreference>(getString(R.string.preference_key_favourites_sort_by_location))
        sortByLocationPreference?.setOnPreferenceChangeListener { _, newValue ->
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
        findPreference<Preference>(getString(R.string.preference_key_contact_send_feedback))?.apply {
            setOnPreferenceClickListener {
                val selectorIntent = Intent(Intent.ACTION_SENDTO)
                selectorIntent.data = Uri.parse("mailto:")
                val emailBody = """
                    Type your feedback here
                    
                    
                    -----------------------------
                    Please don't remove this information
                    App Version: ${appConfig.appVersion()}
                    Device OS: Android
                    Device OS Version: ${Build.VERSION.RELEASE}
                    Device Brand: ${Build.BRAND}
                    Device Model: ${Build.MODEL}
                    Device Manufacturer: ${Build.MANUFACTURER}
                    """.trimIndent()
                val emailIntent = Intent(Intent.ACTION_SEND)
                emailIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf("dublink.io@gmail.com"))
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, "DubLink Feedback")
                emailIntent.putExtra(Intent.EXTRA_TEXT, emailBody)
                emailIntent.selector = selectorIntent

                try {
                    context.startActivity(Intent.createChooser(emailIntent, "Send feedback"))
                } catch (e: ActivityNotFoundException) {
                    Toast.makeText(context, "No email app found", Toast.LENGTH_SHORT).show()
                }
                return@setOnPreferenceClickListener true
            }
        }
//        findPreference<Preference>(getString(R.string.preference_key_contact_rate))?.apply {
//            setOnPreferenceClickListener {
//                val uri = Uri.parse("market://details?id=" + context.packageName)
//                val playStoreIntent = Intent(Intent.ACTION_VIEW, uri)
//                playStoreIntent.addFlags(
//                    Intent.FLAG_ACTIVITY_NO_HISTORY or
//                        Intent.FLAG_ACTIVITY_NEW_DOCUMENT or
//                        Intent.FLAG_ACTIVITY_MULTIPLE_TASK
//                )
//                try {
//                    context.startActivity(playStoreIntent)
//                } catch (e: ActivityNotFoundException) {
//                    context.startActivity(
//                        Intent(
//                            Intent.ACTION_VIEW,
//                            Uri.parse(
//                                "http://play.google.com/store/apps/details?id="
//                                    + context.packageName
//                            )
//                        )
//                    )
//                }
//                return@setOnPreferenceClickListener true
//            }
//        }
//        findPreference<Preference>(getString(R.string.preference_key_contact_share))?.apply {
//            setOnPreferenceClickListener {
//                val shareIntent = Intent()
//                shareIntent.action = Intent.ACTION_SEND
//                shareIntent.putExtra(Intent.EXTRA_SUBJECT, "DubLink")
//                shareIntent.putExtra(
//                    Intent.EXTRA_TEXT,
//                    """
//                    The best app for getting around Dublin!
//
//                    https://play.google.com/store/apps/details?id=${context.packageName}
//                    """.trimIndent()
//                )
//                shareIntent.type = "text/plain"
//                context.startActivity(shareIntent)
//                return@setOnPreferenceClickListener true
//            }
//        }
        val privacyPolicyPreference = findPreference<Preference>(getString(R.string.preference_key_privacy_policy))
        privacyPolicyPreference?.setOnPreferenceClickListener {
            (activity as DubLinkNavigator).navigateToWebView(
                title = getString(R.string.preference_name_privacy_policy),
                url = "https://conor-ob.github.io/dublink/privacypolicy/"
            )
            return@setOnPreferenceClickListener true
        }
        val termsOfServicePreference = findPreference<Preference>(getString(R.string.preference_key_terms_of_service))
        termsOfServicePreference?.setOnPreferenceClickListener {
            (activity as DubLinkNavigator).navigateToWebView(
                title = getString(R.string.preference_name_terms_of_service),
                url = "https://conor-ob.github.io/dublink/termsofservice/"
            )
            return@setOnPreferenceClickListener true
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
