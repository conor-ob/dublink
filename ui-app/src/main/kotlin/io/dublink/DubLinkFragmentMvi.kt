package io.dublink

import com.ww.roxie.BaseAction
import com.ww.roxie.BaseState
import io.dublink.domain.internet.InternetStatus
import io.dublink.domain.util.AppConstants
import java.time.Duration
import java.time.Instant

sealed class DubLinkFragmentAction : BaseAction {
    object SubscribeToInternetStatusChanges : DubLinkFragmentAction()
}

sealed class DubLinkFragmentChange {
    data class InternetStatusChange(val internetStatusChange: InternetStatus) : DubLinkFragmentChange()
}

data class DubLinkFragmentState(
    val internetStatusChangeEvent: InternetStatusChangeEvent? = null
) : BaseState

data class InternetStatusChangeEvent(
    val internetStatusChange: InternetStatus,
    val timestamp: Instant
) {

    fun isRecent(): Boolean = Duration.between(timestamp, Instant.now()) < AppConstants.internetStatusChangeEventExpiry
}