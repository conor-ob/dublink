package io.dublink.domain.service

import io.dublink.domain.repository.ServiceLocationResponse
import io.rtpi.api.Service

interface StringProvider {

    fun loadingMessage(): String

    fun errorMessage(service: Service?, throwable: Throwable): String

    fun errorMessage(errorResponses: List<ServiceLocationResponse.Error>): String

    fun noArrivalsMessage(service: Service?): String
}
