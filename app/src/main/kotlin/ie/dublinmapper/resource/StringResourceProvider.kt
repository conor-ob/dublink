package ie.dublinmapper.resource

import android.content.res.Resources
import ie.dublinmapper.BuildConfig
import ie.dublinmapper.core.StringProvider
import ie.dublinmapper.core.R

class StringResourceProvider(
    private val resources: Resources
) : StringProvider {

    override fun jcDecauxApiKey(): String {
        return BuildConfig.JCDECAUX_API_KEY
    }

    override fun databaseName(): String {
        return resources.getString(R.string.database_name)
    }

    override fun departures(): String {
        return resources.getString(R.string.departures)
    }

    override fun terminating(): String {
        return resources.getString(R.string.terminating)
    }

    override fun favourites(): String {
        return resources.getString(R.string.title_favourites)
    }

    override fun nearby(): String {
        return resources.getString(R.string.title_nearby)
    }
}
