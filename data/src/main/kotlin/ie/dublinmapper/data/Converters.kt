package ie.dublinmapper.data

import androidx.room.TypeConverter
import org.threeten.bp.Instant
import java.util.*

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

}
