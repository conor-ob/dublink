package ie.dublinmapper.domain.repository

import io.reactivex.Observable

interface Repository<T> {

    fun getById(id: String): Observable<T>

    fun getAll(): Observable<List<T>>

    fun getAllFavorites(): Observable<List<T>>

    fun getAllById(id: String): Observable<List<T>>

    fun refresh(): Observable<Boolean>

    fun clearCache()

}
