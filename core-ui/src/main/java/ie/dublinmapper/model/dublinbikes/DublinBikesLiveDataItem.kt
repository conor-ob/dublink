package ie.dublinmapper.model.dublinbikes

//import com.xwray.groupie.Item
//import com.xwray.groupie.kotlinandroidextensions.ViewHolder
//import ie.dublinmapper.model.LiveDataItem
//import io.rtpi.api.DublinBikesLiveData
//
//class DublinBikesLiveDataItem(
//    private val liveData1: DublinBikesLiveData
//) : LiveDataItem(isEven, isLast) {
//
//    override fun bind(viewHolder: ViewHolder, position: Int) {
//        if (isBike) {
////            viewHolder.title.text = "Bikes"
////            viewHolder.subtitle.text = liveData.bikes.toString()
//        } else {
////            viewHolder.title.text = "Docks"
////            viewHolder.subtitle.text = liveData.docks.toString()
//        }
////        viewHolder.operatorIconContainer.setImageResource(R.drawable.ic_bike)
////        viewHolder.operatorIconContainer.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(viewHolder.itemView.context, R.color.dublinBikesTeal))
//    }
//
//    override fun isSameAs(other: Item<*>?): Boolean {
//        if (other is DublinBikesLiveDataItem) {
//            return liveData1.operator == other.liveData1.operator &&
//                    liveData1.bikes == other.liveData1.bikes &&
//                    liveData1.docks == other.liveData1.docks
//        }
//        return false
//    }
//
//    override fun equals(other: Any?): Boolean {
//        if (other is DublinBikesLiveDataItem) {
//            return liveData == other.liveData
//        }
//        return false
//    }
//
//    override fun hashCode(): Int {
//        return liveData.hashCode()
//    }
//
//}
