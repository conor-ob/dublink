package io.dublink.domain.service

interface DubLinkProService {

    fun isFreeTrialRunning(): Boolean

    fun grantDubLinkProAccess()

    fun grantDubLinkProTrial()

    fun grantDubLinkProPreferences()

    fun revokeDubLinkProPreferences()

    fun revokeDubLinkProTrial()
}
