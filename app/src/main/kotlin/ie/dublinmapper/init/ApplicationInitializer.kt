package ie.dublinmapper.init

import android.app.Application

interface ApplicationInitializer {

    fun initialize(application: Application)
}
