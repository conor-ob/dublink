package io.dublink.database

import android.content.Context
import com.squareup.sqldelight.EnumColumnAdapter
import com.squareup.sqldelight.android.AndroidSqliteDriver
import dagger.Module
import dagger.Provides
import io.dublink.domain.datamodel.FavouriteServiceLocationLocalResource
import io.dublink.domain.datamodel.RecentServiceLocationSearchLocalResource
import io.dublink.domain.datamodel.ServiceLocationLocalResource
import io.dublink.domain.datamodel.ServiceLocationRecordStateLocalResource
import io.dublink.domain.service.StringProvider
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
            favouriteServiceEntityAdapter = FavouriteServiceEntity.Adapter(
                serviceAdapter = EnumColumnAdapter(),
                operatorAdapter = EnumColumnAdapter()
            ),
            favouriteDirectionEntityAdapter = FavouriteDirectionEntity.Adapter(
                serviceAdapter = EnumColumnAdapter()
            ),
            locationExpirationEntityAdapter = LocationExpirationEntity.Adapter(
                serviceAdapter = EnumColumnAdapter(),
                lastUpdatedAdapter = InstantColumnAdapter()
            ),
            recentSearchEntityAdapter = RecentSearchEntity.Adapter(
                serviceAdapter = EnumColumnAdapter(),
                timestampAdapter = InstantColumnAdapter()
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
