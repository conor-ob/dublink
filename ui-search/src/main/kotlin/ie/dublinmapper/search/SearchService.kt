package ie.dublinmapper.search

import ie.dublinmapper.domain.model.getCustomName
import io.reactivex.Observable
import io.rtpi.api.Service
import io.rtpi.api.ServiceLocation
import io.rtpi.api.StopLocation
import java.text.Normalizer
import me.xdrop.fuzzywuzzy.Applicable
import me.xdrop.fuzzywuzzy.FuzzySearch
import me.xdrop.fuzzywuzzy.ToStringFunction
import me.xdrop.fuzzywuzzy.algorithms.TokenSet
import me.xdrop.fuzzywuzzy.algorithms.WeightedRatio

class SearchService {

    private val normalizingRegex = "\\p{InCombiningDiacriticalMarks}+".toRegex()
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
                listOfNotNull(
                    it.name,
                    it.getCustomName(),
                    it.service.fullName
                ).plus(
                    if (it is StopLocation) {
                        it.routeGroups.map { routeGroup -> routeGroup.operator.fullName }
                    } else {
                        emptyList()
                    }
                )
                    .toSet()
                    .joinToString(separator = singleSpace) { value -> value.normalize() }
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
        } catch (e: NumberFormatException) {
            false
        } catch (e: Exception) {
            false
        }

    private fun CharSequence.normalize(): String {
        return normalizingRegex.replace(Normalizer.normalize(this, Normalizer.Form.NFD), "")
    }
}
