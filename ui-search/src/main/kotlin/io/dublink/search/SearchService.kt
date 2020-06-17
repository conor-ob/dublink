package io.dublink.search

import io.dublink.domain.model.DubLinkServiceLocation
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
import org.apache.lucene.queryparser.complexPhrase.ComplexPhraseQueryParser
import org.apache.lucene.search.FuzzyQuery
import org.apache.lucene.search.IndexSearcher
import org.apache.lucene.search.PrefixQuery
import org.apache.lucene.search.Query
import org.apache.lucene.search.TermQuery
import org.apache.lucene.store.RAMDirectory
import org.apache.lucene.util.Version
import timber.log.Timber

class SearchService {

    private var memoryIndex = RAMDirectory()
    private val indexAnalyzer = StandardAnalyzer(Version.LUCENE_48)
    @Volatile private var cache: Map<ServiceLocationKey, DubLinkServiceLocation>? = null

    fun search(
        query: String,
        serviceLocations: List<DubLinkServiceLocation>
    ): Observable<List<DubLinkServiceLocation>> {
        if (cache == null || cache?.size != serviceLocations.size) {
            synchronized(SearchService::class.java) {
                if (cache == null || cache?.size != serviceLocations.size) {
                    buildSearchIndex(serviceLocations)
                }
            }
        }
        val sanitizedQuery = query.trim().toLowerCase() // TODO remove punctuation?
        return Observable.zip(
            SearchField.values()
                .map { searchField ->
                    searchIndex(sanitizedQuery, searchField.fieldName)
                }
        ) { searchStreams -> aggregate(searchStreams) }
    }

    private fun buildSearchIndex(serviceLocations: List<DubLinkServiceLocation>) {
        Timber.d("Building search index")
        memoryIndex.close()
        memoryIndex = RAMDirectory()
        writeIndex(serviceLocations)
        cache = serviceLocations.associateBy { ServiceLocationKey(it.service, it.id) }
        Timber.d("Finished building search index")
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

    private fun writeIndex(serviceLocations: List<DubLinkServiceLocation>) {
        val indexWriterConfig = IndexWriterConfig(Version.LUCENE_48, indexAnalyzer).apply {
            openMode = OpenMode.CREATE
        }
        val writer = IndexWriter(memoryIndex, indexWriterConfig)
        for (serviceLocation in serviceLocations) {
            val document = Document()
            document.add(StringField(KeyField.SERVICE.fieldName, serviceLocation.service.name, Field.Store.YES))
            document.add(StringField(KeyField.ID.fieldName, serviceLocation.id, Field.Store.YES))

            SearchField.values().mapNotNull {
                val searchField = it.toSearchField(serviceLocation)
                if (searchField != null) {
                    return@mapNotNull it to searchField
                } else {
                    return@mapNotNull null
                }
            }
                .forEach {
                    document.add(TextField(it.first.fieldName, it.second, Field.Store.YES))
                }
            writer.addDocument(document)
        }
        writer.close()
    }

    private fun searchIndex(phrase: String, field: String): Observable<List<SearchResult>> {
        return Observable.zip(
            listOf(
                searchIndexInternal(QueryParser(Version.LUCENE_48, field, indexAnalyzer).parse(phrase)),
                searchIndexInternal(ComplexPhraseQueryParser(Version.LUCENE_48, field, indexAnalyzer).parse(phrase)),
                when {
                    phrase.length > 5 -> searchIndexInternal(FuzzyQuery(Term(field, phrase), FuzzyQuery.defaultMaxEdits))
                    phrase.length > 3 -> searchIndexInternal(FuzzyQuery(Term(field, phrase), 1))
                    else -> searchIndexInternal(FuzzyQuery(Term(field, phrase), 0))
                },
                searchIndexInternal(TermQuery(Term(field, phrase))),
                searchIndexInternal(PrefixQuery(Term(field, phrase)))
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
            val match = cache?.get(key)
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
