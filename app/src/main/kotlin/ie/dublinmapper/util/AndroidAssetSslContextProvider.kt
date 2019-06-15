package ie.dublinmapper.util

import android.content.Context
import com.mklimek.sslutilsandroid.SslUtils
import javax.net.ssl.SSLContext

class AndroidAssetSslContextProvider(private val context: Context) : SslContextProvider {

    override fun sslContext(): SSLContext {
        return SslUtils.getSslContextForCertificateFile(context, "tracker_aircoach_ie_certificate.pem")
    }

}
