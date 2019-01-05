package ie.dublinmapper.view.nearby

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ie.dublinmapper.domain.dublinbikes.DublinBikesDock
import ie.dublinmapper.domain.usecase.NearbyUseCase
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class NearbyViewModel @Inject constructor(
    private val nearbyUseCase: NearbyUseCase
//    private val schedulers:
) : ViewModel() {

    private lateinit var nearbyServiceLocations: MutableLiveData<List<DublinBikesDock>>

    fun getNearbyServiceLocations(): LiveData<List<DublinBikesDock>> {
        if (!::nearbyServiceLocations.isInitialized) {
            nearbyServiceLocations = MutableLiveData()
            nearbyUseCase.getNearbyServiceLocations()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext { nearbyServiceLocations.value = it }
                .doOnError {  }
                .subscribe()
        }
        return nearbyServiceLocations
    }

}
