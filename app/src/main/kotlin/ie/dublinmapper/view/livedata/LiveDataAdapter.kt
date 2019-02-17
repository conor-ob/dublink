package ie.dublinmapper.view.livedata

import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import ie.dublinmapper.R
import ie.dublinmapper.model.*
import ie.dublinmapper.util.Operator
import ie.dublinmapper.util.StringUtils
import java.util.*

class LiveDataAdapter : RecyclerView.Adapter<LiveDataAdapter.LiveDataViewHolder>() {

    var liveData = emptyList<LiveDataUi>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LiveDataViewHolder {
        return when (viewType) {
            1 -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.list_item_live_data_dart, parent, false)
                LiveDataViewHolder.Dart(view)
            }
            10 -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.list_item_live_data_dart_condensed, parent, false)
                LiveDataViewHolder.DartCondensed(view)
            }
            2 -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.list_item_live_data_dublin_bikes, parent, false)
                LiveDataViewHolder.DublinBikes(view)
            }
            3 -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.list_item_live_data_dublin_bus, parent, false)
                LiveDataViewHolder.DublinBus(view)
            }
            30 -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.list_item_live_data_dublin_bus_condensed, parent, false)
                LiveDataViewHolder.DublinBusCondensed(view)
            }
            4 -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.list_item_live_data_luas, parent, false)
                LiveDataViewHolder.Luas(view)
            }
            40 -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.list_item_live_data_luas_condensed, parent, false)
                LiveDataViewHolder.LuasCondensed(view)
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
        val data = liveData[position]
        return when (data) {
            is AircoachLiveDataUi -> 22
            is DartLiveDataUi -> {
                return if (data.liveData.dueTime.size == 1) {
                    1
                } else {
                    10
                }
            }
            is DublinBikesLiveDataUi -> 2
            is DublinBusLiveDataUi -> {
                return if (data.liveData.dueTime.size == 1) {
                    3
                } else {
                    30
                }
            }
            is LuasLiveDataUi -> {
                return if (data.liveData.dueTime.size == 1) {
                    4
                } else {
                    40
                }
            }
            else -> 1111
        }
    }

    fun showLiveData(liveData: List<LiveDataUi>) {
        this.liveData = liveData
        notifyDataSetChanged()
    }

    sealed class LiveDataViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        abstract fun bind(liveData: LiveDataUi)

        class Dart(private val view: View) : LiveDataViewHolder(view) {

            private var trainType: TextView = view.findViewById(R.id.train_type)
            private var directionAndDestination: TextView = view.findViewById(R.id.direction_destination)
            private var due: TextView = view.findViewById(R.id.due)

            override fun bind(liveData: LiveDataUi) {
                val dartLiveData = liveData as DartLiveDataUi
                trainType.text = dartLiveData.liveData.operator.shortName
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
                if (dartLiveData.liveData.dueTime[0].minutes == 0L) {
                    due.text = view.resources.getString(R.string.live_data_due)
                } else {
                    due.text = view.resources.getString(R.string.live_data_due_time, dartLiveData.liveData.dueTime[0].minutes)
                }
            }

        }

        class DartCondensed(private val view: View) : LiveDataViewHolder(view) {

            private var trainType: TextView = view.findViewById(R.id.train_type)
            private var directionAndDestination: TextView = view.findViewById(R.id.direction_destination)
            private var due: TextView = view.findViewById(R.id.due)
            private var dueLater: TextView = view.findViewById(R.id.due_later)

            override fun bind(liveData: LiveDataUi) {
                val dartLiveData = liveData as DartLiveDataUi
                trainType.text = dartLiveData.liveData.operator.shortName
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
                if (dartLiveData.liveData.dueTime[0].minutes == 0L) {
                    due.text = view.resources.getString(R.string.live_data_due)
                } else {
                    due.text = view.resources.getString(R.string.live_data_due_time, dartLiveData.liveData.dueTime[0].minutes)
                }
                dueLater.text = view.resources.getString(R.string.live_data_due_times, StringUtils.join(
                    dartLiveData.liveData.dueTime.subList(1, dartLiveData.liveData.dueTime.size).map { it.minutes.toString() }, ", "
                ))
            }

        }

        class DublinBikes(view: View) : LiveDataViewHolder(view) {

            private val availableBikes = view.findViewById(R.id.available_bikes) as TextView
            private val availableDocks = view.findViewById(R.id.available_docks) as TextView

            override fun bind(liveData: LiveDataUi) {
                val dublinBikesLiveData = liveData as DublinBikesLiveDataUi
                availableBikes.text = dublinBikesLiveData.liveData.bikes.toString()
                availableDocks.text = dublinBikesLiveData.liveData.docks.toString()
            }

        }

        class DublinBus(private val view: View) : LiveDataViewHolder(view) {

            private var routeId: TextView = view.findViewById(R.id.route_id)
            private var destination: TextView = view.findViewById(R.id.destination)
            private var due: TextView = view.findViewById(R.id.due)

            override fun bind(liveData: LiveDataUi) {
                val dublinBusLiveData = liveData as DublinBusLiveDataUi
                routeId.text = dublinBusLiveData.liveData.route
                destination.text = dublinBusLiveData.liveData.destination
                if (dublinBusLiveData.liveData.dueTime[0].minutes == 0L) {
                    due.text = view.resources.getString(R.string.live_data_due)
                } else {
                    due.text = view.resources.getString(R.string.live_data_due_time, dublinBusLiveData.liveData.dueTime[0].minutes)
                }
            }

        }

        class DublinBusCondensed(private val view: View) : LiveDataViewHolder(view) {

            private var routeId: TextView = view.findViewById(R.id.route_id)
            private var destination: TextView = view.findViewById(R.id.destination)
            private var due: TextView = view.findViewById(R.id.due)
            private var dueLater: TextView = view.findViewById(R.id.due_later)

            override fun bind(liveData: LiveDataUi) {
                val dublinBusLiveData = liveData as DublinBusLiveDataUi
                routeId.text = dublinBusLiveData.liveData.route
                destination.text = dublinBusLiveData.liveData.destination
                if (dublinBusLiveData.liveData.dueTime[0].minutes == 0L) {
                    due.text = view.resources.getString(R.string.live_data_due)
                } else {
                    due.text = view.resources.getString(R.string.live_data_due_time, dublinBusLiveData.liveData.dueTime[0].minutes)
                }
                dueLater.text = view.resources.getString(R.string.live_data_due_times, StringUtils.join(
                    dublinBusLiveData.liveData.dueTime.subList(1, dublinBusLiveData.liveData.dueTime.size).map { it.minutes.toString() }, ", "
                ))
            }

        }

        class Luas(private val view: View) : LiveDataViewHolder(view) {

            private var operator: TextView = view.findViewById(R.id.operator)
            private var directionAndDestination: TextView = view.findViewById(R.id.direction_destination)
            private var due: TextView = view.findViewById(R.id.due)

            override fun bind(liveData: LiveDataUi) {
                val luasLiveData = liveData as LuasLiveDataUi
                operator.text = luasLiveData.liveData.operator.name
                directionAndDestination.text = StringUtils.join(
                    Arrays.asList(
                        luasLiveData.liveData.route,
                        luasLiveData.liveData.destination
                    ), " ${StringUtils.MIDDLE_DOT} ")
                if (luasLiveData.liveData.dueTime[0].minutes == 0L) {
                    due.text = view.resources.getString(R.string.live_data_due)
                } else {
                    due.text = view.resources.getString(R.string.live_data_due_time, luasLiveData.liveData.dueTime[0].minutes)
                }
            }

        }

        class LuasCondensed(private val view: View) : LiveDataViewHolder(view) {

            private var operator: TextView = view.findViewById(R.id.operator)
            private var directionAndDestination: TextView = view.findViewById(R.id.direction_destination)
            private var due: TextView = view.findViewById(R.id.due)
            private var dueLater: TextView = view.findViewById(R.id.due_later)

            override fun bind(liveData: LiveDataUi) {
                val luasLiveData = liveData as LuasLiveDataUi
                operator.text = luasLiveData.liveData.operator.name
                directionAndDestination.text = StringUtils.join(
                    Arrays.asList(
                        luasLiveData.liveData.route,
                        luasLiveData.liveData.destination
                    ), " ${StringUtils.MIDDLE_DOT} ")
                if (luasLiveData.liveData.dueTime[0].minutes == 0L) {
                    due.text = view.resources.getString(R.string.live_data_due)
                } else {
                    due.text = view.resources.getString(R.string.live_data_due_time, luasLiveData.liveData.dueTime[0].minutes)
                }
                dueLater.text = view.resources.getString(R.string.live_data_due_times, StringUtils.join(
                    luasLiveData.liveData.dueTime.subList(1, luasLiveData.liveData.dueTime.size).map { it.minutes.toString() }, ", "
                ))
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
