package io.dublink.search

import io.dublink.domain.model.DubLinkServiceLocation
import io.dublink.domain.model.DubLinkStopLocation
import io.dublink.domain.repository.ServiceLocationKey
import io.dublink.domain.service.PreferenceStore
import io.reactivex.Observable
import io.rtpi.api.Service
import java.io.IOException
import org.apache.lucene.analysis.standard.StandardAnalyzer
import org.apache.lucene.document.Document
import org.apache.lucene.document.Field
import org.apache.lucene.document.StringField
import org.apache.lucene.document.TextField
import org.apache.lucene.index.DirectoryReader
import org.apache.lucene.index.IndexReader
import org.apache.lucene.index.IndexWriter
import org.apache.lucene.index.IndexWriterConfig
import org.apache.lucene.index.IndexWriterConfig.OpenMode
import org.apache.lucene.index.Term
import org.apache.lucene.queryparser.classic.ParseException
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
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SearchService @Inject constructor(
    private val preferenceStore: PreferenceStore
) {

    private enum class KeyField(val fieldName: String) {
        ID(fieldName = "key_id"),
        SERVICE(fieldName = "key_service");
    }

    private enum class SearchField(val fieldName: String) {
        ID(fieldName = "id") {
            override fun toSearchField(serviceLocation: DubLinkServiceLocation): String? {
                return if (serviceLocation.service == Service.DUBLIN_BUS ||
                    serviceLocation.service == Service.BUS_EIREANN ||
                    serviceLocation.service == Service.IRISH_RAIL
                ) {
                    serviceLocation.id
                } else {
                    null
                }
            }
        },
        NAME(fieldName = "name") {
            override fun toSearchField(serviceLocation: DubLinkServiceLocation): String? {
                return serviceLocation.name
            }
        },
        SERVICE(fieldName = "service") {
            override fun toSearchField(serviceLocation: DubLinkServiceLocation): String? {
                return serviceLocation.service.fullName
            }
        },
        ROUTES(fieldName = "routes") {
            override fun toSearchField(serviceLocation: DubLinkServiceLocation): String? {
                return if (serviceLocation is DubLinkStopLocation) {
                    if (serviceLocation.service == Service.DUBLIN_BUS || serviceLocation.service == Service.BUS_EIREANN || serviceLocation.service == Service.LUAS) {
                        serviceLocation.stopLocation.routeGroups.flatMap { routeGroup -> routeGroup.routes }.joinToString(separator = " ")
                    } else {
                        null
                    }
                } else {
                    null
                }
            }
        },
        OPERATORS(fieldName = "operators") {
            override fun toSearchField(serviceLocation: DubLinkServiceLocation): String? {
                return if (serviceLocation is DubLinkStopLocation) {
                    serviceLocation.stopLocation.routeGroups.joinToString(separator = " ") { routeGroup -> routeGroup.operator.fullName }
                } else {
                    null
                }
            }
        },
        CONTENT(fieldName = "content") {
            override fun toSearchField(serviceLocation: DubLinkServiceLocation): String? {
                return values()
                    .filter { it != this }
                    .map { it.toSearchField(serviceLocation) }
                    .toSet()
                    .joinToString(separator = " ")
            }
        };

        abstract fun toSearchField(serviceLocation: DubLinkServiceLocation): String?
    }

    private val singleSpace = " "

    private var memoryIndex = RAMDirectory()
    private val analyzer = StandardAnalyzer(Version.LUCENE_48)
    private var cache = emptyMap<ServiceLocationKey, DubLinkServiceLocation>()

    fun search(
        query: String,
        serviceLocations: List<DubLinkServiceLocation>
    ): Observable<List<DubLinkServiceLocation>> {
        Timber.d("${javaClass.simpleName}::${object{}.javaClass.enclosingMethod?.name} $query")
        if (serviceLocations.size != cache.size) {
            Timber.d("${javaClass.simpleName}::${object{}.javaClass.enclosingMethod?.name} rebuilding index")
            memoryIndex.close()
            memoryIndex = RAMDirectory()
            writeIndex(serviceLocations)
            cache = serviceLocations.associateBy { ServiceLocationKey(it.service, it.id) }
            Timber.d("${javaClass.simpleName}::${object{}.javaClass.enclosingMethod?.name} finished building index")
        }
        // Search indexed docs in RAMDirectory
        return Observable.zip(
            SearchField.values().map { searchIndex(query, it.fieldName) }
        ) { res -> aggregate(res) }
    }

    private fun aggregate(serviceLocationStreams: Array<out Any>): List<DubLinkServiceLocation> {
        return serviceLocationStreams
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

    fun writeIndex(serviceLocations: List<DubLinkServiceLocation>) {
        try {
            // IndexWriter Configuration
            val iwc = IndexWriterConfig(Version.LUCENE_48, analyzer)
            iwc.openMode = OpenMode.CREATE

            // IndexWriter writes new index files to the directory
            val writer = IndexWriter(memoryIndex, iwc)

            // Create some docs with name and content
            serviceLocations.forEach {
                indexDoc(writer, it)
            }

            // don't forget to close the writer
            writer.close()
        } catch (e: IOException) {
            // Any error goes here
            e.printStackTrace()
        }
    }

    @Throws(IOException::class)
    fun indexDoc(writer: IndexWriter, serviceLocation: DubLinkServiceLocation) {
        val doc = Document()
        doc.add(StringField(KeyField.SERVICE.fieldName, serviceLocation.service.name, Field.Store.YES))
        doc.add(StringField(KeyField.ID.fieldName, serviceLocation.id, Field.Store.YES))

        SearchField.values().mapNotNull {
            val searchField = it.toSearchField(serviceLocation)
            if (searchField != null) {
                return@mapNotNull it to searchField
            } else {
                return@mapNotNull null
            }
        }
            .forEach {
                doc.add(TextField(it.first.fieldName, it.second, Field.Store.YES))
            }
        writer.addDocument(doc)
    }

    data class SearchResult(
        val serviceLocation: DubLinkServiceLocation,
        val score: Float
    )

    fun searchIndex(phrase: String, field: String): Observable<List<SearchResult>> {
        return Observable.zip(
            listOf(
                searchIndexInternal(QueryParser(Version.LUCENE_48, field, analyzer).parse(phrase), field),
                searchIndexInternal(ComplexPhraseQueryParser(Version.LUCENE_48, field, analyzer).parse(phrase), field),
                when {
                    phrase.length > 5 -> searchIndexInternal(FuzzyQuery(Term(field, phrase), FuzzyQuery.defaultMaxEdits), field)
                    phrase.length > 3 -> searchIndexInternal(FuzzyQuery(Term(field, phrase), 1), field)
                    else -> searchIndexInternal(FuzzyQuery(Term(field, phrase), 0), field)
                },
                searchIndexInternal(TermQuery(Term(field, phrase)), field),
                searchIndexInternal(PrefixQuery(Term(field, phrase)), field)
            )
        ) { res -> aggregateAgain(res) }
    }

    private fun aggregateAgain(serviceLocationStreams: Array<out Any>): List<SearchResult> {
        return serviceLocationStreams.flatMap { it as List<SearchResult> }
    }

    fun searchIndexInternal(query: Query, field: String): Observable<List<SearchResult>> {
        var reader: IndexReader? = null
        try {
            // Create Reader
            reader = DirectoryReader.open(memoryIndex)

            // Create index searcher
            val searcher = IndexSearcher(reader)

            // Build query
//            val qp = QueryParser(Version.LUCENE_48, field, analyzer)
//            val query: Query = qp.parse(phrase)

//            val query = if (flag) {
//                ComplexPhraseQueryParser(Version.LUCENE_48, field, analyzer).parse(phrase)
//            } else {
//                FuzzyQuery(Term(field, phrase))
//            }

            // Search the index
            val foundDocs = searcher.search(query, 100)

            // Total found documents
            println("Total Results :: " + foundDocs.totalHits)

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

//            val (l1, l2) = poop.partition { it.service == Service.BUS_EIREANN }

            // Let's print found doc names and their content along with score
//            for (sd in foundDocs.scoreDocs) {
//                val d = searcher.doc(sd.doc)
//                println(
//                    "Document Name : " + d["name"]
//                        + "  :: Content : " + d["content"]
//                        + "  :: Score : " + sd.score
//                )
//            }
            // don't forget to close the reader
            reader.close()

            return Observable.just(results)
        } catch (e: IOException) {
            // Any error goes here
            e.printStackTrace()
            throw e
        } catch (e: ParseException) {
            e.printStackTrace()
            throw e
        }
    }
}
