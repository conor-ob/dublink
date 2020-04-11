package ie.dublinmapper.database

import com.squareup.sqldelight.runtime.rx.asObservable
import com.squareup.sqldelight.runtime.rx.mapToList
import ie.dublinmapper.domain.datamodel.RecentServiceLocationSearchLocalResource
import ie.dublinmapper.domain.model.RecentServiceLocationSearch
import io.reactivex.Observable

class SqlDelightRecentServiceLocationSearchLocalResource(
    private val database: Database
) : RecentServiceLocationSearchLocalResource {

    override fun selectRecentSearches(): Observable<List<RecentServiceLocationSearch>> {
        return database.recentSearchesQueries.selectAll()
            .asObservable()
            .mapToList()
            .map { recentSearchEntities: List<RecentSearches> ->
                recentSearchEntities.map {
                    RecentServiceLocationSearch(
                        service = it.service,
                        locationId = it.locationId,
                        timestamp = it.timestamp
                    )
                }
            }
    }

    override fun insertRecentSearch(recentSearch: RecentServiceLocationSearch) {
        val recentSearchEntities = database.recentSearchesQueries.selectAll().executeAsList()
        if (recentSearchEntities.size > 100) {
            val reduced = recentSearchEntities
                .sortedByDescending { it.timestamp }
                .take(50)
            database.transaction {
                database.recentSearchesQueries.deleteAll()
                for (entity in reduced) {
                    database.recentSearchesQueries.insertOrReplace(
                        service = entity.service,
                        locationId = entity.locationId,
                        timestamp = entity.timestamp
                    )
                }
            }
        }
        database.recentSearchesQueries.insertOrReplace(
            service = recentSearch.service,
            locationId = recentSearch.locationId,
            timestamp = recentSearch.timestamp
        )
    }
}
