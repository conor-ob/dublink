package ie.dublinmapper.util

interface StringProvider {

    fun jcdecauxBaseUrl(): String

    fun jcDecauxContract(): String

    fun jcDecauxApiKey(): String

    fun rtpibaseUrl(): String

    fun rtpiOperatoreLuas(): String

    fun rtpiFormat(): String

    fun irishRailBaseUrl(): String

    fun irishRailApiDartStationType(): String

    fun dublinBusBaseUrl(): String

    fun rtpiOperatoreBusEireann(): String

    fun rtpiOperatoreDublinBus(): String

    fun rtpiOperatoreGoAhead(): String

    fun swordsExpressBaseUrl(): String

    fun aircoachBaseUrl(): String

    fun githubBaseUrl(): String

}
