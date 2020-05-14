package io.dublink.domain.util

import java.time.Duration

object AppConstants {

    /**
     * The duration to cache stop locations for. This data rarely changes so it is not required to be refreshed often.
     * It is better to refresh only when WiFi is available.
     */
    val stopLocationCacheExpiry: Duration = Duration.ofDays(1L)

    /**
     * The duration to cache dock locations for. This is a much smaller value than the stop location cache since docks
     * have dynamic data related to the number of bikes or docks available which is likely to change often.
     */
    val dockLocationCacheExpiry: Duration = Duration.ofSeconds(90L)

    /**
     * The duration live data is cached for. This is slightly less than the shortest configurable auto refresh duration.
     */
    val liveDataCacheExpiry: Duration = Duration.ofSeconds(15L)

    /**
     * The duration of time to pass before live data is considered stale. When the app returns to the foreground it
     * could potentially be displaying stale data if it has been in the background for a long time. If this happens,
     * a live data refresh is triggered.
     */
    val liveDataTimeToLive: Duration = Duration.ofMinutes(1L)

    /**
     * The is the minimum duration between events rendered by the favourites view. This value ensures smooth rendering
     * of views.
     */
    val favouritesUiEventThrottling: Duration = Duration.ofMillis(500L)

    /**
     * The throttling duration applied to search query input.
     */
    val searchQueryInputThrottling: Duration = Duration.ofMillis(500L)

    /**
     * The max number of grouped live data items to display with each favourite location
     */
    const val favouritesLiveDataLimit: Int = 3

    /**
     * The max number of live data times to display within a live data group
     */
    const val favouritesGroupedLiveDataLimit: Int = 3

    /**
     * Search results are ranked by their accuracy in relation to the search query. This is a score out of 100 of which
     * search results will be filtered.
     */
    const val searchAccuracyScoreCutoff: Int = 50

    /**
     * The maximum number of search results to be displayed
     */
    const val maxSearchResults = 100

    /**
     * The maximum number of recent search results to be displayed
     */
    const val maxRecentSearches = 50

    /**
     * The maximum number of nearby locations to be displayed
     */
    const val maxNearbyLocations = 50
}
