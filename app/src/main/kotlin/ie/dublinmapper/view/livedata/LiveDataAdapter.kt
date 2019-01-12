package ie.dublinmapper.view.livedata

import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import ie.dublinmapper.R
import ie.dublinmapper.model.LiveDataUi
import ie.dublinmapper.util.Operator
import ie.dublinmapper.util.StringUtils
import java.util.*

class LiveDataAdapter : RecyclerView.Adapter<LiveDataAdapter.LiveDataViewHolder>() {

    var liveData = emptyList<LiveDataUi>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LiveDataViewHolder {
        return when (viewType) {
            1 -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.view_nearby_list_item_dart, parent, false)
                LiveDataViewHolder.Dart(view)
            }
            2 -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.view_nearby_list_item_dublin_bikes, parent, false)
                LiveDataViewHolder.DublinBikes(view)
            }
            3 -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.view_nearby_list_item_dublin_bus, parent, false)
                LiveDataViewHolder.DublinBus(view)
            }
            4 -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.view_nearby_list_item_luas, parent, false)
                LiveDataViewHolder.Luas(view)
            }
//            0 -> {
//                val view = LayoutInflater.from(parent.context)
//                    .inflate(R.layout.view_nearby_list_item_empty, parent, false)
//                LiveDataViewHolder.Empty(view)
//            }
            else -> throw IllegalStateException("Unknown view type")
        }
    }

    override fun getItemCount() = liveData.size

    override fun onBindViewHolder(holder: LiveDataViewHolder, position: Int) {
        holder.bind(liveData[position])
    }

    override fun getItemViewType(position: Int): Int {
        return when (liveData[position]) {
            is LiveDataUi.Dart -> 1
            is LiveDataUi.DublinBikes -> 2
            is LiveDataUi.DublinBus -> 3
            is LiveDataUi.Luas -> 4
        }
    }

    fun showLiveData(liveData: List<LiveDataUi>) {
        this.liveData = liveData
        notifyDataSetChanged()
    }

    sealed class LiveDataViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        abstract fun bind(liveData: LiveDataUi)

        class Dart(view: View) : LiveDataViewHolder(view) {

            private var trainType: TextView = view.findViewById(R.id.train_type)
            private var directionAndDestination: TextView = view.findViewById(R.id.direction_destination)
            private var due: TextView = view.findViewById(R.id.due)

            override fun bind(liveData: LiveDataUi) {
                val dartLiveData = liveData as LiveDataUi.Dart
                trainType.text = dartLiveData.liveData.operator.name
                when (dartLiveData.liveData.operator) {
                    Operator.DART -> trainType.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(itemView.context, R.color.dartGreen))
                    Operator.COMMUTER -> trainType.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(itemView.context, R.color.commuterBlue))
                    Operator.INTERCITY -> trainType.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(itemView.context, R.color.intercityGrey))
                    else -> {

                    }
                }
                directionAndDestination.text = StringUtils.join(
                    Arrays.asList(
                        dartLiveData.liveData.direction,
                        dartLiveData.liveData.destination
                    ), " ${StringUtils.MIDDLE_DOT} ")
                due.text = dartLiveData.liveData.dueTime.toString()
            }

        }

        class DublinBikes(view: View) : LiveDataViewHolder(view) {

            private val availableBikes = view.findViewById(R.id.available_bikes) as TextView
            private val availableDocks = view.findViewById(R.id.available_docks) as TextView

            override fun bind(liveData: LiveDataUi) {
                val dublinBikesLiveData = liveData as LiveDataUi.DublinBikes
                availableBikes.text = dublinBikesLiveData.liveData.bikes.toString()
                availableDocks.text = dublinBikesLiveData.liveData.docks.toString()
            }

        }

        class DublinBus(view: View) : LiveDataViewHolder(view) {

            private var routeId: TextView = view.findViewById(R.id.route_id)
            private var destination: TextView = view.findViewById(R.id.destination)
            private var due: TextView = view.findViewById(R.id.due)

            override fun bind(liveData: LiveDataUi) {
                val dublinBusLiveData = liveData as LiveDataUi.DublinBus
                routeId.text = dublinBusLiveData.liveData.route
                destination.text = dublinBusLiveData.liveData.destination
                due.text = dublinBusLiveData.liveData.dueTime.toString()
            }

        }

        class Luas(view: View) : LiveDataViewHolder(view) {

            private var operator: TextView = view.findViewById(R.id.operator)
            private var directionAndDestination: TextView = view.findViewById(R.id.direction_destination)
            private var due: TextView = view.findViewById(R.id.due)

            override fun bind(liveData: LiveDataUi) {
                val luasLiveData = liveData as LiveDataUi.Luas
                operator.text = luasLiveData.liveData.operator.name
                directionAndDestination.text = StringUtils.join(
                    Arrays.asList(
                        luasLiveData.liveData.route,
                        luasLiveData.liveData.destination
                    ), " ${StringUtils.MIDDLE_DOT} ")
                due.text = luasLiveData.liveData.dueTime.toString()
            }

        }

//        class Empty(view: View) : NearbyLiveDataViewHolder(view) {
//
//            private val message: TextView = view.findViewById(R.id.message)
//
//            override fun bind(liveData: LiveDataUi) {
//                val emptyLiveData = liveData as LiveData.Empty
//                message.text = emptyLiveData.message
//            }
//
//        }

    }

}
