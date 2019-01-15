package ie.dublinmapper.util

import org.simpleframework.xml.core.Persister
import java.io.StringWriter

object MockXmlUtils {

    private val serializer by lazy { Persister() }
    private val classLoader by lazy { javaClass.classLoader }

    fun <T> deserialize(fileName: String, type: Class<T>): T {
        val url = classLoader.getResource(fileName)
        return serializer.read(type, url.openStream())
    }

    fun <T> resolveXml(fileName: String, type: Class<T>): Boolean {
        val url = classLoader.getResource(fileName)
        val derivedFromResource = serializer.read(type, url.openStream())
        val writer = StringWriter()
        serializer.write(derivedFromResource, writer)
        val derivedFromWrite = serializer.read(type, writer.toString())
        return derivedFromResource == derivedFromWrite
    }

}
