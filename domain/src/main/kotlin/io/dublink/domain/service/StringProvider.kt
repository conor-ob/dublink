package io.dublink.domain.service

import io.rtpi.api.Service

interface StringProvider {

    fun jcDecauxApiKey(): String

    fun databaseName(): String

    fun noArrivalsMessage(service: Service?): String
}
