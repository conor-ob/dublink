package ie.dublinmapper.view.favourite

import android.content.Context
import com.hannesdorfmann.mosby3.mvp.MvpBasePresenter
import javax.inject.Inject

class FavouritesPresenterImpl @Inject constructor(
    private val context: Context
) : MvpBasePresenter<FavouritesView>(), FavouritesPresenter {

}
