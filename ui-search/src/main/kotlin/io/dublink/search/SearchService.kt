package io.dublink.search

import io.dublink.domain.model.DubLinkServiceLocation
import io.dublink.domain.model.DubLinkStopLocation
import io.dublink.domain.repository.ServiceLocationKey
import io.reactivex.Observable
import io.rtpi.api.Service
import org.apache.lucene.analysis.standard.StandardAnalyzer
import org.apache.lucene.document.Document
import org.apache.lucene.document.Field
import org.apache.lucene.document.StringField
import org.apache.lucene.document.TextField
import org.apache.lucene.index.DirectoryReader
import org.apache.lucene.index.IndexWriter
import org.apache.lucene.index.IndexWriterConfig
import org.apache.lucene.index.IndexWriterConfig.OpenMode
import org.apache.lucene.index.Term
import org.apache.lucene.queryparser.classic.QueryParser
import org.apache.lucene.search.FuzzyQuery
import org.apache.lucene.search.IndexSearcher
import org.apache.lucene.search.PrefixQuery
import org.apache.lucene.search.Query
import org.apache.lucene.store.RAMDirectory
import org.apache.lucene.util.Version
import timber.log.Timber

interface SearchService {
    fun search(
        query: String,
        serviceLocations: List<DubLinkServiceLocation>
    ): Observable<List<DubLinkServiceLocation>>
}

class SimpleSearchService : SearchService {

    override fun search(
        query: String,
        serviceLocations: List<DubLinkServiceLocation>
    ): Observable<List<DubLinkServiceLocation>> {
        val results = mutableListOf<DubLinkServiceLocation>()
        for (serviceLocation in serviceLocations) {
            if (serviceLocation.id.contains(query, ignoreCase = true)) {
                results.add(serviceLocation)
            } else if (serviceLocation.defaultName.contains(query, ignoreCase = true)) {
                results.add(serviceLocation)
            } else if (serviceLocation.service.fullName.contains(query, ignoreCase = true)) {
                results.add(serviceLocation)
            } else if (serviceLocation is DubLinkStopLocation &&
                serviceLocation.stopLocation.routeGroups
                    .map { it.operator.fullName }
                    .toSet()
                    .any { it.contains(query, ignoreCase = true) }
            ) {
                results.add(serviceLocation)
            } else if (serviceLocation is DubLinkStopLocation &&
                serviceLocation.stopLocation.routeGroups
                    .flatMap { it.routes }
                    .toSet()
                    .any { it.contains(query, ignoreCase = true) }
            ) {
                results.add(serviceLocation)
            }
        }
        return Observable.just(results)
    }
}

class LuceneSearchService : SearchService {

    /**
     * Tests to add
     * james's
     * bride's glen
     * artane
     * st. john's
     */

    private var memoryIndex = RAMDirectory()
    private val indexAnalyzer = StandardAnalyzer(Version.LUCENE_48)
    @Volatile private var cache = emptyMap<ServiceLocationKey, DubLinkServiceLocation>()

    override fun search(
        query: String,
        serviceLocations: List<DubLinkServiceLocation>
    ): Observable<List<DubLinkServiceLocation>> {
        val newServiceLocations = serviceLocations.associateBy { ServiceLocationKey(it.service, it.id) }
        if (cache.size != newServiceLocations.size) {
            synchronized(SearchService::class.java) {
                if (cache.size != newServiceLocations.size) {
                    buildSearchIndex(newServiceLocations)
                }
            }
        }
        cache = newServiceLocations
        val sanitizedQueries = sanitizeQuery(query)
        Timber.d("queries=$sanitizedQueries")
        return Observable.zip(
            SearchField.values()
                .flatMap { searchField ->
                    sanitizedQueries.map {
                        searchIndex(it, searchField.fieldName)
                    }
                }
        ) { searchStreams -> aggregate(searchStreams) }
    }

    // TODO remove punctuation?
    private fun sanitizeQuery(query: String): Set<String> {
        val sanitized = query.trim().toLowerCase()
        val tokens = sanitized.split("\\s+".toRegex())
        val copy = tokens.mapIndexed { index, s ->
            if (index == 0 && s == "st") {
                "st."
            } else if (s.endsWith("s") && s.lastIndexOf("'") != s.length - 2) {
                s.substring(0, s.length - 1) + "'" + "s"
            } else if (s.contains("'")) {
                s.replace("'", "")
            } else if (s.endsWith(".")) {
                s.replace(".", "")
            } else {
                s
            }
        }.joinToString(separator = " ")
        return listOf(sanitized, copy).toSet()
    }

    private fun aggregate(searchStreams: Array<out Any>): List<DubLinkServiceLocation> {
        return searchStreams
            .flatMap { it as List<SearchResult> }
            .groupBy { it.serviceLocation }
            .mapValues {
                if (it.value.size > 1) {
                    it.value.maxBy { thing -> thing.score }!!
                } else {
                    it.value.first()
                }
            }
            .map { it.value }
            .sortedByDescending { it.score }
            .map { it.serviceLocation }
    }

    private fun buildSearchIndex(serviceLocations: Map<ServiceLocationKey, DubLinkServiceLocation>) {
        Timber.d("Building search index")
        memoryIndex.close()
        memoryIndex = RAMDirectory()
        val indexWriterConfig = IndexWriterConfig(Version.LUCENE_48, indexAnalyzer)
        indexWriterConfig.openMode = OpenMode.CREATE
        val writer = IndexWriter(memoryIndex, indexWriterConfig)
        writer.addDocuments(
            serviceLocations.values.map { serviceLocation ->
                Document().apply {
                    add(StringField(KeyField.SERVICE.fieldName, serviceLocation.service.name, Field.Store.YES))
                    add(StringField(KeyField.ID.fieldName, serviceLocation.id, Field.Store.YES))

                    SearchField.values().mapNotNull {
                        val searchField = it.toSearchField(serviceLocation)
                        if (searchField != null) {
                            return@mapNotNull it to searchField
                        } else {
                            return@mapNotNull null
                        }
                    }
                        .forEach {
                            add(TextField(it.first.fieldName, it.second, Field.Store.NO))
                        }
                }
            }
        )
        writer.close()
        cache = serviceLocations
        Timber.d("Finished building search index")
    }

    private fun searchIndex(query: String, field: String): Observable<List<SearchResult>> {
        return Observable.zip(
            listOf(
                searchIndexInternal(QueryParser(Version.LUCENE_48, field, indexAnalyzer).parse(query)),
//                searchIndexInternal(ComplexPhraseQueryParser(Version.LUCENE_48, field, indexAnalyzer).parse(query)),
//                searchIndexInternal(TermQuery(Term(field, query))),
                searchIndexInternal(PrefixQuery(Term(field, query))),
                when {
                    query.length > 5 -> searchIndexInternal(FuzzyQuery(Term(field, query), FuzzyQuery.defaultMaxEdits))
                    query.length > 3 -> searchIndexInternal(FuzzyQuery(Term(field, query), 1))
                    else -> searchIndexInternal(FuzzyQuery(Term(field, query), 0))
                }
            )
        ) { searchStreams -> aggregateSearchQueries(searchStreams) }
    }

    private fun aggregateSearchQueries(serviceLocationStreams: Array<out Any>): List<SearchResult> {
        return serviceLocationStreams.flatMap { it as List<SearchResult> }
    }

    private fun searchIndexInternal(query: Query): Observable<List<SearchResult>> {
        val reader = DirectoryReader.open(memoryIndex)
        val searcher = IndexSearcher(reader)
        val foundDocs = searcher.search(query, 100)

        val results = foundDocs.scoreDocs.mapNotNull {
            val doc = searcher.doc(it.doc)
            val service = Service.valueOf(doc.get(KeyField.SERVICE.fieldName))
            val locationId = doc.get(KeyField.ID.fieldName)
            val key = ServiceLocationKey(service, locationId)
            val match = cache[key]
            if (match != null) {
                SearchResult(match, it.score)
            } else {
                null
            }
        }

        reader.close()
        return Observable.just(results)
    }
}

data class SearchResult(
    val serviceLocation: DubLinkServiceLocation,
    val score: Float
)
