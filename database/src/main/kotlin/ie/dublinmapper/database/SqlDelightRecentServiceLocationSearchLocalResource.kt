package ie.dublinmapper.database

import com.squareup.sqldelight.runtime.rx.asObservable
import com.squareup.sqldelight.runtime.rx.mapToList
import ie.dublinmapper.domain.datamodel.RecentServiceLocationSearchLocalResource
import ie.dublinmapper.domain.model.RecentServiceLocationSearch
import io.reactivex.Observable
import java.time.Instant

class SqlDelightRecentServiceLocationSearchLocalResource(
    private val database: Database
) : RecentServiceLocationSearchLocalResource {

    override fun selectRecentSearches(): Observable<List<RecentServiceLocationSearch>> {
        return database.recentSearchEntityQueries.selectAll()
            .asObservable()
            .mapToList()
            .map { recentSearchEntities: List<RecentSearchEntity> ->
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
        val recentSearchEntities = database.recentSearchEntityQueries.selectAll().executeAsList()
        if (recentSearchEntities.size > 100) {
            val reduced = recentSearchEntities
                .sortedByDescending { it.timestamp }
                .take(50)
            database.transaction {
                database.recentSearchEntityQueries.deleteAll()
                for (entity in reduced) {
                    database.recentSearchEntityQueries.insertOrReplace(
                        service = entity.service,
                        locationId = entity.locationId,
                        timestamp = entity.timestamp
                    )
                }
            }
        }
        database.recentSearchEntityQueries.insertOrReplace(
            service = recentSearch.service,
            locationId = recentSearch.locationId,
            timestamp = recentSearch.timestamp
        )
    }
}
