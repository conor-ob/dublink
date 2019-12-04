package ie.dublinmapper.init

import android.app.Application
import com.jakewharton.threetenabp.AndroidThreeTen

class ThreeTenInitializer : ApplicationInitializer {

    override fun initialize(application: Application) {
        AndroidThreeTen.init(application)
    }

}
