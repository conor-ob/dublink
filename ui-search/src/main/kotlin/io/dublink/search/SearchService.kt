package io.dublink.search

import io.dublink.domain.model.DubLinkServiceLocation
import io.dublink.domain.model.DubLinkStopLocation
import io.dublink.domain.repository.ServiceLocationKey
import io.dublink.domain.util.AppConstants
import io.reactivex.Observable
import io.reactivex.functions.BiFunction
import io.rtpi.api.Service
import me.xdrop.fuzzywuzzy.Applicable
import me.xdrop.fuzzywuzzy.FuzzySearch
import me.xdrop.fuzzywuzzy.ToStringFunction
import me.xdrop.fuzzywuzzy.algorithms.TokenSet
import me.xdrop.fuzzywuzzy.algorithms.WeightedRatio
import me.xdrop.fuzzywuzzy.model.BoundExtractedResult
import org.apache.lucene.analysis.Analyzer
import org.apache.lucene.analysis.core.SimpleAnalyzer
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
import org.apache.lucene.search.FuzzyQuery
import org.apache.lucene.search.IndexSearcher
import org.apache.lucene.search.Query
import org.apache.lucene.search.Sort
import org.apache.lucene.search.SortField
import org.apache.lucene.store.RAMDirectory
import org.apache.lucene.util.Version
import java.io.IOException
import java.text.Normalizer
import java.util.Locale

class SearchService {

    private val normalizingRegex = "\\p{InCombiningDiacriticalMarks}+".toRegex()
    private val identifierRegex1 = "^\\d{0,4}[a-zA-Z]?\\z".toRegex()
    private val identifierRegex2 = "^[a-zA-Z]\\d{0,4}\\z".toRegex()
    private val whiteSpace = "\\s+".toRegex()
    private val singleSpace = " "

    private val ramDir = RAMDirectory()
    private val analyzer: Analyzer = StandardAnalyzer(Version.LUCENE_47)
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
        return searchIndex(query)
    }

    fun writeIndex(serviceLocations: List<DubLinkServiceLocation>) {
        try {
            // IndexWriter Configuration
            val iwc = IndexWriterConfig(Version.LUCENE_47, analyzer)
            iwc.openMode = OpenMode.CREATE

            //IndexWriter writes new index files to the directory
            val writer = IndexWriter(ramDir, iwc)

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
        val searchable = listOfNotNull(
            serviceLocation.id,
            serviceLocation.defaultName,
            serviceLocation.service.fullName
        ).plus(
            if (serviceLocation is DubLinkStopLocation) {
                serviceLocation.stopLocation.routeGroups.flatMap { routeGroup -> routeGroup.routes }
            } else {
                emptyList()
            }
        ).plus(
            if (serviceLocation is DubLinkStopLocation) {
                serviceLocation.stopLocation.routeGroups.map { routeGroup -> routeGroup.operator.fullName }
            } else {
                emptyList()
            }
        )
            .toSet()
            .joinToString(separator = singleSpace) { value -> value.normalize() }

        val doc = Document()
        doc.add(TextField("service", serviceLocation.service.name, Field.Store.YES))
        doc.add(TextField("locationId", serviceLocation.id, Field.Store.YES))
        doc.add(TextField("content", searchable, Field.Store.YES))
        writer.addDocument(doc)
    }

    fun searchIndex(phrase: String): Observable<List<DubLinkServiceLocation>> {
        var reader: IndexReader? = null
        try {
            //Create Reader
            reader = DirectoryReader.open(ramDir)

            //Create index searcher
            val searcher = IndexSearcher(reader)

            //Build query
//            val qp = QueryParser("content", analyzer)
//            val query: Query = qp.parse("Blackrock")

            val query = if (phrase.split(whiteSpace).size > 1) {
                ComplexPhraseQueryParser(Version.LUCENE_47, "content", analyzer).parse(phrase)
            } else {
                FuzzyQuery(Term("content", phrase))
            }

            //Search the index
            val foundDocs = searcher.search(query, 100, Sort(SortField.FIELD_SCORE))

            // Total found documents
            println("Total Results :: " + foundDocs.totalHits)

            val results = foundDocs.scoreDocs.map {
                searcher.doc(it.doc)
            }

            val poop = results.mapNotNull {
                val service = Service.valueOf(it.get("service"))
                val locationId = it.get("locationId")
                val key = ServiceLocationKey(service, locationId)
                cache[key]
            }

            val (l1, l2) = poop.partition { it.service == Service.BUS_EIREANN }

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

            return Observable.just(l2.plus(l1))
        } catch (e: IOException) {
            //Any error goes here
            e.printStackTrace()
            throw e
        } catch (e: ParseException) {
            e.printStackTrace()
            throw e
        }
    }

//    fun search(
//        query: String,
//        serviceLocations: List<DubLinkServiceLocation>
//    ): Observable<List<DubLinkServiceLocation>> =
//        if (isIdentifier(query)) {
//            identifierFieldSearch(query, getAlgorithm(query), serviceLocations)
//        } else {
//            stringFieldSearch(query, getAlgorithm(query), serviceLocations)
//        }

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
                    it.defaultName
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
                    it.service == Service.DUBLIN_BUS || it.service == Service.BUS_EIREANN
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
            identifierRegex2.matches(value)
    }

    private fun CharSequence.normalize(): String {
        return normalizingRegex.replace(Normalizer.normalize(this, Normalizer.Form.NFD), "")
    }
}
