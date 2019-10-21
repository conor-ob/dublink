package ie.dublinmapper

import ie.dublinmapper.util.StringProvider

object MockStringProvider : StringProvider {

    override fun jcDecauxApiKey(): String {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun databaseName(): String {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun departures() = "Departures"

    override fun terminating() = "Terminating"

    override fun favourites(): String {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}
