package io.dublink.database.irishrail
//
// import com.squareup.sqldelight.ColumnAdapter
// import com.squareup.sqldelight.EnumColumnAdapter
// import com.squareup.sqldelight.sqlite.driver.JdbcSqliteDriver
// import io.dublink.database.*
// import io.rtpi.api.Coordinate
// import io.rtpi.api.IrishRailStation
// import io.rtpi.api.Operator
// import org.junit.Test
// import java.time.Instant
//
// class SqlDelightIrishRailStationLocalResourceTest {
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
//    private val irishRailStationLocalResource =
//        SqlDelightIrishRailStationLocalResource(database)
//
//    @Test
//    fun `test irish rail`() {
//        // arrange
//        irishRailStationLocalResource.insertStations(
//            listOf(
//                IrishRailStation(
//                    id = "TARA",
//                    name = "Tara Street",
//                    coordinate = Coordinate(latitude = 0.0, longitude = 0.0),
//                    operators = setOf(Operator.DART, Operator.COMMUTER, Operator.INTERCITY),
//                    routes = emptyList()
//                )
//            )
//        )
//
//        irishRailStationLocalResource.selectStations()
//            .test()
//            .assertResult(
//                listOf(
//                    IrishRailStation(
//                        id = "TARA",
//                        name = "Tara Street",
//                        coordinate = Coordinate(0.0, 0.0),
//                        operators = setOf(Operator.DART, Operator.COMMUTER, Operator.INTERCITY),
//                        routes = emptyList()
//                    )
//                )
//            )
//    }
//
// }
