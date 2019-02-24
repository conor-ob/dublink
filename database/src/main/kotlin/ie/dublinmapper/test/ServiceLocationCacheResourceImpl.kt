package ie.dublinmapper.test

import ie.dublinmapper.data.*
import ie.dublinmapper.data.test.*
import io.reactivex.Maybe

class ServiceLocationCacheResourceImpl(
    private val serviceLocationDao: ServiceLocationDao,
    private val locationDao: LocationDao,
    private val serviceDao: ServiceDao,
    private val txRunner: TxRunner
) : ServiceLocationCacheResource {

    override fun selectServiceLocations(operator: String): Maybe<List<ServiceLocationEntity>> {
        return serviceLocationDao.selectAll(operator)
    }

    override fun insertServiceLocations(serviceLocations: Pair<List<LocationEntity>, List<ServiceEntity>>) {
        txRunner.runInTx {
            locationDao.insertAll(serviceLocations.first)
            serviceDao.insertAll(serviceLocations.second)
        }
    }

}
