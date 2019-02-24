package ie.dublinmapper.data

interface TxRunner {

    fun runInTx(task: () -> Unit)

}
