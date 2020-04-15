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
            Observable.concat(
                regularSearch(sanitizedQuery, serviceLocations),
                idSearch(sanitizedQuery, serviceLocations)
            )
        } else {
            regularSearch(sanitizedQuery, serviceLocations)
        }
    }

    private fun regularSearch(
        sanitizedQuery: String,
        serviceLocations: List<ServiceLocation>
    ): Observable<List<ServiceLocation>> {
        return Observable.just(
            FuzzySearch.extractTop(
                sanitizedQuery,
                serviceLocations,
                defaultToStringFunction(),
                50
            ).map { it.referent }
        )
    }

    private fun defaultToStringFunction(): (item: ServiceLocation) -> String =
        {
            if (it is StopLocation) {
                "$${it.name} ${it.service.fullName} ${it.routeGroups.map { routeGroup -> routeGroup.operator }.joinToString(separator = " ")}"
            } else {
                "$${it.name} ${it.service.fullName}"
            }
        }


    private fun idSearch(
        sanitizedQuery: String,
        serviceLocations: List<ServiceLocation>
    ): Observable<List<ServiceLocation>> {
        return Observable.just(
            FuzzySearch.extractTop(
                sanitizedQuery,
                serviceLocations.filter { it.service == Service.DUBLIN_BUS },
                { it.id },
                50
            ).map { it.referent }
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
