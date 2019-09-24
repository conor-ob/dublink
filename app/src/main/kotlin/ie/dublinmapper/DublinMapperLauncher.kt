package ie.dublinmapper

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class DublinMapperLauncher : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        startActivity(Intent(this, DublinMapperActivity::class.java))
        finish()
    }

}
