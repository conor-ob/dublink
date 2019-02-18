package ie.dublinmapper.domain.repository

interface Mapper<in F, out T> {

    fun map(from: F): T

    fun map(from: List<F>): List<T> {
        return from.map { map(it) }
    }

    fun mapOneToMany(from: F): List<T> {
        TODO()
    }

}
