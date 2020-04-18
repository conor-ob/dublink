package ie.dublinmapper.search

import ie.dublinmapper.domain.model.getCustomName
import io.reactivex.Observable
import io.rtpi.api.Service
import io.rtpi.api.ServiceLocation
import io.rtpi.api.StopLocation
import java.text.Normalizer
import java.util.Locale
import me.xdrop.fuzzywuzzy.Applicable
import me.xdrop.fuzzywuzzy.FuzzySearch
import me.xdrop.fuzzywuzzy.ToStringFunction
import me.xdrop.fuzzywuzzy.algorithms.TokenSet
import me.xdrop.fuzzywuzzy.algorithms.WeightedRatio

class SearchService {

    private val normalizingRegex = "\\p{InCombiningDiacriticalMarks}+".toRegex()
    private val identifierRegex1 = "^\\d{0,4}[a-zA-Z]?\\z".toRegex()
    private val identifierRegex2 = "^[a-zA-Z]\\d{0,4}\\z".toRegex()
    private val whiteSpace = "\\s+".toRegex()
    private val singleSpace = " "
    private val searchScoreCutoff = 70

    fun search(
        query: String,
        serviceLocations: List<ServiceLocation>
    ): Observable<List<ServiceLocation>> =
        if (isIdentifier(query)) {
            identifierFieldSearch(query, getAlgorithm(query), serviceLocations)
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

    private fun identifierFieldSearch(
        query: String,
        algorithm: Applicable,
        serviceLocations: List<ServiceLocation>
    ): Observable<List<ServiceLocation>> =
        search(
            query = query,
            algorithm = algorithm,
            serviceLocations = serviceLocations.filter {
                it.service == Service.DUBLIN_BUS || it.service == Service.BUS_EIREANN ||
                    it.service == Service.LUAS || it.service == Service.AIRCOACH
            },
            toStringFunction = ToStringFunction<ServiceLocation> {
                listOfNotNull(
                    it.id
                ).plus(
                    if (it is StopLocation) {
                        it.routeGroups.flatMap { routeGroup -> routeGroup.routes }
                    } else {
                        emptyList()
                    }
                )
                    .toSet()
                    .joinToString(separator = singleSpace) { value -> value.normalize() }
            }
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

    private fun isIdentifier(value: String): Boolean {
        try {
            value.toInt()
            return true
        } catch (e: NumberFormatException) {
            // ignore
        } catch (e: Exception) {
            // ignore
        }
        val sanitizedQuery = value.toLowerCase(Locale.getDefault()).trim()
        return identifierRegex1.matches(value) ||
            identifierRegex2.matches(value) ||
            sanitizedQuery.contains("green") ||
            sanitizedQuery.contains("red")
    }

    private fun CharSequence.normalize(): String {
        return normalizingRegex.replace(Normalizer.normalize(this, Normalizer.Form.NFD), "")
    }
}
