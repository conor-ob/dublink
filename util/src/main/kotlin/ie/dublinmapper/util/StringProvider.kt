package ie.dublinmapper.util

interface StringProvider {

    fun jcDecauxBaseUrl(): String

    fun jcDecauxContract(): String

    fun jcDecauxApiKey(): String

    fun rtpiBaseUrl(): String

    fun rtpiOperatorLuas(): String

    fun rtpiFormat(): String

    fun irishRailBaseUrl(): String

    fun irishRailApiDartStationType(): String

    fun dublinBusBaseUrl(): String

    fun rtpiOperatorBusEireann(): String

    fun rtpiOperatorDublinBus(): String

    fun rtpiOperatorGoAhead(): String

    fun swordsExpressBaseUrl(): String

    fun aircoachBaseUrl(): String

    fun githubBaseUrl(): String

}
