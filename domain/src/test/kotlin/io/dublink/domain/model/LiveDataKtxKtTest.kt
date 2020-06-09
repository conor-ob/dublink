package io.dublink.domain.model

import com.google.common.truth.Truth.assertThat
import io.rtpi.api.Operator
import io.rtpi.api.Prediction
import io.rtpi.api.PredictionLiveData
import io.rtpi.api.RouteInfo
import io.rtpi.api.Service
import java.time.Duration
import java.time.ZonedDateTime
import org.junit.Test

class LiveDataKtxKtTest {

    @Test
    fun `predictions less than 1 minute a away should be on time`() {
        // arrange
        val liveData = PredictionLiveData(
            service = Service.IRISH_RAIL,
            operator = Operator.DART,
            routeInfo = RouteInfo(
                route = Operator.DART.fullName,
                direction = "Northbound",
                origin = "Greystones",
                destination = "Malahide"
            ),
            prediction = Prediction(
                waitTime = Duration.ZERO,
                currentDateTime = ZonedDateTime.now(),
                scheduledDateTime = ZonedDateTime.now().plusMinutes(3),
                expectedDateTime = ZonedDateTime.now().plusMinutes(3).plusSeconds(47)
            )
        )

        // act
        val onTime = liveData.isOnTime()

        // assert
        assertThat(onTime).isTrue()
    }
}
