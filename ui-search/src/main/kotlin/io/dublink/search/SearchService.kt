package io.dublink.search

import io.dublink.domain.model.DubLinkServiceLocation
import io.dublink.domain.model.DubLinkStopLocation
import io.dublink.domain.repository.ServiceLocationKey
import io.reactivex.Observable
import io.reactivex.functions.Function3
import io.rtpi.api.Service
import org.apache.lucene.analysis.Analyzer
import org.apache.lucene.analysis.standard.StandardAnalyzer
import org.apache.lucene.document.Document
import org.apache.lucene.document.Field
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
import org.apache.lucene.search.FieldDoc
import org.apache.lucene.search.FuzzyQuery
import org.apache.lucene.search.IndexSearcher
import org.apache.lucene.search.Query
import org.apache.lucene.search.Sort
import org.apache.lucene.search.SortField
import org.apache.lucene.store.RAMDirectory
import org.apache.lucene.util.Version
import java.io.IOException

class SearchService {

    private val singleSpace = " "

    private val memoryIndex = RAMDirectory()
    private val analyzer = StandardAnalyzer(Version.LUCENE_48)
    private var indexLoaded = false
    private var cache = emptyMap<ServiceLocationKey, DubLinkServiceLocation>()

    fun search(
        query: String,
        serviceLocations: List<DubLinkServiceLocation>
    ): Observable<List<DubLinkServiceLocation>> {
        if (!indexLoaded) {
            writeIndex(serviceLocations)
            cache = serviceLocations.associateBy { ServiceLocationKey(it.service, it.id) }
            indexLoaded = true
        }
        //Search indexed docs in RAMDirectory
        return Observable.zip(
            listOf(
                searchIndex(query, "id"),
                searchIndex(query, "name"),
                searchIndex(query, "service"),
                searchIndex(query, "routes"),
                searchIndex(query, "operators")
            )
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

            //IndexWriter writes new index files to the directory
            val writer = IndexWriter(memoryIndex, iwc)

            //Create some docs with name and content
            serviceLocations.forEach {
                indexDoc(writer, it)
            }

            //don't forget to close the writer
            writer.close()
        } catch (e: IOException) {
            //Any error goes here
            e.printStackTrace()
        }
    }

    @Throws(IOException::class)
    fun indexDoc(writer: IndexWriter, serviceLocation: DubLinkServiceLocation) {
        val doc = Document()
        doc.add(TextField("service", serviceLocation.service.name, Field.Store.YES))
        doc.add(TextField("locationId", serviceLocation.id, Field.Store.YES))
        if (serviceLocation.service == Service.DUBLIN_BUS || serviceLocation.service == Service.BUS_EIREANN || serviceLocation.service == Service.IRISH_RAIL) {
            doc.add(TextField("id", serviceLocation.id, Field.Store.YES))
        }
        doc.add(TextField("service", serviceLocation.service.fullName, Field.Store.YES))
        doc.add(TextField("name", serviceLocation.defaultName, Field.Store.YES))
        if (serviceLocation is DubLinkStopLocation) {
            if (serviceLocation.service == Service.DUBLIN_BUS || serviceLocation.service == Service.BUS_EIREANN) {
                doc.add(TextField("routes", serviceLocation.stopLocation.routeGroups.flatMap { routeGroup -> routeGroup.routes }.joinToString(separator = singleSpace), Field.Store.YES))
            }
            doc.add(TextField("operators", serviceLocation.stopLocation.routeGroups.map { routeGroup -> routeGroup.operator }.joinToString(separator = singleSpace), Field.Store.YES))
        }
        writer.addDocument(doc)
    }

    data class SearchResult(
        val serviceLocation: DubLinkServiceLocation,
        val score: Float
    )

    fun searchIndex(phrase: String, field: String): Observable<List<SearchResult>> {
        return Observable.zip(
            searchIndexInternal(QueryParser(Version.LUCENE_48, field, analyzer).parse(phrase), field),
            searchIndexInternal(ComplexPhraseQueryParser(Version.LUCENE_48, field, analyzer).parse(phrase), field),
            searchIndexInternal(FuzzyQuery(Term(field, phrase)), field),
            Function3 { t1, t2, t3 -> t1.plus(t2).plus(t3) }
        )
    }

    fun searchIndexInternal(query: Query, field: String): Observable<List<SearchResult>> {
        var reader: IndexReader? = null
        try {
            //Create Reader
            reader = DirectoryReader.open(memoryIndex)

            //Create index searcher
            val searcher = IndexSearcher(reader)

            //Build query
//            val qp = QueryParser(Version.LUCENE_48, field, analyzer)
//            val query: Query = qp.parse(phrase)

//            val query = if (flag) {
//                ComplexPhraseQueryParser(Version.LUCENE_48, field, analyzer).parse(phrase)
//            } else {
//                FuzzyQuery(Term(field, phrase))
//            }

            //Search the index
            val foundDocs = searcher.search(query, 100)

            // Total found documents
            println("Total Results :: " + foundDocs.totalHits)

            val results = foundDocs.scoreDocs.mapNotNull {
                val doc = searcher.doc(it.doc)
                val service = Service.valueOf(doc.get("service"))
                val locationId = doc.get("locationId")
                val key = ServiceLocationKey(service, locationId)
                val match = cache[key]
                if (match != null) {
                    SearchResult(match, it.score)
                } else {
                    null
                }
            }

//            val (l1, l2) = poop.partition { it.service == Service.BUS_EIREANN }

            //Let's print found doc names and their content along with score
//            for (sd in foundDocs.scoreDocs) {
//                val d = searcher.doc(sd.doc)
//                println(
//                    "Document Name : " + d["name"]
//                        + "  :: Content : " + d["content"]
//                        + "  :: Score : " + sd.score
//                )
//            }
            //don't forget to close the reader
            reader.close()

            return Observable.just(results)
        } catch (e: IOException) {
            //Any error goes here
            e.printStackTrace()
            throw e
        } catch (e: ParseException) {
            e.printStackTrace()
            throw e
        }
    }
}
