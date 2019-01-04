package ie.dublinmapper.view.nearby

import android.arch.lifecycle.ViewModel
import ie.dublinmapper.domain.usecase.NearbyUseCase
import javax.inject.Inject

class NearbyViewModel @Inject constructor(
    private val nearbyUseCase: NearbyUseCase
) : ViewModel() {

}
