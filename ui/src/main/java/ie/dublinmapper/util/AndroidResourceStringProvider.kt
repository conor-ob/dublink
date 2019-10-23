package ie.dublinmapper.util

import android.content.Context
import android.content.pm.PackageManager
import android.content.res.Resources
import ie.dublinmapper.ui.R

class AndroidResourceStringProvider(
    private val context: Context,
    private val resources: Resources
) : StringProvider {

    override fun jcDecauxApiKey(): String {
        try {
            val appInfo = context.packageManager.getApplicationInfo(context.packageName, PackageManager.GET_META_DATA)
            return if (appInfo.metaData == null) {
                throw IllegalArgumentException("meta-data cannot be null")
            } else {
                appInfo.metaData.getString("com.developer.jcdecaux.API_KEY")!!
            }
        } catch (e: PackageManager.NameNotFoundException) {
            throw IllegalArgumentException("meta-data name not found")
        } catch (e: NullPointerException) {
            throw IllegalArgumentException("meta-data name not found")
        }
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
