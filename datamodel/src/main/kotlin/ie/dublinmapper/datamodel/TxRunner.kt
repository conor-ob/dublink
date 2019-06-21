package ie.dublinmapper.datamodel

interface TxRunner {

    fun runInTx(task: () -> Unit)

}
