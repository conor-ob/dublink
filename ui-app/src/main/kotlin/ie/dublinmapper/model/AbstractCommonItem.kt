package ie.dublinmapper.model

import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import com.xwray.groupie.kotlinandroidextensions.Item

abstract class AbstractCommonItem(id: Long) : Item(id) {

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        // nothing to do
    }

    override fun isSameAs(other: com.xwray.groupie.Item<*>) =
        if (javaClass == other.javaClass) {
            other.id == id
        } else {
            false
        }

    override fun equals(other: Any?) = javaClass == other?.javaClass

    override fun hashCode() = javaClass.simpleName.hashCode()
}
