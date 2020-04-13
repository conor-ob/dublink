package ie.dublinmapper.domain.service

interface StringProvider {

    fun jcDecauxApiKey(): String

    fun databaseName(): String
}
