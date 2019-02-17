package ie.dublinmapper

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bluelinelabs.conductor.Conductor
import com.bluelinelabs.conductor.Router
import com.bluelinelabs.conductor.RouterTransaction
import ie.dublinmapper.view.nearby.HomeController
import kotlinx.android.synthetic.main.activity_root.*

class DublinMapperActivity : AppCompatActivity() {

    private lateinit var router: Router

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_root)
        setupRouter(savedInstanceState)
    }

    private fun setupRouter(savedInstanceState: Bundle?) {
        router = Conductor.attachRouter(this, activity_root, savedInstanceState)
        if (!router.hasRootController()) {
            router.setRoot(RouterTransaction.with(HomeController(Bundle.EMPTY)))
        }
    }

    override fun onBackPressed() {
        if (!router.handleBack()) {
            super.onBackPressed()
        }
    }

}
