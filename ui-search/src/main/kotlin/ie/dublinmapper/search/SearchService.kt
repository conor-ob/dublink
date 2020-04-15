package ie.dublinmapper.search

import io.reactivex.Observable
import io.rtpi.api.Service
import io.rtpi.api.ServiceLocation
import io.rtpi.api.StopLocation
import me.xdrop.fuzzywuzzy.FuzzySearch
import me.xdrop.fuzzywuzzy.ToStringFunction

class SearchService {

    fun search(query: String, serviceLocations: List<ServiceLocation>): Observable<List<ServiceLocation>> {
        val sanitizedQuery = sanitizeQuery(query)
        return if (isId(sanitizedQuery)) {
            idSearch(sanitizedQuery, serviceLocations)
        } else {
            regularSearch(sanitizedQuery, serviceLocations)
        }
    }

    private fun regularSearch(
        query: String,
        serviceLocations: List<ServiceLocation>
    ): Observable<List<ServiceLocation>> {
        return search(
            query = query,
            serviceLocations = serviceLocations,
            toStringFunction = ToStringFunction<ServiceLocation> {
                if (it is StopLocation) {
                    listOf(
                        it.name,
                        it.service.fullName
                    ).plus(
                        it.routeGroups.map { routeGroup -> routeGroup.operator.fullName }
                    )
                } else {
                    listOf(
                        it.name,
                        it.service.fullName
                    )
                }
                    .toSet()
                    .joinToString(separator = " ")
//                    .joinToString(separator = " ") { value -> DefaultStringFunction.subNonAlphaNumeric(value.toLowerCase(), " ") }
            }
        )
    }

    private fun idSearch(
        query: String,
        serviceLocations: List<ServiceLocation>
    ): Observable<List<ServiceLocation>> {
        return search(
            query = query,
            serviceLocations = serviceLocations.filter {
                it.service == Service.DUBLIN_BUS || it.service == Service.BUS_EIREANN
            },
            toStringFunction = ToStringFunction<ServiceLocation> { it.id }
        )
    }

    private fun search(
        query: String,
        serviceLocations: List<ServiceLocation>,
        toStringFunction: ToStringFunction<ServiceLocation>
    ): Observable<List<ServiceLocation>> {
        return Observable.just(
            FuzzySearch.extractSorted(
                query,
                serviceLocations,
                toStringFunction
            )
                .map { it.referent }
        )
    }

    private fun isId(value: String): Boolean {
        try {
            value.toInt()
        } catch (e : NumberFormatException) {
            return false
        }
        return true
    }

    private fun sanitizeQuery(query: String): String {
        return query.trim() // TODO
    }
}
