package ie.dublinmapper.util

class MockStringProvider : StringProvider {
    
    override fun jcdecauxBaseUrl(): String {
        return ""
    }

    override fun jcDecauxContract(): String {
        return ""
    }

    override fun jcDecauxApiKey(): String {
        return ""
    }

    override fun rtpibaseUrl(): String {
        return ""
    }

    override fun rtpiOperatoreLuas(): String {
        return ""
    }

    override fun rtpiFormat(): String {
        return ""
    }

    override fun irishRailBaseUrl(): String {
        return ""
    }

    override fun irishRailApiDartStationType(): String {
        return ""
    }

    override fun dublinBusBaseUrl(): String {
        return ""
    }

    override fun rtpiOperatoreDublinBus(): String {
        return ""
    }

    override fun rtpiOperatoreGoAhead(): String {
        return ""
    }
    
}
