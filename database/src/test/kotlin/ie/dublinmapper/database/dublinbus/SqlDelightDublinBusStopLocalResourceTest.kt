 package ie.dublinmapper.database.dublinbus
//
// import com.squareup.sqldelight.ColumnAdapter
// import com.squareup.sqldelight.EnumColumnAdapter
// import com.squareup.sqldelight.sqlite.driver.JdbcSqliteDriver
// import ie.dublinmapper.database.*
// import io.rtpi.api.Coordinate
// import io.rtpi.api.Operator
// import io.rtpi.api.Service
// import java.time.Instant
// import org.junit.Test
//
// class SqlDelightDublinBusStopLocalResourceTest {
//
//    private val database = Database(
//        driver = JdbcSqliteDriver(JdbcSqliteDriver.IN_MEMORY).apply {
//            Database.Schema.create(this)
//        },
//        aircoachStopServiceEntityAdapter = AircoachService.Adapter(
//            operatorAdapter = EnumColumnAdapter()
//        ),
//        busEireannStopServiceEntityAdapter = Buseireann_services.Adapter(
//            operatorAdapter = EnumColumnAdapter()
//        ),
//        dublinBikesDockServiceEntityAdapter = DublinBikesServiceEntity.Adapter(
//            operatorAdapter = EnumColumnAdapter()
//        ),
//        dublinBusStopServiceEntityAdapter = DublinBusServiceEntity.Adapter(
//            operatorAdapter = EnumColumnAdapter()
//        ),
//        irishRailStationServiceEntityAdapter = IrishRailServiceEntity.Adapter(
//            operatorAdapter = EnumColumnAdapter()
//        ),
//        luasStopServiceEntityAdapter = LuasServiceEntity.Adapter(
//            operatorAdapter = EnumColumnAdapter()
//        ),
//        favouriteServiceLocationEntityAdapter = FavouriteServiceLocationEntity.Adapter(
//            serviceAdapter = EnumColumnAdapter()
//        ),
//        serviceLocationRecordStateEntityAdapter = ServiceLocationRecordStateEntity.Adapter(
//            lastUpdatedAdapter = object : ColumnAdapter<Instant, String> {
//
//                override fun decode(databaseValue: String): Instant {
//                    return Instant.parse(databaseValue)
//                }
//
//                override fun encode(value: Instant): String {
//                    return value.toString()
//                }
//
//            }
//        )
//    )
//    private val dublinBusStopLocalResource =
//        SqlDelightDublinBusStopLocalResource(database)
//
//    @Test
//    fun `holy poop`() {
//        database.dublinBusStopLocationEntityQueries.insertOrReplace(
//            id = "769",
//            name = "Stillorgan Road",
//            latitude = 0.0,
//            longitude = 0.0
//        )
//        database.dublinBusStopLocationEntityQueries.insertOrReplace(
//            id = "315",
//            name = "Bachelor's Walk",
//            latitude = 0.0,
//            longitude = 0.0
//        )
//        database.dublinBusStopServiceEntityQueries.insertOrReplace(
//            locationId = "769",
//            operator = Operator.DUBLIN_BUS,
//            route = "46A"
//        )
//        database.dublinBusStopServiceEntityQueries.insertOrReplace(
//            locationId = "769",
//            operator = Operator.DUBLIN_BUS,
//            route = "145"
//        )
//        database.dublinBusStopServiceEntityQueries.insertOrReplace(
//            locationId = "769",
//            operator = Operator.GO_AHEAD,
//            route = "75"
//        )
//        database.dublinBusStopServiceEntityQueries.insertOrReplace(
//            locationId = "315",
//            operator = Operator.DUBLIN_BUS,
//            route = "7A"
//        )
//        database.favouriteServiceLocationEntityQueries.insertOrReplace(
//            id = "769",
//            service = Service.DUBLIN_BUS,
//            name = "hello"
//        )
//
//        val locations = database.dublinBusStopLocationEntityQueries.selectAll().executeAsList()
//        val services = database.dublinBusStopServiceEntityQueries.selectAll().executeAsList()
//        val favourites =
//            database.favouriteServiceLocationEntityQueries.selectAllByService(Service.DUBLIN_BUS)
//                .executeAsList()
//        val dublinBusStops = database.dublinBusStopLocationEntityQueries.selectAllTest()
//            .executeAsList()
//        val favouriteDublinBusStops =
//            database.dublinBusStopLocationEntityQueries.selectAllFavouritesTest().executeAsList()
//
//        print("")
//    }
//
//    @Test
//    fun `test dublin bus`() {
//
//
//        dublinBusStopLocalResource.insertStops(
//            listOf(
//                DublinBusStop(
//                    id = "315",
//                    name = "test",
//                    coordinate = Coordinate(latitude = 0.0, longitude = 0.0),
//                    operators = setOf(Operator.DUBLIN_BUS, Operator.GO_AHEAD),
//                    routes = emptyList()
//                )
//            )
//        )
//    }
//
// }
