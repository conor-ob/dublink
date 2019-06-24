package ie.dublinmapper

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bluelinelabs.conductor.Conductor
import com.bluelinelabs.conductor.Router
import com.bluelinelabs.conductor.RouterTransaction
import ie.dublinmapper.domain.usecase.PreloadUseCase
import ie.dublinmapper.util.Service
import ie.dublinmapper.util.getApplicationComponent
import ie.dublinmapper.view.favourite.FavouritesController
import ie.dublinmapper.view.livedata.LiveDataController
import ie.dublinmapper.view.search.SearchController
import kotlinx.android.synthetic.main.activity_root.*

class DublinMapperActivity : AppCompatActivity() {

    private lateinit var router: Router
    private lateinit var preloadUseCase: PreloadUseCase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_root)
        setupRouter(savedInstanceState)
        preloadData()
    }

    private fun preloadData() {
        preloadUseCase = getApplicationComponent().preloadUseCase()
        preloadUseCase.start()
    }

    private fun setupRouter(savedInstanceState: Bundle?) {
        router = Conductor.attachRouter(this, activity_root, savedInstanceState)
        if (!router.hasRootController()) {
//            val liveDataController = LiveDataController.Builder(
//                serviceLocationId = "CNLLY",
//                serviceLocationName = "Dublin Connolly",
//                serviceLocationService = Service.IRISH_RAIL,
//                serviceLocationStyleId = R.style.DartTheme,
//                serviceLocationIsFavourite = false
//            ).build()
//            router.setRoot(RouterTransaction.with(liveDataController))
//            router.setRoot(RouterTransaction.with(SearchController.Builder(R.style.SearchTheme).build()))
            router.setRoot(RouterTransaction.with(FavouritesController.Builder(R.style.FavouritesTheme).build()))
        }
    }

    override fun onDestroy() {
        preloadUseCase.stop()
        super.onDestroy()
    }

    override fun onBackPressed() {
        if (!router.handleBack()) {
            super.onBackPressed()
        }
    }

}
