package ie.dublinmapper.database

import android.content.Context
import com.squareup.sqldelight.ColumnAdapter
import com.squareup.sqldelight.EnumColumnAdapter
import com.squareup.sqldelight.android.AndroidSqliteDriver
import dagger.Module
import dagger.Provides
import ie.dublinmapper.domain.datamodel.*
import ie.dublinmapper.domain.service.StringProvider
import java.time.Instant
import javax.inject.Singleton

@Module
class DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(context: Context, stringProvider: StringProvider): Database {
        return Database(
            driver = AndroidSqliteDriver(
                schema = Database.Schema,
                context = context,
                name = stringProvider.databaseName()
            ),
            aircoachServicesAdapter = AircoachServices.Adapter(
                operatorAdapter = EnumColumnAdapter()
            ),
            busEireannServicesAdapter = BusEireannServices.Adapter(
                operatorAdapter = EnumColumnAdapter()
            ),
            dublinBusServicesAdapter = DublinBusServices.Adapter(
                operatorAdapter = EnumColumnAdapter()
            ),
            irishRailServicesAdapter = IrishRailServices.Adapter(
                operatorAdapter = EnumColumnAdapter()
            ),
            luasServicesAdapter = LuasServices.Adapter(
                operatorAdapter = EnumColumnAdapter()
            ),
            favouriteLocationsAdapter = FavouriteLocations.Adapter(
                serviceAdapter = EnumColumnAdapter()
            ),
            locationExpirationsAdapter = LocationExpirations.Adapter(
                serviceAdapter = EnumColumnAdapter(),
                lastUpdatedAdapter = object : ColumnAdapter<Instant, String> {

                    override fun decode(databaseValue: String): Instant {
                        return Instant.parse(databaseValue)
                    }

                    override fun encode(value: Instant): String {
                        return value.toString()
                    }

                }
            ),
            recentSearchesAdapter = RecentSearches.Adapter(
                serviceAdapter = EnumColumnAdapter(),
                timestampAdapter = object : ColumnAdapter<Instant, String> {

                    override fun decode(databaseValue: String): Instant {
                        return Instant.parse(databaseValue)
                    }

                    override fun encode(value: Instant): String {
                        return value.toString()
                    }
                }
            )
        )
    }

    @Provides
    @Singleton
    fun favouriteCacheResource(database: Database): FavouriteServiceLocationLocalResource {
        return SqlDelightFavouriteServiceLocationLocalResource(database)
    }

    @Provides
    @Singleton
    fun serviceLocationLocalResource(database: Database): ServiceLocationLocalResource {
        return SqlDelightServiceLocationLocalResource(database)
    }

    @Provides
    @Singleton
    fun serviceLocationRecordStateLocalResource(database: Database): ServiceLocationRecordStateLocalResource {
        return SqlDelightServiceLocationRecordStateLocalResource(database)
    }

    @Provides
    @Singleton
    fun recentServiceLocationSearchLocalResource(database: Database): RecentServiceLocationSearchLocalResource {
        return SqlDelightRecentServiceLocationSearchLocalResource(database)
    }
}
