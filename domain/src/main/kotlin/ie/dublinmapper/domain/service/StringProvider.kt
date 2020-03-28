package ie.dublinmapper.domain.service

interface StringProvider {

    fun jcDecauxApiKey(): String

    fun databaseName(): String

    fun departures(): String

    fun terminating(): String

    fun favourites(): String

    fun nearby(): String
}
