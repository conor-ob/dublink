package ie.dublinmapper.util

import android.content.Context
import java.security.SecureRandom
import java.security.cert.CertificateException
import java.security.cert.X509Certificate
import javax.net.ssl.*

class AndroidAssetSslContextProvider(private val context: Context) : SslContextProvider {

    override fun sslContext(): SSLContext {
        HttpsURLConnection.setDefaultHostnameVerifier { _, _ -> true }
        val sslContext = SSLContext.getInstance("TLS")
        sslContext.init(null, arrayOf<X509TrustManager>(object : X509TrustManager {
            override fun checkClientTrusted(chain: Array<out X509Certificate>?, authType: String?) {

            }

            override fun checkServerTrusted(chain: Array<out X509Certificate>?, authType: String?) {

            }

            override fun getAcceptedIssuers(): Array<X509Certificate> {
                return  emptyArray()
            }

        }), SecureRandom())
        HttpsURLConnection.setDefaultSSLSocketFactory(
            sslContext.socketFactory
        )
        return sslContext
    }

//    override fun sslContext(): SSLContext {
////        return SslUtils.getSslContextForCertificateFile(context, "tracker_aircoach_ie_certificate.pem")
//
//        // Load CAs from an InputStream
//        // (could be from a resource or ByteArrayInputStream or ...)
//        val cf: CertificateFactory = CertificateFactory.getInstance("X.509")
//        // From https://www.washington.edu/itconnect/security/ca/load-der.crt
////        val caInput: InputStream = BufferedInputStream(FileInputStream("load-der.crt"))
//        val caInput = context.assets.open("tracker_aircoach_ie_certificate.pem")
//        val ca: X509Certificate = caInput.use {
//            cf.generateCertificate(it) as X509Certificate
//        }
//        System.out.println("ca=" + ca.subjectDN)
//
//        // Create a KeyStore containing our trusted CAs
//        val keyStoreType = KeyStore.getDefaultType()
//        val keyStore = KeyStore.getInstance(keyStoreType).apply {
//            load(null, null)
//            setCertificateEntry("ca", ca)
//        }
//
//        // Create a TrustManager that trusts the CAs inputStream our KeyStore
//        val tmfAlgorithm: String = TrustManagerFactory.getDefaultAlgorithm()
//        val tmf: TrustManagerFactory = TrustManagerFactory.getInstance(tmfAlgorithm).apply {
//            init(keyStore)
//        }
//
//        // Create an SSLContext that uses our TrustManager
//        val sslContext: SSLContext = SSLContext.getInstance("TLS").apply {
//            init(null, tmf.trustManagers, null)
//        }
//
//        return sslContext
//
//        // Tell the URLConnection to use a SocketFactory from our SSLContext
////        val url = URL("https://certs.cac.washington.edu/CAtest/")
////        val urlConnection = url.openConnection() as HttpsURLConnection
////        urlConnection.sslSocketFactory = sslContext.socketFactory
////        val inputStream: InputStream = urlConnection.inputStream
////        copyInputStreamToOutputStream(inputStream, System.out)
//    }

}
