package io.dublink.search

import io.dublink.domain.model.DubLinkServiceLocation
import io.dublink.domain.model.DubLinkStopLocation
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
import org.apache.lucene.store.RAMDirectory
import java.io.IOException
import java.text.Normalizer
import java.util.Locale

class SearchService {

    private val normalizingRegex = "\\p{InCombiningDiacriticalMarks}+".toRegex()
    private val identifierRegex1 = "^\\d{0,4}[a-zA-Z]?\\z".toRegex()
    private val identifierRegex2 = "^[a-zA-Z]\\d{0,4}\\z".toRegex()
    private val whiteSpace = "\\s+".toRegex()
    private val singleSpace = " "

    fun search(
        query: String,
        serviceLocations: List<DubLinkServiceLocation>
    ): Observable<List<DubLinkServiceLocation>> {

        //Create RAMDirectory instance
        //Create RAMDirectory instance
        val ramDir = RAMDirectory()

        //Builds an analyzer with the default stop words

        //Builds an analyzer with the default stop words
        val analyzer: Analyzer = StandardAnalyzer()

        //Write some docs to RAMDirectory

        //Write some docs to RAMDirectory
        writeIndex(ramDir, analyzer, serviceLocations)

        //Search indexed docs in RAMDirectory

        //Search indexed docs in RAMDirectory
        searchIndex(ramDir, analyzer)

        return Observable.just(emptyList())
    }

    fun writeIndex(ramDir: RAMDirectory?, analyzer: Analyzer?, serviceLocations: List<DubLinkServiceLocation>) {
        try {
            // IndexWriter Configuration
            val iwc = IndexWriterConfig(analyzer)
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
            serviceLocation.defaultName
        ).plus(
            if (serviceLocation is DubLinkStopLocation) {
                serviceLocation.stopLocation.routeGroups.flatMap { routeGroup -> routeGroup.routes }
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

    fun searchIndex(ramDir: RAMDirectory?, analyzer: Analyzer?) {
        var reader: IndexReader? = null
        try {
            //Create Reader
            reader = DirectoryReader.open(ramDir)

            //Create index searcher
            val searcher = IndexSearcher(reader)

            //Build query
            val qp = QueryParser("content", analyzer)
//            val query: Query = qp.parse("Blackrock")

            val phrase = "299"

            val query: Query = ComplexPhraseQueryParser("content", analyzer)
                .parse(phrase)

            val fuzzyQuery = FuzzyQuery(Term("content", phrase))
//            val foundDocs = searcher.search(fuzzyQuery, 100)

            //Search the index
            val foundDocs = searcher.search(query, 100)

            // Total found documents
            println("Total Results :: " + foundDocs.totalHits)

            val result = foundDocs.scoreDocs.map {
                searcher.doc(it.doc)
            }

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
        } catch (e: IOException) {
            //Any error goes here
            e.printStackTrace()
        } catch (e: ParseException) {
            e.printStackTrace()
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
