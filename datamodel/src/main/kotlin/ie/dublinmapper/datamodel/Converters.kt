package ie.dublinmapper.datamodel

import androidx.room.TypeConverter
import ie.dublinmapper.datamodel.favourite.FavouriteKey
import io.rtpi.api.Operator
import io.rtpi.api.Service
import org.threeten.bp.Instant
import java.util.UUID

object Converters {

    @TypeConverter
    @JvmStatic
    fun fromInstant(instant: Instant): Long {
        return instant.epochSecond
    }

    @TypeConverter
    @JvmStatic
    fun toInstant(value: Long): Instant {
        return Instant.ofEpochSecond(value)
    }

    @TypeConverter
    @JvmStatic
    fun fromUuid(uuid: UUID): String {
        return uuid.toString()
    }

    @TypeConverter
    @JvmStatic
    fun toUuid(value: String): UUID {
        return UUID.fromString(value)
    }

    @TypeConverter
    @JvmStatic
    fun fromService(service: Service): String {
        return service.name
    }

    @TypeConverter
    @JvmStatic
    fun toService(value: String): Service {
        return Service.parse(value)
    }

    @TypeConverter
    @JvmStatic
    fun fromOperator(operator: Operator): String {
        return operator.name
    }

    @TypeConverter
    @JvmStatic
    fun toOperator(value: String): Operator {
        return Operator.parse(value)
    }

    @TypeConverter
    @JvmStatic
    fun fromFavouriteKey(favouriteKey: FavouriteKey): String {
        return "${favouriteKey.service.name}::${favouriteKey.serviceId}"
    }

    @TypeConverter
    @JvmStatic
    fun toFavouriteKey(value: String): FavouriteKey {
        val parts = value.split("::")
        return FavouriteKey(parts[1], Service.parse(parts[0]))
    }

}
