package ie.dublinmapper.resource

import android.content.res.Resources
import ie.dublinmapper.BuildConfig
import ie.dublinmapper.domain.service.StringProvider
import ie.dublinmapper.ui.R

class StringResourceProvider(
    private val resources: Resources
) : StringProvider {

    override fun jcDecauxApiKey(): String {
        return BuildConfig.JCDECAUX_API_KEY
    }

    override fun databaseName(): String {
        return resources.getString(R.string.database_name)
    }
}
