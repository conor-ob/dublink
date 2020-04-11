package ie.dublinmapper.domain.model

import io.rtpi.api.Service
import java.time.Instant

data class RecentServiceLocationSearch(
    val service: Service,
    val locationId: String,
    val timestamp: Instant
)
