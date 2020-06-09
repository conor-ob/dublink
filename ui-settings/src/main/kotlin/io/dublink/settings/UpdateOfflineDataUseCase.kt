package io.dublink.settings

import io.dublink.domain.repository.AggregatedServiceLocationRepository
import javax.inject.Inject

class UpdateOfflineDataUseCase @Inject constructor(
    private val serviceLocationRepository: AggregatedServiceLocationRepository
) {

    fun update() {
        serviceLocationRepository.get(refresh = true)
    }
}