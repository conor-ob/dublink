package io.dublink.database

import com.squareup.sqldelight.runtime.rx.asObservable
import com.squareup.sqldelight.runtime.rx.mapToList
import io.dublink.domain.datamodel.RecentServiceLocationSearchLocalResource
import io.dublink.domain.model.RecentServiceLocationSearch
import io.reactivex.Observable

class SqlDelightRecentServiceLocationSearchLocalResource(
    private val database: Database
) : RecentServiceLocationSearchLocalResource {

    override fun selectRecentSearches(): Observable<List<RecentServiceLocationSearch>> {
        return database.recentSearchQueries.selectAll()
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
        val existing = database.recentSearchQueries.select(
            service = recentSearch.service,
            locationId = recentSearch.locationId
        ).executeAsOneOrNull()

        if (existing != null) {
            database.recentSearchQueries.delete(
                service = recentSearch.service,
                locationId = recentSearch.locationId
            )
        }

        val recentSearchEntities = database.recentSearchQueries.selectAll().executeAsList()
        if (recentSearchEntities.size > 100) {
            val reduced = recentSearchEntities
                .sortedByDescending { it.timestamp }
                .take(50)
            database.transaction {
                database.recentSearchQueries.deleteAll()
                for (entity in reduced) {
                    database.recentSearchQueries.insertOrReplace(
                        service = entity.service,
                        locationId = entity.locationId,
                        timestamp = entity.timestamp
                    )
                }
            }
        }
        database.recentSearchQueries.insertOrReplace(
            service = recentSearch.service,
            locationId = recentSearch.locationId,
            timestamp = recentSearch.timestamp
        )
    }

    override fun deleteRecentSearches() {
        database.recentSearchQueries.deleteAll()
    }
}
