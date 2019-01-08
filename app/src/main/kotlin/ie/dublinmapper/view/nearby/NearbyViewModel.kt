package ie.dublinmapper.view.nearby

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ie.dublinmapper.domain.model.ServiceLocation
import ie.dublinmapper.domain.usecase.NearbyUseCase
import ie.dublinmapper.util.Coordinate
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class NearbyViewModel @Inject constructor(
    private val nearbyUseCase: NearbyUseCase
) : ViewModel() {

    val nearbyServiceLocations = MutableLiveData<Collection<ServiceLocation>>()

    fun onCameraMoved(coordinate: Coordinate) {
        nearbyUseCase.getNearbyServiceLocations(coordinate)
            .debounce(100L, TimeUnit.MILLISECONDS)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnNext { nearbyServiceLocations.value = it }
            .doOnError {  }
            .subscribe()
    }

}
