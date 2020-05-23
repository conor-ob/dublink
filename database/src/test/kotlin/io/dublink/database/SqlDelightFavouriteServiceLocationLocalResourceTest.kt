package io.dublink.database

import com.google.common.truth.Truth.assertThat
import io.dublink.domain.model.DubLinkStopLocation
import io.dublink.domain.model.FavouriteMetadata
import io.dublink.domain.model.setCustomName
import io.rtpi.api.Coordinate
import io.rtpi.api.Service
import io.rtpi.api.StopLocation
import org.junit.Test

class SqlDelightFavouriteServiceLocationLocalResourceTest {

    @Test
    fun `inserting a new favourite should set the sort index to 0`() {
        // arrange
        val favouritesResource = createFavouriteServiceLocationLocalResource()
        val irishRailStation = DubLinkStopLocation(
            stopLocation = StopLocation(
                id = "123",
                name = "foo",
                service = Service.IRISH_RAIL,
                coordinate = Coordinate(0.0, 0.0),
                routeGroups = emptyList()
            )
        )

        // act
        val updatedIrishRailStation = irishRailStation.setCustomName("My favourite stop")
        favouritesResource.saveFavourites(
            listOf(
                updatedIrishRailStation
            ),
            overwrite = false
        )
        val favourites = favouritesResource.getFavourites().blockingFirst()

        // assert
        assertThat(favourites).containsExactly(
            DubLinkStopLocation(
                stopLocation = StopLocation(
                    id = "123",
                    name = "My favourite stop",
                    service = Service.IRISH_RAIL,
                    coordinate = Coordinate(0.0, 0.0),
                    routeGroups = emptyList()
                ),
                favouriteMetadata = FavouriteMetadata(
                    isFavourite = true,
                    name = "My favourite stop",
                    sortIndex = 0
                )
            )
        )
    }

    @Test
    fun `editing and saving an existing favourite should preserve it's sort order`() {
        // arrange
        val favouritesResource = createFavouriteServiceLocationLocalResource()
        favouritesResource.saveFavourites(
            listOf(
                DubLinkStopLocation(
                    stopLocation = StopLocation(
                        id = "1",
                        name = "stop 1",
                        service = Service.IRISH_RAIL,
                        coordinate = Coordinate(0.0, 0.0),
                        routeGroups = emptyList()
                    ),
                    favouriteMetadata = FavouriteMetadata(
                        isFavourite = false,
                        name = "favourite stop 1",
                        sortIndex = 0
                    )
                ),
                DubLinkStopLocation(
                    stopLocation = StopLocation(
                        id = "2",
                        name = "stop 2",
                        service = Service.DUBLIN_BUS,
                        coordinate = Coordinate(0.0, 0.0),
                        routeGroups = emptyList()
                    ),
                    favouriteMetadata = FavouriteMetadata(
                        isFavourite = false,
                        name = "favourite stop 2",
                        sortIndex = 1
                    )
                ),
                DubLinkStopLocation(
                    stopLocation = StopLocation(
                        id = "3",
                        name = "stop 3",
                        service = Service.LUAS,
                        coordinate = Coordinate(0.0, 0.0),
                        routeGroups = emptyList()
                    ),
                    favouriteMetadata = FavouriteMetadata(
                        isFavourite = false,
                        name = "favourite stop 3",
                        sortIndex = 2
                    )
                )
            ),
            overwrite = false
        )

        // act
        favouritesResource.saveFavourites(
            listOf(
                DubLinkStopLocation(
                    stopLocation = StopLocation(
                        id = "2",
                        name = "stop 2",
                        service = Service.DUBLIN_BUS,
                        coordinate = Coordinate(0.0, 0.0),
                        routeGroups = emptyList()
                    ),
                    favouriteMetadata = FavouriteMetadata(
                        isFavourite = false,
                        name = "updated favourite stop 2",
                        sortIndex = 1
                    )
                )
            ),
            overwrite = false
        )
        val favourites = favouritesResource.getFavourites().blockingFirst()

        // assert
        assertThat(favourites).containsExactly(
            DubLinkStopLocation(
                stopLocation = StopLocation(
                    id = "1",
                    name = "favourite stop 1",
                    service = Service.IRISH_RAIL,
                    coordinate = Coordinate(0.0, 0.0),
                    routeGroups = emptyList()
                ),
                favouriteMetadata = FavouriteMetadata(
                    isFavourite = true,
                    name = "favourite stop 1",
                    sortIndex = 0
                )
            ),
            DubLinkStopLocation(
                stopLocation = StopLocation(
                    id = "2",
                    name = "updated favourite stop 2",
                    service = Service.DUBLIN_BUS,
                    coordinate = Coordinate(0.0, 0.0),
                    routeGroups = emptyList()
                ),
                favouriteMetadata = FavouriteMetadata(
                    isFavourite = true,
                    name = "updated favourite stop 2",
                    sortIndex = 1
                )
            ),
            DubLinkStopLocation(
                stopLocation = StopLocation(
                    id = "3",
                    name = "favourite stop 3",
                    service = Service.LUAS,
                    coordinate = Coordinate(0.0, 0.0),
                    routeGroups = emptyList()
                ),
                favouriteMetadata = FavouriteMetadata(
                    isFavourite = true,
                    name = "favourite stop 3",
                    sortIndex = 2
                )
            )
        )
    }
}
