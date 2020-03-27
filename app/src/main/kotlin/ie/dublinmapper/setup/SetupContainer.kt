package ie.dublinmapper.setup

import android.app.Application

interface SetupContainer {

    fun setup(application: Application)
}
