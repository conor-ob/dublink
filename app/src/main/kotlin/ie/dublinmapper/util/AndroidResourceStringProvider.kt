package ie.dublinmapper.util

import android.content.Context
import android.content.pm.PackageManager
import android.content.res.Resources
import ie.dublinmapper.R
import java.lang.NullPointerException

class AndroidResourceStringProvider(
    private val context: Context,
    private val resources: Resources
) : StringProvider {

    override fun jcdecauxBaseUrl(): String {
        return resources.getString(R.string.jcdecaux_base_url)
    }

    override fun jcdecauxContract(): String {
        return resources.getString(R.string.jcdecaux_contract)
    }

    // copied from MetadataUtils
    override fun jcdecauxApiKey(): String {
        try {
            val appInfo = context.packageManager.getApplicationInfo(context.packageName, PackageManager.GET_META_DATA)
            return if (appInfo.metaData == null) {
                throw IllegalArgumentException("meta-data cannot be null")
            } else {
                appInfo.metaData.getString("api.jcdecaux.API_KEY")!!
            }
        } catch (e: PackageManager.NameNotFoundException) {
            throw IllegalArgumentException("meta-data name not found")
        } catch (e: NullPointerException) {
            throw IllegalArgumentException("meta-data name not found")
        }
    }

    override fun rtpibaseUrl(): String {
        return resources.getString(R.string.rtpi_base_url)
    }

    override fun rtpiOperatoreLuas(): String {
        return resources.getString(R.string.rtpi_operator_luas)
    }

    override fun rtpiOperatoreDublinBus(): String {
        return resources.getString(R.string.rtpi_operator_dublin_bus)
    }

    override fun rtpiFormat(): String {
        return resources.getString(R.string.rtpi_format)
    }

    override fun irishRailBaseUrl(): String {
        return resources.getString(R.string.irish_rail_api_base_url)
    }

    override fun irishRailApiDartStationType(): String {
        return resources.getString(R.string.irish_rail_api_dart_station_type)
    }

}
