package ie.dublinmapper.domain.usecase

import ie.dublinmapper.domain.dublinbikes.DublinBikesDock
import ie.dublinmapper.domain.repository.Repository
import javax.inject.Inject

class NearbyUseCase @Inject constructor(
    private val dublinBikesDockRepository: Repository<DublinBikesDock>
) {
}