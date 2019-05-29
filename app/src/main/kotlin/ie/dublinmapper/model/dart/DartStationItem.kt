package ie.dublinmapper.model.dart

import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import ie.dublinmapper.R
import ie.dublinmapper.domain.model.DartStation
import kotlinx.android.synthetic.main.list_item_service_location_dart.*

class DartStationItem(val dartStation: DartStation) : Item() {

    override fun getLayout() = R.layout.list_item_service_location_dart_prototype

    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.service_location_name.text = dartStation.name
    }

}
