package io.dublink.database

import com.squareup.sqldelight.EnumColumnAdapter
import com.squareup.sqldelight.sqlite.driver.JdbcSqliteDriver

private fun createDatabase() =
    Database(
        driver = JdbcSqliteDriver(JdbcSqliteDriver.IN_MEMORY).apply {
            Database.Schema.create(this)
        },
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

fun createFavouriteServiceLocationLocalResource(
    database: Database = createDatabase()
) = SqlDelightFavouriteServiceLocationLocalResource(
    database = database
)
