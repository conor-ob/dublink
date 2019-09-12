package ie.dublinmapper

import javax.net.ssl.SSLContext

interface SslContextProvider {

    fun sslContext(): SSLContext

}
