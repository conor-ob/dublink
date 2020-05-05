package io.dublink.resource

import android.content.res.Resources
import io.dublink.BuildConfig
import io.dublink.domain.service.StringProvider
import io.dublink.ui.R

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
