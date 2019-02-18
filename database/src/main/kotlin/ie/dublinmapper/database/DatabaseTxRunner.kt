package ie.dublinmapper.database

import ie.dublinmapper.data.TxRunner


class DatabaseTxRunner(private val database: DublinMapperDatabase) : TxRunner {

    override fun runInTx(task: () -> Unit) = database.runInTransaction(task)

}
