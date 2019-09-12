package ie.dublinmapper.util

import javax.net.ssl.SSLContext

interface SslContextProvider {

    fun sslContext(): SSLContext

}
