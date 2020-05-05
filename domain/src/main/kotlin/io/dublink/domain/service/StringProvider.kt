package io.dublink.domain.service

interface StringProvider {

    fun jcDecauxApiKey(): String

    fun databaseName(): String
}
