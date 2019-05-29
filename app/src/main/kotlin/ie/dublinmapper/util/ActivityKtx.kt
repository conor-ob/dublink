package ie.dublinmapper.util

import android.app.Activity
import ie.dublinmapper.DublinMapperApplication
import ie.dublinmapper.di.ApplicationComponent

fun Activity.getApplicationComponent(): ApplicationComponent {
    return (application as DublinMapperApplication).applicationComponent
}
