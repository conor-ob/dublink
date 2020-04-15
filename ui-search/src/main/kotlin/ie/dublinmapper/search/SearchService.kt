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

    private val whiteSpace = "\\s+".toRegex()
    private val singleSpace = " "

    private val searchScoreCutoff = 70

    fun search(
        query: String,
        serviceLocations: List<ServiceLocation>
    ): Observable<List<ServiceLocation>> =
        if (isInteger(query)) {
            integerFieldSearch(query, getAlgorithm(query), serviceLocations)
        } else {
            stringFieldSearch(query, getAlgorithm(query), serviceLocations)
        }

    private fun getAlgorithm(query: String): Applicable =
        if (query.split(whiteSpace).size > 1) {
            TokenSet()
        } else {
            WeightedRatio()
        }

    private fun stringFieldSearch(
        query: String,
        algorithm: Applicable,
        serviceLocations: List<ServiceLocation>
    ): Observable<List<ServiceLocation>> =
        search(
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
                    .joinToString(separator = singleSpace)
            }
        )

    private fun integerFieldSearch(
        query: String,
        algorithm: Applicable,
        serviceLocations: List<ServiceLocation>
    ): Observable<List<ServiceLocation>> =
        search(
            query = query,
            algorithm = algorithm,
            serviceLocations = serviceLocations.filter {
                it.service == Service.DUBLIN_BUS || it.service == Service.BUS_EIREANN
            },
            toStringFunction = ToStringFunction<ServiceLocation> { it.id }
        )

    private fun search(
        query: String,
        algorithm: Applicable,
        serviceLocations: List<ServiceLocation>,
        toStringFunction: ToStringFunction<ServiceLocation>
    ): Observable<List<ServiceLocation>> =
        Observable.just(
            FuzzySearch.extractSorted(
                query,
                serviceLocations,
                toStringFunction,
                algorithm,
                searchScoreCutoff
            ).map { it.referent }
        )

    private fun isInteger(value: String): Boolean =
        try {
            value.toInt()
            true
        } catch (e : NumberFormatException) {
            false
        } catch (e : Exception) {
            false
        }
}
