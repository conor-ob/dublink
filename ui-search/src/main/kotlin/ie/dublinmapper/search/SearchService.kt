package ie.dublinmapper.search

import io.reactivex.Observable
import io.rtpi.api.Service
import io.rtpi.api.ServiceLocation
import io.rtpi.api.StopLocation
import me.xdrop.fuzzywuzzy.Applicable
import me.xdrop.fuzzywuzzy.FuzzySearch
import me.xdrop.fuzzywuzzy.ToStringFunction
import me.xdrop.fuzzywuzzy.algorithms.TokenSet
import me.xdrop.fuzzywuzzy.algorithms.WeightedRatio

class SearchService {

    private val searchScoreCutoff = 70

    fun search(query: String, serviceLocations: List<ServiceLocation>): Observable<List<ServiceLocation>> {
        val algorithm = getAlgorithm(query)
        return if (isId(query)) {
            idSearch(query, algorithm, serviceLocations)
        } else {
            regularSearch(query, algorithm, serviceLocations)
        }
    }

    private fun getAlgorithm(query: String): Applicable {
        val tokens = query.split(" ") // TODO split("\\s+")
        return if (tokens.size > 1) {
            TokenSet()
        } else {
            WeightedRatio()
        }
    }

    private fun regularSearch(
        query: String,
        algorithm: Applicable,
        serviceLocations: List<ServiceLocation>
    ): Observable<List<ServiceLocation>> {
        return search(
            query = query,
            serviceLocations = serviceLocations,
            algorithm = algorithm,
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
        algorithm: Applicable,
        serviceLocations: List<ServiceLocation>
    ): Observable<List<ServiceLocation>> {
        return search(
            query = query,
            algorithm = algorithm,
            serviceLocations = serviceLocations.filter {
                it.service == Service.DUBLIN_BUS || it.service == Service.BUS_EIREANN
            },
            toStringFunction = ToStringFunction<ServiceLocation> { it.id }
        )
    }

    private fun search(
        query: String,
        algorithm: Applicable,
        serviceLocations: List<ServiceLocation>,
        toStringFunction: ToStringFunction<ServiceLocation>
    ): Observable<List<ServiceLocation>> {
        return Observable.just(
            FuzzySearch.extractSorted(
                query,
                serviceLocations,
                toStringFunction,
                algorithm,
                searchScoreCutoff
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
}
