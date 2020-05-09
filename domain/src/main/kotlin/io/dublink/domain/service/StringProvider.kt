package io.dublink.domain.service

import io.rtpi.api.Service

interface StringProvider {

    fun noArrivalsMessage(service: Service?): String
}
