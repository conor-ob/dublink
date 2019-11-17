package ie.dublinmapper.settings

import android.os.Bundle
import androidx.fragment.app.commitNow
import dagger.android.support.DaggerAppCompatActivity
import kotlinx.android.synthetic.main.activity_settings.*

class SettingsActivity : DaggerAppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        settings_toolbar.setNavigationOnClickListener {
            onBackPressed() //TODO
        }
        supportFragmentManager.commitNow {
            replace(R.id.settings_container, PreferencesFragment())
        }
    }
}
