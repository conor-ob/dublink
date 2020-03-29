package ie.dublinmapper.repository

import com.nytimes.android.external.store3.base.impl.room.StoreRoom
import ie.dublinmapper.domain.model.isFavourite
import ie.dublinmapper.domain.repository.Repository
import ie.dublinmapper.domain.service.EnabledServiceManager
import io.reactivex.Observable
import io.rtpi.api.Service
import io.rtpi.api.ServiceLocation
import java.io.IOException
import java.net.SocketException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

abstract class ServiceLocationRepository<T : ServiceLocation>(
    private val service: Service,
    private val serviceLocationStore: StoreRoom<List<T>, Service>,
    private val enabledServiceManager: EnabledServiceManager
) : Repository<T> {

    private var cache = emptyMap<String, T>()

    private fun fillCache(serviceLocations: List<T>) {
        cache = serviceLocations.associateBy { it.id }
    }

    override fun clearCache() {
        cache = emptyMap()
        serviceLocationStore.clear()
    }

    override fun getAll(): Observable<List<T>> {
        if (enabledServiceManager.isServiceEnabled(service)) {
            return serviceLocationStore.get(service)
                .doOnNext { serviceLocations -> fillCache(serviceLocations) }
                .onErrorReturn { e ->
                    val message = when (e) {
                        is IOException -> "Please check your internet connection"
                        else -> "Oops! Something went wrong. Try refreshing the page"
                    }
                    emptyList()
                }
        }
        return Observable.just(emptyList())
    }

    override fun getAllFavorites(): Observable<List<T>> {
        return getAll().map { it.filter { serviceLocation -> serviceLocation.isFavourite() } }
    }

    override fun getById(id: String): Observable<T> {
        val serviceLocation = cache[id]
        if (serviceLocation != null) {
            return Observable.just(serviceLocation)
        }
        return getAll().map { serviceLocations -> serviceLocations.find { it.id == id } }
    }

    override fun refresh(): Observable<Boolean> {
        return serviceLocationStore.fetch(service)
            .doOnNext { serviceLocations -> fillCache(serviceLocations) }
            .map { true }
    }

    override fun getAllById(id: String): Observable<List<T>> {
        throw UnsupportedOperationException()
    }
}
