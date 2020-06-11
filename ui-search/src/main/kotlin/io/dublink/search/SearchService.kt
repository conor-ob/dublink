package io.dublink.search

import io.dublink.domain.model.DubLinkServiceLocation
import io.dublink.domain.model.DubLinkStopLocation
import io.dublink.domain.util.AppConstants
import io.reactivex.Observable
import io.reactivex.functions.BiFunction
import io.rtpi.api.Service
import java.text.Normalizer
import java.util.Locale
import me.xdrop.fuzzywuzzy.Applicable
import me.xdrop.fuzzywuzzy.FuzzySearch
import me.xdrop.fuzzywuzzy.ToStringFunction
import me.xdrop.fuzzywuzzy.algorithms.TokenSet
import me.xdrop.fuzzywuzzy.algorithms.WeightedRatio
import me.xdrop.fuzzywuzzy.model.BoundExtractedResult

class SearchService {

    private val normalizingRegex = "\\p{InCombiningDiacriticalMarks}+".toRegex()
    private val identifierRegex1 = "^\\d{0,4}[a-zA-Z]?\\z".toRegex()
    private val identifierRegex2 = "^[a-zA-Z]\\d{0,4}\\z".toRegex()
    private val whiteSpace = "\\s+".toRegex()
    private val singleSpace = " "

    fun search(
        query: String,
        serviceLocations: List<DubLinkServiceLocation>
    ): Observable<List<DubLinkServiceLocation>> =
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
        serviceLocations: List<DubLinkServiceLocation>
    ): Observable<List<DubLinkServiceLocation>> =
        search(
            query = query,
            serviceLocations = serviceLocations,
            algorithm = algorithm,
            toStringFunction = ToStringFunction<DubLinkServiceLocation> {
                listOfNotNull(
                    it.defaultName,
                    it.service.fullName
                ).plus(
                    if (it is DubLinkStopLocation) {
                        it.stopLocation.routeGroups.map { routeGroup -> routeGroup.operator.fullName }
                    } else {
                        emptyList()
                    }
                )
                    .toSet()
                    .joinToString(separator = singleSpace) { value -> value.normalize() }
            }
        ).map { results ->
            results.sortedByDescending { it.score }
                .map { it.referent }
        }

    private fun identifierFieldSearch(
        query: String,
        algorithm: Applicable,
        serviceLocations: List<DubLinkServiceLocation>
    ): Observable<List<DubLinkServiceLocation>> =
        Observable.zip(
            search(
                query = query,
                algorithm = algorithm,
                serviceLocations = serviceLocations.filter {
                    it.service == Service.DUBLIN_BUS || it.service == Service.BUS_EIREANN
                },
                toStringFunction = ToStringFunction<DubLinkServiceLocation> { it.id }
            ),
            search(
                query = query,
                algorithm = algorithm,
                serviceLocations = serviceLocations.filter {
                    it.service == Service.DUBLIN_BUS || it.service == Service.BUS_EIREANN ||
                        it.service == Service.LUAS || it.service == Service.AIRCOACH
                },
                toStringFunction = ToStringFunction<DubLinkServiceLocation> {
                    if (it is DubLinkStopLocation) {
                        it.stopLocation.routeGroups.flatMap { routeGroup -> routeGroup.routes }
                    } else {
                        emptyList()
                    }
                        .toSet()
                        .joinToString(separator = singleSpace) { value -> value.normalize() }
                }
            ),
            BiFunction { t1, t2 ->
                t1.plus(t2).sortedByDescending { it.score }
                    .map { it.referent } // TODO remove duplicates?
            }
        )

    private fun search(
        query: String,
        algorithm: Applicable,
        serviceLocations: List<DubLinkServiceLocation>,
        toStringFunction: ToStringFunction<DubLinkServiceLocation>
    ): Observable<List<BoundExtractedResult<DubLinkServiceLocation>>> =
        Observable.just(
            FuzzySearch.extractSorted(
                query,
                serviceLocations,
                toStringFunction,
                algorithm,
                AppConstants.searchAccuracyScoreCutoff
            )
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
