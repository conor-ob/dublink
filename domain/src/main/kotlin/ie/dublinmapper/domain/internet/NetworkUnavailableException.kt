package ie.dublinmapper.domain.internet

import java.io.IOException

class NetworkUnavailableException : IOException() {

    override val message = "No Internet Connection"
}
