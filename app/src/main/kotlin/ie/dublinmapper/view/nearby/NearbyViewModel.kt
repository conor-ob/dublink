package ie.dublinmapper.view.nearby

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ie.dublinmapper.domain.model.ServiceLocation
import ie.dublinmapper.domain.usecase.NearbyUseCase
import ie.dublinmapper.util.Coordinate
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class NearbyViewModel @Inject constructor(
    private val nearbyUseCase: NearbyUseCase
) : ViewModel() {

    val nearbyServiceLocations = MutableLiveData<List<ServiceLocation>>()

    fun onCameraMoved(coordinate: Coordinate) {
        nearbyUseCase.getNearbyServiceLocations(coordinate)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnNext { nearbyServiceLocations.value = it.values.toList() }
            .doOnError {  }
            .subscribe()
    }

}
