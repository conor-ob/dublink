package io.dublink.database

import com.squareup.sqldelight.ColumnAdapter
import java.time.Instant

class InstantColumnAdapter : ColumnAdapter<Instant, String> {

    override fun decode(databaseValue: String): Instant {
        return Instant.parse(databaseValue)
    }

    override fun encode(value: Instant): String {
        return value.toString()
    }
}
