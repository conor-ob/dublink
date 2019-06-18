package ie.dublinmapper.util

import android.content.Context
import android.content.pm.PackageManager
import android.content.res.Resources
import ie.dublinmapper.R

class AndroidResourceStringProvider(
    private val context: Context,
    private val resources: Resources
) : StringProvider {

    override fun jcDecauxBaseUrl(): String {
        return resources.getString(R.string.jcdecaux_base_url)
    }

    override fun jcDecauxContract(): String {
        return resources.getString(R.string.jcdecaux_contract)
    }

    // copied from MetadataUtils
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

    override fun rtpiBaseUrl(): String {
        return resources.getString(R.string.rtpi_base_url)
    }

    override fun rtpiOperatorLuas(): String {
        return resources.getString(R.string.rtpi_operator_luas)
    }

    override fun rtpiOperatorBusEireann(): String {
        return resources.getString(R.string.rtpi_operator_bus_eireann)
    }

    override fun rtpiOperatorDublinBus(): String {
        return resources.getString(R.string.rtpi_operator_dublin_bus)
    }

    override fun rtpiOperatorGoAhead(): String {
        return resources.getString(R.string.rtpi_operator_go_ahead)
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

    override fun dublinBusBaseUrl(): String {
        return resources.getString(R.string.dublin_bus_api_base_url)
    }

    override fun swordsExpressBaseUrl(): String {
        return resources.getString(R.string.swords_express_api_base_url)
    }

    override fun githubBaseUrl(): String {
        return resources.getString(R.string.github_base_url)
    }

    override fun aircoachBaseUrl(): String {
        return resources.getString(R.string.aircoach_base_url)
    }

    override fun aircoachHost(): String {
        return resources.getString(R.string.aircoach_host)
    }

    override fun aircoachPort(): String {
        return resources.getString(R.string.aircoach_port)
    }

    override fun databaseName(): String {
        return resources.getString(R.string.database_name)
    }

}
