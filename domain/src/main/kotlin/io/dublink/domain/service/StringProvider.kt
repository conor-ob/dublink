package io.dublink.domain.service

import io.rtpi.api.Service

interface StringProvider {

    fun loadingMessage(): String

    fun serviceErrorMessage(service: Service?, throwable: Throwable): String

    fun noArrivalsMessage(service: Service?): String
}
