package ie.dublinmapper.database

import android.content.Context
import com.squareup.sqldelight.ColumnAdapter
import com.squareup.sqldelight.EnumColumnAdapter
import com.squareup.sqldelight.android.AndroidSqliteDriver
import dagger.Module
import dagger.Provides
import ie.dublinmapper.domain.datamodel.FavouriteServiceLocationLocalResource
import ie.dublinmapper.domain.datamodel.RecentServiceLocationSearchLocalResource
import ie.dublinmapper.domain.datamodel.ServiceLocationLocalResource
import ie.dublinmapper.domain.datamodel.ServiceLocationRecordStateLocalResource
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
            aircoachServiceEntityAdapter = AircoachServiceEntity.Adapter(
                operatorAdapter = EnumColumnAdapter()
            ),
            busEireannServiceEntityAdapter = BusEireannServiceEntity.Adapter(
                operatorAdapter = EnumColumnAdapter()
            ),
            dublinBusServiceEntityAdapter = DublinBusServiceEntity.Adapter(
                operatorAdapter = EnumColumnAdapter()
            ),
            irishRailServiceEntityAdapter = IrishRailServiceEntity.Adapter(
                operatorAdapter = EnumColumnAdapter()
            ),
            luasServiceEntityAdapter = LuasServiceEntity.Adapter(
                operatorAdapter = EnumColumnAdapter()
            ),
            favouriteLocationEntityAdapter = FavouriteLocationEntity.Adapter(
                serviceAdapter = EnumColumnAdapter()
            ),
            locationExpirationEntityAdapter = LocationExpirationEntity.Adapter(
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
            recentSearchEntityAdapter = RecentSearchEntity.Adapter(
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
