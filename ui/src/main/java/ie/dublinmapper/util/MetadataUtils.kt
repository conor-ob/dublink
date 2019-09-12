package ie.dublinmapper.util

import android.content.Context
import android.content.pm.PackageManager
import java.lang.NullPointerException

object MetadataUtils {

    fun getMetadata(context: Context, name: String): String {
        try {
            val appInfo = context.packageManager.getApplicationInfo(context.packageName, PackageManager.GET_META_DATA)
            return if (appInfo.metaData == null) {
                throw IllegalArgumentException("meta-data cannot be null")
            } else {
                appInfo.metaData.getString(name)!!
            }
        } catch (e: PackageManager.NameNotFoundException) {
            throw IllegalArgumentException("meta-data name not found")
        } catch (e: NullPointerException) {
            throw IllegalArgumentException("meta-data name not found")
        }
    }

}
